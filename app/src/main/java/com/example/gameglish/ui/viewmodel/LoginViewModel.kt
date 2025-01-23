package com.example.gameglish.ui.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import com.example.gameglish.data.database.GameGlishDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.gameglish.data.repository.RepositoryUsuario

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

    fun registrarUsuario(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            val exito = usuarioRepository.registrarUsuarioCorreo(email, password)
            Toast.makeText(getApplication(), "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()
            _loginState.value = if (exito) LoginState.Success else LoginState.Error
        }
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}

