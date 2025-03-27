package com.example.gameglish.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gameglish.data.model.CompetitiveGame
import com.example.gameglish.data.repository.RepositoryCompetitivo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CompetitiveGameViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = RepositoryCompetitivo()

    val currentUserId: String = FirebaseAuth.getInstance().currentUser?.uid ?: "unknown"

    private val _gameState = MutableStateFlow(CompetitiveGame())
    val gameState: StateFlow<CompetitiveGame> = _gameState

    private val firebaseUrl = "https://gameglish-default-rtdb.europe-west1.firebasedatabase.app"
    private val dbRef = FirebaseDatabase.getInstance(firebaseUrl).getReference("competitivo/games")

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

    fun observeGame(gameId: String) {
        dbRef.child(gameId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val game = snapshot.getValue(CompetitiveGame::class.java) ?: return
                _gameState.value = game
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun observeGameStatus(gameId: String, onGameStarted: () -> Unit) {
        val gameRef = dbRef.child(gameId)
        gameRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val game = snapshot.getValue(CompetitiveGame::class.java) ?: return
                if (game.state == "inProgress" && !game.joinerId.isNullOrEmpty()) {
                    onGameStarted()
                    gameRef.removeEventListener(this)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun sendAnswer(gameId: String, answer: String) {
        // ...
    }


    fun handleTimeUp(gameId: String) {
        viewModelScope.launch {
            // Ejemplo: restar una vida al jugador que no ha respondido.
            // Podrías leer el game actual, identificar al host/joiner y actualizar.
            val currentGame = _gameState.value
            val isHost = (currentUserId == currentGame.hostId)
            val newLives = if (isHost) currentGame.hostLives - 1 else currentGame.joinerLives - 1

            // Actualizar en DB
            if (isHost) {
                dbRef.child(gameId).child("hostLives").setValue(newLives)
            } else {
                dbRef.child(gameId).child("joinerLives").setValue(newLives)
            }
        }
    }
}
