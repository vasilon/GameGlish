// Kotlin
package com.example.gameglish.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gameglish.data.database.GameGlishDatabase
import com.example.gameglish.data.model.EntityEstadistica
import com.example.gameglish.data.model.EntityPregunta
import com.example.gameglish.data.model.EntityUsuario
import com.example.gameglish.data.repository.RepositoryEstadistica
import com.example.gameglish.data.repository.RepositoryPregunta
import com.example.gameglish.data.repository.RepositoryUsuario
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class PreguntaViewModel(application: Application) : AndroidViewModel(application) {
    private val db = GameGlishDatabase.getDatabase(application)
    private val repository = RepositoryPregunta(db)
    private val repositoryEstadistica = RepositoryEstadistica(db)
    private val repositoryUsuario = RepositoryUsuario(db)

    // StateFlow for questions
    val preguntas = MutableStateFlow<List<EntityPregunta>>(emptyList())

    fun cargarPreguntasGramatica(context: Context) {
        viewModelScope.launch {
            if (repository.getPreguntasPorTema("Gramatica").isEmpty()) {
                repository.insertarPreguntasGramaticaDesdeJson(context)
            }
            preguntas.value = repository.getPreguntasPorTema("Gramatica")
        }
    }

    // Function to submit statistics after test is finished.
    fun submitEstadistica(correctCount: Int, total: Int) {
        viewModelScope.launch {
            FirebaseAuth.getInstance().currentUser?.uid?.let { userId ->
                val errores = total - correctCount
                val puntos = correctCount * 10
                val estadistica = EntityEstadistica(
                    userId = userId,
                    fecha = System.currentTimeMillis(),
                    aciertos = correctCount,
                    errores = errores,
                    puntos = puntos
                )
                repositoryEstadistica.insertEstadistica(estadistica)
            } ?: run {
                // Log error or alert that the user is not authenticated.
            }
        }
    }

    // New function to update the user's points.
    fun addPuntosToUsuario(nuevosPuntos: Int) {
        viewModelScope.launch {
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            val usuario: EntityUsuario? = repositoryUsuario.obtenerUsuarioLocal(uid)
            if (usuario != null) {
                val updatedUsuario = usuario.copy(puntos = usuario.puntos + nuevosPuntos)
                repositoryUsuario.guardarUsuarioLocal(updatedUsuario)
                repositoryUsuario.guardarUsuarioRemoto(updatedUsuario)
            }
        }
    }
}