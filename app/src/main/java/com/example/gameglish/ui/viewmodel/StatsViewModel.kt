// -----------------------------------------------------------------------------
// StatsViewModel_comentado.kt
// -----------------------------------------------------------------------------
// ViewModel responsable de la sincronización y exposición de las estadísticas
// de usuario en GameGlish.
// Contiene comentarios detallados en español para facilitar el mantenimiento
// y la comprensión del flujo de datos (Firebase ↔ Room ↔ UI).
// -----------------------------------------------------------------------------

package com.example.gameglish.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gameglish.data.database.GameGlishDatabase
import com.example.gameglish.data.model.EntityEstadistica

import com.example.gameglish.data.repository.RepositoryEstadistica

import com.google.firebase.auth.FirebaseAuth

import kotlinx.coroutines.launch

/**
 * ViewModel que mantiene una **fuente única de verdad** para las estadísticas
 * mostradas en la sección “Stats”.
 * Estrategia: siempre se leen primero del backend (Firebase) para garantizar
 * datos frescos, se guardan en Room y, por último, se publican en la UI.
 */

class StatsViewModel(application: Application) : AndroidViewModel(application) {

    // ---------------------------------------------------------------------
    // DEPENDENCIAS
    // ---------------------------------------------------------------------

    /** Referencia a la base de datos local (Room). */
    private val db   = GameGlishDatabase.getDatabase(application)
    private val repo = RepositoryEstadistica(db)

    // ---------------------------------------------------------------------
    // STATEHOLDER PARA LA UI (Compose)
    // ---------------------------------------------------------------------

    /**
     * State que expone la lista de estadísticas.
     * **mutableStateOf** → Compose observará y recompondrá cuando cambie.
     */

    private val _estadisticas = mutableStateOf<List<EntityEstadistica>>(emptyList())
    val estadisticas: State<List<EntityEstadistica>> = _estadisticas


    // ---------------------------------------------------------------------
    // INIT BLOQUE — Carga inmediata al crear el VM
    // ---------------------------------------------------------------------

    init {
        viewModelScope.launch {

            // 1) Obtén UID. Si no hay usuario, sal silenciosamente.
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch

// 2) Limpia cualquier estadística local desfasada de ESTE usuario.
            repo.clearLocalStatsForUser(uid)

            // 3) Descarga siempre la fuente remota más reciente.
            val listaRemota = try {
                repo.getEstadisticasUsuarioRemoto(uid)
            } catch (e: Exception) {
                Log.e("StatsViewModel", "Error lectura remota: ${e.message}", e)
                emptyList()
            }

            // 4) Persiste localmente (REPLACE evita duplicados).
            listaRemota.forEach { repo.insertEstadisticaLocalOnly(it) }

            // 5) Publica en la UI ordenando por fecha descendente.
            _estadisticas.value = listaRemota.sortedByDescending { it.fecha }
        }
    }
}