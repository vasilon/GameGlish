package com.example.gameglish.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gameglish.data.repository.RepositoryCompetitivo
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class CompetitiveGameViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = RepositoryCompetitivo()
    // Obtén el UID del usuario actual
    val currentUserId: String = FirebaseAuth.getInstance().currentUser?.uid ?: "unknown"

    fun createGame(onResult: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val gameId = repository.createGame(currentUserId)
                onResult(gameId)
            } catch (e: Exception) {
                onResult("")
            }
        }
    }

    fun joinGame(gameId: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                repository.joinGame(gameId, currentUserId)
                onResult(true)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }
}
