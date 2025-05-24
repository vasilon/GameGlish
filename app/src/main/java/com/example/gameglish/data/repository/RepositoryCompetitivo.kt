
// -----------------------------------------------------------------------------
// RepositoryCompetitivo_comentado.kt
// -----------------------------------------------------------------------------
// Repositorio exclusivo para la lógica de partidas competitivas 1vs1 en
// GameGlish.
// Encapsula toda la interacción con Firebase Realtime Database para crear y
// unir partidas.
// Se añaden comentarios detallados en español para aclarar cada paso y facilitar
// el mantenimiento.
// -----------------------------------------------------------------------------

package com.example.gameglish.data.repository

import com.example.gameglish.data.model.CompetitiveGame
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

/**
 * Repositorio que gestiona la colección `competitivo/games` en Firebase RTDB.
 *
 * IMPORTANTE:
 * - Se inyecta la URL regional (europe‑west1) explícitamente para evitar latencia.
 * - Los métodos son `suspend` porque utilizan operaciones con `await()`.
 */

class RepositoryCompetitivo {

    // -------------------------------------------------------------------------
    // Propiedades
    // -------------------------------------------------------------------------

    /** Endpoint de la base de datos en la región europe‑west1 */
    private val firebaseUrl = "https://gameglish-default-rtdb.europe-west1.firebasedatabase.app"

    /** Referencia raíz al nodo de partidas competitivas */
    private val remoteDb = FirebaseDatabase.getInstance(firebaseUrl)
        .getReference("competitivo/games")

    // -------------------------------------------------------------------------
    // API pública
    // -------------------------------------------------------------------------

    /**
     * Crea una nueva partida aprovechando `push()` para generar la clave única.
     *
     * @param hostId UID del jugador que actúa como anfitrión.
     * @return gameId (la misma clave generada por Firebase)
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
     * Une a un segundo jugador a la partida existente y cambia su estado.
     *
     * @param gameId   Identificador de la partida (key generada por createGame).
     * @param joinerId UID del jugador que se une.
     */
    suspend fun joinGame(gameId: String, joinerId: String) {
        val gameRef = remoteDb.child(gameId)  // Usa la misma key generada por createGame
        gameRef.child("joinerId").setValue(joinerId).await()
        gameRef.child("state").setValue("inProgress").await()
    }
}
