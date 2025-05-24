
// -----------------------------------------------------------------------------
// Este archivo forma parte del proyecto GameGlish.
// Contiene la lógica de presentación y de orquestación de flujos de autenticación
// en la aplicación Android. Se ha añadido documentación detallada en español para
// facilitar su comprensión y mantenimiento por parte de futuros desarrolladores.
// -----------------------------------------------------------------------------

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


// -----------------------------------------------------------------------------
// ENUMS Y MODELOS DE ESTADO
// -----------------------------------------------------------------------------

/**
 * Posibles estados de la pantalla de login.
 * Se expone a la interfaz (Compose) para reaccionar según corresponda.
 */
enum class LoginState {
    Idle,
    Loading,
    Success,
    Error,
    PasswordResetSent          // ← NUEVO
}

// -----------------------------------------------------------------------------
// VIEWMODEL
// -----------------------------------------------------------------------------

/**
 * ViewModel encargado de gestionar la autenticación y el perfil de usuario.
 *
 *  db               Instancia única de la BD Room local.
 *  usuarioRepository Repositorio que implementa la lógica de datos (local + remota).
 *  auth             Módulo de autenticación de Firebase.
 */

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    // --------------------------
    // DEPENDENCIAS Y REPOS
    // -----------------

    private val db: GameGlishDatabase = GameGlishDatabase.getDatabase(application)
    private val usuarioRepository = RepositoryUsuario(db)
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // --------------------------
    // STATEFLOWS EXPUESTOS A UI
    // --------------------------


    private val _loginState = MutableStateFlow(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    private val _isUserLoggedIn = MutableStateFlow(auth.currentUser != null)
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn

    // null  -> todavía no sabemos
    // true  -> es su primer login (mostrar pantalla de onboarding)
    // false -> ya ha completado onboarding anteriormente

    private val _isFirstLogin = MutableStateFlow<Boolean?>(null)
    val isFirstLogin: StateFlow<Boolean?> = _isFirstLogin


    // --------------------------
    // INIT
    // --------------------------
    init {
        checkUserState()
    }

    // -------------------------------------------------------------------------
    // RESTABLECIMIENTO DE CONTRASEÑA
    // -------------------------------------------------------------------------

    /**
     * Envía un correo de restablecimiento de contraseña a la dirección indicada.
     * Actualiza el estado para que la UI muestre feedback al usuario.
     */

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
    // -------------------------------------------------------------------------
    // CHEQUEO ESTADO DE USUARIO
    // -------------------------------------------------------------------------

    /**
     * Comprueba si hay sesión iniciada y, de ser así, sincroniza el perfil local
     * con la nube. También determina si es la primera vez que el usuario entra.
     */

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

    // -------------------------------------------------------------------------
    // INICIO DE SESIÓN
    // -------------------------------------------------------------------------

    /**
     * Inicia sesión con correo y contraseña.
     * @param email    Correo electrónico introducido por el usuario.
     * @param password Contraseña introducida por el usuario.
     */

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

    // -------------------------------------------------------------------------
    // REGISTRO DE USUARIO
    // -------------------------------------------------------------------------

    /**
     * Registra un nuevo usuario con email/contraseña.
     * Se utilizan valores por defecto para nombre y nivel en Repository.
     */


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

    // -------------------------------------------------------------------------
    // ONBOARDING COMPLETADO
    // -------------------------------------------------------------------------

    /**
     * Marca en BD que el usuario ya pasó por el flujo de primer inicio de sesión.
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

    // -------------------------------------------------------------------------
    // ACTUALIZAR PERFIL (NOMBRE + NIVEL)
    // -------------------------------------------------------------------------

    /**
     * Actualiza el nombre y nivel en el perfil del usuario.
     *
     * @param uid    Identificador del usuario (Firebase UID).
     * @param nombre Nuevo nombre a almacenar.
     * @param nivel  Nuevo nivel seleccionado.
     */

    fun actualizarNombreUsuario(uid: String, nombre: String, nivel: String) {
        viewModelScope.launch {
            usuarioRepository.actualizarUsuarioProfile(uid, nombre, nivel)
        }
    }

    // -------------------------------------------------------------------------
    // MÉTODOS AUXILIARES DE ESTADO
    // -------------------------------------------------------------------------

    /**
     * Reinicia el estado de la UI (útil al volver a la pantalla de login).
     */
    fun resetState() {
        _loginState.value = LoginState.Idle
    }
    /**
     * Cierra la sesión de Firebase y actualiza el flag local.
     */
    fun cerrarSesion() {
        auth.signOut()
        _isUserLoggedIn.value = false
    }

    // -------------------------------------------------------------------------
    // LOGIN CON GOOGLE
    // -------------------------------------------------------------------------

    /**
     * Maneja el flujo de autenticación con Google. Crea usuario nuevo si no existe.
     *
     * @param idToken Token de ID devuelto por Google Sign‑In.
     */

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




                // 3) Sincronizar flag firstLogin desde Room
                val uid = auth.currentUser?.uid
                val local = uid?.let { usuarioRepository.obtenerUsuarioLocal(it) }
                val firstLogin = local?.firstLogin ?: false
                _isFirstLogin.value = firstLogin

                // 4) Notificar éxito a la interfaz
                _loginState.value = LoginState.Success

            } catch (e: Exception) {
                Log.e("LoginVM", "Google sign-in error", e)
                _loginState.value = LoginState.Error
            }
        }
    }

}
