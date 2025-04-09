// Kotlin
package com.example.gameglish.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gameglish.data.database.GameGlishDatabase
import com.example.gameglish.data.repository.RepositoryUsuario
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class LoginState { Idle, Loading, Success, Error, FirstLogin }

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val db: GameGlishDatabase = GameGlishDatabase.getDatabase(application)
    private val usuarioRepository = RepositoryUsuario(db)
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _loginState = MutableStateFlow(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    private val _isUserLoggedIn = MutableStateFlow(auth.currentUser != null)
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn

    private val _isFirstLogin = MutableStateFlow<Boolean?>(null)
    val isFirstLogin: StateFlow<Boolean?> = _isFirstLogin



    init {
        checkUserState()
    }




    private fun checkUserState() {
        viewModelScope.launch {
            val uid = auth.currentUser?.uid
            _isUserLoggedIn.value = uid != null
            if(uid != null){
                var localUser = usuarioRepository.obtenerUsuarioLocal(uid)
                if(localUser != null){
                    localUser = usuarioRepository.obtenerUsuarioRemoto(uid)
                    if(localUser != null){
                        usuarioRepository.guardarUsuarioLocal(localUser)
                    }
                }
                _isFirstLogin.value = localUser?.firstLogin ?: false
            } else {
                _isFirstLogin.value = false
            }
        }
    }

    fun iniciarSesion(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val exito = usuarioRepository.iniciarSesionCorreo(email, password)
                if (exito) {
                    checkUserState() // Verifica el estado después del inicio de sesión
                    _loginState.value = LoginState.Success
                } else {
                    _loginState.value = LoginState.Error
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Error during login", e)
                _loginState.value = LoginState.Error
            }
        }
    }

    // Added default parameter values so that RegisterScreen does not need to pass name and level.
    fun registrarUsuario(email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val exito = usuarioRepository.registrarUsuarioCorreo(email, password, confirmPassword)
                if (exito) {
                    checkUserState()
                    _loginState.value = LoginState.Success
                } else {
                    _loginState.value = LoginState.Error
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Error during registration", e)
                _loginState.value = LoginState.Error
            }
        }
    }

    /**
     * Marca que el usuario ya completó el primer inicio de sesión y actualiza la base de datos.
     */
    fun marcarPrimerLoginCompleto() {
        viewModelScope.launch {
            val uid = auth.currentUser?.uid
            if (uid != null) {
                usuarioRepository.marcarPrimerLoginCompleto(uid)
                _isFirstLogin.value = false
            }
        }
    }

    fun actualizarNombreUsuario(uid: String, nombre: String, nivel: String) {
        viewModelScope.launch {
            usuarioRepository.actualizarUsuarioProfile(uid, nombre, nivel)
        }
    }


    fun resetState() {
        _loginState.value = LoginState.Idle
    }

    fun cerrarSesion() {
        auth.signOut()
        _isUserLoggedIn.value = false
    }
}