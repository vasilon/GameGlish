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

    private val _loginState = MutableStateFlow(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun iniciarSesion(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val success = usuarioRepository.iniciarSesionCorreo(email, password)
                if (success) {
                    // Obtain the user's profile from the local DB.
                    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                    val usuario = usuarioRepository.obtenerUsuarioLocal(uid)
                    // If the user record is missing or their name is empty, assume it is their first login.
                    _loginState.value = if (usuario == null || usuario.nombre.isEmpty()) {
                        LoginState.FirstLogin
                    } else {
                        LoginState.Success
                    }
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
    fun registrarUsuario(
        email: String,
        password: String,
        confirmPassword: String,
        nombre: String = "",
        nivelSeleccionado: String = "A1"
    ) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            Log.d("LoginViewModel", "Attempting to register user: $email")
            try {
                val exito = usuarioRepository.registrarUsuarioCorreo(
                    email, password, confirmPassword, nombre, nivelSeleccionado
                )
                Log.d("LoginViewModel", "Registration success: $exito")
                _loginState.value = if (exito) LoginState.Success else LoginState.Error
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Error during registration", e)
                _loginState.value = LoginState.Error
            }
        }
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}