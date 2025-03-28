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
    // UID del usuario actual
    val currentUserId: String = FirebaseAuth.getInstance().currentUser?.uid ?: "unknown"

    // StateFlow para exponer el estado del juego
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

    fun observeGame(gameId: String) {
        dbRef.child(gameId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val game = snapshot.getValue(CompetitiveGame::class.java) ?: return
                _gameState.value = game
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("CompetitiveGameVM", "observeGame cancelled: ${error.message}")
            }
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
            override fun onCancelled(error: DatabaseError) {
                Log.e("CompetitiveGameVM", "observeGameStatus cancelled: ${error.message}")
            }
        })
    }


    fun getUserName(uid: String, onResult: (String) -> Unit) {
        val userRef = FirebaseDatabase.getInstance(firebaseUrl)
            .getReference("usuarios")
            .child(uid)
            .child("nombre")
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.getValue(String::class.java) ?: "Unknown"
                onResult(name)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("CompetitiveGameVM", "getUserName cancelled: ${error.message}")
                onResult("Unknown")
            }
        })
    }

    fun sendAnswer(gameId: String, answer: String) {
        // Lógica para enviar respuesta
        Log.d("CompetitiveGameVM", "User $currentUserId answered: $answer")
    }

    fun handleTimeUp(gameId: String) {
        viewModelScope.launch {
            val currentGame = _gameState.value
            val isHost = (currentUserId == currentGame.hostId)
            val newLives = if (isHost) currentGame.hostLives - 1 else currentGame.joinerLives - 1

            if (isHost) {
                dbRef.child(gameId).child("hostLives").setValue(newLives)
            } else {
                dbRef.child(gameId).child("joinerLives").setValue(newLives)
            }
        }
    }
}
