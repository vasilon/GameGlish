package com.example.gameglish.data.repository

import com.example.gameglish.data.model.CompetitiveGame
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class RepositoryCompetitivo {
    private val firebaseUrl = "https://gameglish-default-rtdb.europe-west1.firebasedatabase.app"
    private val remoteDb = FirebaseDatabase.getInstance(firebaseUrl)
        .getReference("competitivo/games")

    /**
     * Crea una partida competitiva usando push() para generar un ID único.
     * Inicializa la partida con el host, estado "waiting", 3 vidas para cada jugador,
     * y otros campos iniciales. Devuelve el gameId generado.
     */
    suspend fun createGame(hostId: String): String {
        val newGameRef = remoteDb.push()  // Genera un nodo único
        val gameId = newGameRef.key ?: ""
        val game = CompetitiveGame(
            gameId = gameId,
            hostId = hostId,
            joinerId = null,
            currentQuestion = null,
            answerOptions = emptyList(),
            hostLives = 3,
            joinerLives = 3,
            timeLeft = 20,
            state = "waiting",
            timestamp = System.currentTimeMillis()
        )
        newGameRef.setValue(game).await()
        return gameId
    }

    /**
     * Permite a un jugador (joiner) unirse a una partida existente.
     * Actualiza el nodo de la partida (sin crear uno nuevo) estableciendo joinerId y
     * cambiando el estado a "inProgress".
     */
    suspend fun joinGame(gameId: String, joinerId: String) {
        val gameRef = remoteDb.child(gameId)  // Usa la misma key generada por createGame
        gameRef.child("joinerId").setValue(joinerId).await()
        gameRef.child("state").setValue("inProgress").await()
    }
}
