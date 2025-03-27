package com.example.gameglish.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gameglish.data.model.CompetitiveGame
import com.example.gameglish.data.repository.RepositoryCompetitivo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch

class CompetitiveGameViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = RepositoryCompetitivo()
    // Obtén el UID del usuario actual.
    val currentUserId: String = FirebaseAuth.getInstance().currentUser?.uid ?: "unknown"

    fun createGame(onResult: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val gameId = repository.createGame(currentUserId)
                onResult(gameId)
            } catch (e: Exception) {
                Log.e("CompetitiveGameVM", "Error creating game: ${e.message}")
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
                Log.e("CompetitiveGameVM", "Error joining game: ${e.message}")
                onResult(false)
            }
        }
    }

    fun observeGameStatus(gameId: String, onGameStarted: () -> Unit) {
        val firebaseUrl = "https://gameglish-default-rtdb.europe-west1.firebasedatabase.app"
        val remoteDb = FirebaseDatabase.getInstance(firebaseUrl)
            .getReference("competitivo/games")
            .child(gameId)
        remoteDb.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val game = snapshot.getValue(CompetitiveGame::class.java)
                if (game != null && game.state == "inProgress" && game.joinerId != null) {
                    onGameStarted()
                    // Una vez detectado el cambio, puedes remover el listener.
                    remoteDb.removeEventListener(this)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("CompetitiveGameVM", "observeGameStatus cancelled: ${error.message}")
            }
        })
    }
}
