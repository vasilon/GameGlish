// Kotlin
package com.example.gameglish.ui.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
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
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid == null) {
                Log.e("PreguntaViewModel", "Usuario no autenticado")
                return@launch
            }

            // 1) Calculamos datos
            val errores = total - correctCount
            val puntos  = correctCount * 10
            val fecha   = System.currentTimeMillis()

            // 2) Preparamos la referencia remota y generamos la push key
            val firebaseUrl = "https://gameglish-default-rtdb.europe-west1.firebasedatabase.app"
            val remoteRef   = FirebaseDatabase.getInstance(firebaseUrl)
                .getReference("estadisticas")
                .child(uid)
            val newRef      = remoteRef.push()
            val remoteId    = newRef.key ?: return@launch

            // 3) Construimos la entidad incluyendo ese remoteId
            val estadistica = EntityEstadistica(
                remoteId = remoteId,
                userId   = uid,
                fecha    = fecha,
                aciertos = correctCount,
                errores  = errores,
                puntos   = puntos
            )

            // 4) Guardamos en Firebase bajo la misma clave
            try {
                newRef.setValue(estadistica).await()
            } catch (e: Exception) {
                Log.e("PreguntaViewModel", "Error guardando remoto: ${e.message}", e)
            }

            // 5) Insertamos localmente (REPLACE evitará duplicados si existiera)
            repositoryEstadistica.insertEstadisticaLocalOnly(estadistica)
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