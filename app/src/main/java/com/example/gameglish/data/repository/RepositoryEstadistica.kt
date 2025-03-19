// Kotlin
package com.example.gameglish.data.repository

import android.util.Log
import com.example.gameglish.data.database.GameGlishDatabase
import com.example.gameglish.data.model.EntityEstadistica
import com.example.gameglish.data.model.EntityRanking
import com.example.gameglish.data.model.EntityUsuario
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

class RepositoryEstadistica(private val db: GameGlishDatabase) {

    suspend fun insertEstadistica(estadistica: EntityEstadistica) {
        db.estadisticaDao().insertEstadistica(estadistica)
        // Save the statistic remotely after local insertion.
        guardarEstadisticaRemota(estadistica)
    }

    suspend fun getEstadisticasUsuario(userId: String): List<EntityEstadistica> {
        return db.estadisticaDao().getEstadisticasByUser(userId)
    }

    // New function to save statistic remotely using Firebase.
    // Kotlin - RepositoryEstadistica.kt
    suspend fun guardarEstadisticaRemota(estadistica: EntityEstadistica) {
        try {
            withContext(NonCancellable) {
                val firebaseUrl = "https://gameglish-default-rtdb.europe-west1.firebasedatabase.app"
                val remoteDb = FirebaseDatabase.getInstance(firebaseUrl).getReference("estadisticas")
                remoteDb.push().setValue(estadistica).await()
            }
        } catch (e: Exception) {
            Log.e("RepositoryEstadistica", "Error saving remote estadistica: ${e.message}", e)
        }
    }

    suspend fun obtenerRankingGlobal(): List<EntityRanking> {
        return withTimeoutOrNull(15000L) {
            val firebaseUrl = "https://gameglish-default-rtdb.europe-west1.firebasedatabase.app"
            val firebaseDb = FirebaseDatabase.getInstance(firebaseUrl)

            // Fetch all statistics and group total points by userId.
            val statsSnapshot = firebaseDb.getReference("estadisticas").get().await()
            val pointsMap = mutableMapOf<String, Int>()
            for (child in statsSnapshot.children) {
                val stat = child.getValue(EntityEstadistica::class.java)
                stat?.let {
                    pointsMap[it.userId] = (pointsMap[it.userId] ?: 0) + it.puntos
                }
            }

            // Fetch all users to retrieve name and level.
            val usersSnapshot = firebaseDb.getReference("usuarios").get().await()
            val rankingList = mutableListOf<EntityRanking>()
            for (userChild in usersSnapshot.children) {
                val user = userChild.getValue(EntityUsuario::class.java)
                user?.let {
                    val totalPoints = pointsMap[it.uidFirebase] ?: 0
                    rankingList.add(
                        EntityRanking(
                            userId = it.uidFirebase,
                            nombre = it.nombre,
                            nivel = it.nivel,
                            puntos = totalPoints
                        )
                    )
                }
            }
            rankingList.sortedByDescending { it.puntos }
        } ?: emptyList()
    }
}