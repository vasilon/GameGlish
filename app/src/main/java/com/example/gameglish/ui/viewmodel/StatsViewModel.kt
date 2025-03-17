package com.example.gameglish.ui.viewmodel


import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gameglish.data.model.EntityEstadistica
import com.example.gameglish.data.model.EntityUsuario
import com.example.gameglish.data.repository.RepositoryEstadistica
import com.example.gameglish.data.repository.RepositoryUsuario
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class StatsViewModel(
    private val repositoryUsuario: RepositoryUsuario,
    private val repositoryEstadistica: RepositoryEstadistica
) : ViewModel() {

    private val _usuario = mutableStateOf<EntityUsuario?>(null)
    val usuario: State<EntityUsuario?> = _usuario

    private val _estadisticas = mutableStateOf<List<EntityEstadistica>>(emptyList())
    val estadisticas: State<List<EntityEstadistica>> = _estadisticas

    init {
        viewModelScope.launch {
            // Se obtiene el uid del usuario autenticado mediante Firebase.
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid != null) {
                // Obtiene el usuario desde la base de datos local.
                _usuario.value = repositoryUsuario.obtenerUsuarioLocal(uid)
                // Obtiene las estadísticas correspondientes a este usuario.
                _estadisticas.value = repositoryEstadistica.getEstadisticasUsuario(uid)
            } else {
                Log.e("StatsViewModel", "No hay usuario autenticado.")
            }
        }
    }
}
