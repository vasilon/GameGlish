// Kotlin
package com.example.gameglish.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gameglish.data.database.GameGlishDatabase
import com.example.gameglish.data.model.EntityUsuario
import com.example.gameglish.data.repository.RepositoryUsuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

enum class LoginState {
    Idle,
    Loading,
    Success,
    Error,
    PasswordResetSent          // ← NUEVO
}

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

    fun enviarCorreoReset(email: String) = viewModelScope.launch {
        _loginState.value = LoginState.Loading
        try {
            auth.sendPasswordResetEmail(email).await()
            _loginState.value = LoginState.PasswordResetSent
        } catch (e: FirebaseAuthException) {
            Log.e("LoginViewModel", "Error envío reset", e)
            _loginState.value = LoginState.Error
        }
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

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                // 1) Autenticar en Firebase
                val authResult = usuarioRepository.signInWithGoogle(idToken)

                // 2) Si es usuario nuevo: créalo en Room y en la base remota
                if (authResult.additionalUserInfo?.isNewUser == true) {
                    val user = authResult.user!!          // ahora sí existe
                    val newUsuario = EntityUsuario(
                        uidFirebase = user.uid,
                        email       = user.email.orEmpty(),
                        nombre      = "",
                        puntos      = 0,
                        nivel       = 0,                  // <-- ponlo si tu data-class lo exige
                        firstLogin  = true
                    )
                    usuarioRepository.guardarUsuarioLocal(newUsuario)
                    usuarioRepository.guardarUsuarioRemoto(newUsuario)
                }




                // 4) Ahora sí obtén el flag firstLogin de forma secuencial
                val uid = auth.currentUser?.uid
                val local = uid?.let { usuarioRepository.obtenerUsuarioLocal(it) }
                val firstLogin = local?.firstLogin ?: false
                _isFirstLogin.value = firstLogin

                // 5) Finalmente, notificamos Success (la UI ya navegará correctamente)
                _loginState.value = LoginState.Success

            } catch (e: Exception) {
                Log.e("LoginVM", "Google sign-in error", e)
                _loginState.value = LoginState.Error
            }
        }
    }

}
