package com.example.gameglish.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gameglish.data.database.GameGlishDatabase
import com.example.gameglish.data.model.EntityEstadistica
import com.example.gameglish.data.model.EntityUsuario
import com.example.gameglish.data.repository.RepositoryEstadistica
import com.example.gameglish.data.repository.RepositoryUsuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

class StatsViewModel(application: Application) : AndroidViewModel(application) {
    private val db   = GameGlishDatabase.getDatabase(application)
    private val repo = RepositoryEstadistica(db)

    private val _estadisticas = mutableStateOf<List<EntityEstadistica>>(emptyList())
    val estadisticas: State<List<EntityEstadistica>> = _estadisticas

    init {
        viewModelScope.launch {
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch

            // 1) Borra TODO lo local de este usuario
            repo.clearLocalStatsForUser(uid)

            // 2) Lee siempre del remoto
            val listaRemota = try {
                repo.getEstadisticasUsuarioRemoto(uid)
            } catch (e: Exception) {
                Log.e("StatsViewModel", "Error lectura remota: ${e.message}", e)
                emptyList()
            }

            // 3) Inserta en local (REPLACE evita duplicados si alguna ya existiera)
            listaRemota.forEach { repo.insertEstadisticaLocalOnly(it) }

            // 4) Publica en UI ordenado
            _estadisticas.value = listaRemota.sortedByDescending { it.fecha }
        }
    }
}