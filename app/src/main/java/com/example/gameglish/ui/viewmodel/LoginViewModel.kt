// Kotlin
package com.example.gameglish.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gameglish.data.database.GameGlishDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.gameglish.data.repository.RepositoryUsuario
import kotlinx.coroutines.launch

enum class LoginState { Idle, Loading, Success, Error }

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val db: GameGlishDatabase = GameGlishDatabase.getDatabase(application)
    private val usuarioRepository = RepositoryUsuario(db)

    private val _loginState = MutableStateFlow(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun iniciarSesion(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            val exito = usuarioRepository.iniciarSesionCorreo(email, password)
            _loginState.value = if (exito) LoginState.Success else LoginState.Error
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
            val exito = usuarioRepository.registrarUsuarioCorreo(
                email, password, confirmPassword, nombre, nivelSeleccionado
            )
            Log.d("LoginViewModel", "Registration success: $exito")
            _loginState.value = if (exito) LoginState.Success else LoginState.Error
        }
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}