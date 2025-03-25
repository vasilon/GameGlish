// File: app/src/main/java/com/example/gameglish/data/repository/RepositoryCompetitivo.kt
package com.example.gameglish.data.repository

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import kotlinx.coroutines.tasks.await
import com.example.gameglish.data.model.CompetitiveGame

class RepositoryCompetitivo {
    private val firebaseUrl = "https://gameglish-default-rtdb.europe-west1.firebasedatabase.app"
    private val remoteDb = FirebaseDatabase.getInstance(firebaseUrl).getReference("competitivo/games")

    suspend fun createGame(hostId: String): String {
        val newGameRef = remoteDb.push()
        val game = CompetitiveGame(gameId = newGameRef.key ?: "", hostId = hostId)
        newGameRef.setValue(game).await()
        return newGameRef.key ?: ""
    }

    suspend fun joinGame(gameId: String, joinerId: String) {
        remoteDb.child(gameId).child("joinerId").setValue(joinerId).await()
        remoteDb.child(gameId).child("state").setValue("inProgress").await()
    }
}