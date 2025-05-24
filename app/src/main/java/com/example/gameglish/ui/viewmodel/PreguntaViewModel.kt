// -----------------------------------------------------------------------------
// PreguntaViewModel_comentado.kt
// -----------------------------------------------------------------------------
// ViewModel encargado de gestionar la lógica relacionada con preguntas, temas y
// estadísticas del modo de práctica individual en GameGlish.
// Se añaden comentarios detallados en español explicando la intención de cada
// sección y las decisiones de diseño.
// -----------------------------------------------------------------------------

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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * ViewModel de preguntas.
 *  - Carga y baraja preguntas por tema.
 *  - Envía a Firebase y guarda localmente las estadísticas de la sesión.
 *  - Actualiza los puntos acumulados del usuario.
 */

class PreguntaViewModel(application: Application) : AndroidViewModel(application) {


    // ──────────────────────────────────────────────────────────────────────────
    // Repositorios y base de datos
    // ──────────────────────────────────────────────────────────────────────────

    private val db = GameGlishDatabase.getDatabase(application)
    private val repository = RepositoryPregunta(db)
    private val repositoryEstadistica = RepositoryEstadistica(db)
    private val repositoryUsuario = RepositoryUsuario(db)


    // ──────────────────────────────────────────────────────────────────────────
    // StateFlows
    // ──────────────────────────────────────────────────────────────────────────

    // Flujo que expone la lista barajada de preguntas a la UI.

    private val _preguntas = MutableStateFlow<List<EntityPregunta>>(emptyList())
    val preguntas: StateFlow<List<EntityPregunta>> = _preguntas

    // ──────────────────────────────────────────────────────────────────────────
    // Carga de preguntas
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Carga las preguntas del tema indicado:
     * 1. Si la tabla local aún no tiene preguntas para ese tema, importa el JSON.
     * 2. Recupera las preguntas, las baraja y actualiza el StateFlow.
     *
     * @param context Contexto para acceder a los assets.
     * @param tema    Nombre del tema (debe coincidir con el JSON y el campo en BD).
     */

    fun cargarPreguntasPorTema(context: Context, tema: String) {
        viewModelScope.launch {
            // 1) Importar desde JSON si no hay en BD
            if (repository.getPreguntasPorTema(tema).isEmpty()) {
                repository.insertarPreguntasDesdeJson(context, tema)
            }
            // 2) Recuperar y barajar
            val listaOriginal = repository.getPreguntasPorTema(tema)
            val listaBarajada = listaOriginal.shuffled()
            _preguntas.value = listaBarajada
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Envío de estadísticas y suma de puntos
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Construye la entidad de estadística y la guarda:
     *  - Remotamente en Firebase, bajo /estadisticas/{uid}/{pushId}
     *  - Localmente en Room, evitando duplicados con REPLACE.
     *  - Añade los puntos calculados al usuario tanto local como remotamente.
     *
     * @param correctCount Número de respuestas correctas.
     * @param total        Total de preguntas respondidas.
     */

    fun submitEstadistica(correctCount: Int, total: Int) {
        viewModelScope.launch {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid == null) {
                Log.e("PreguntaViewModel", "Usuario no autenticado")
                return@launch
            }

            val errores = total - correctCount
            // Aciertos menos fallos, luego * 10 (puede quedar negativo)
            val puntos  = (correctCount - errores) * 10
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

            addPuntosToUsuario(puntos)
        }
    }


    /**
     * Incrementa los puntos del usuario actual en Room y Firebase.
     *
     * @param nuevosPuntos Puntos a añadir (pueden ser negativos en caso de más fallos que aciertos).
     */

    private fun addPuntosToUsuario(nuevosPuntos: Int) {
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