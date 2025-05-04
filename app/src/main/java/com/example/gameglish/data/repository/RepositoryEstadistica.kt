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

class RepositoryEstadistica(val db: GameGlishDatabase) {


    private val firebaseUrl = "https://gameglish-default-rtdb.europe-west1.firebasedatabase.app"
    private val remoteRef = FirebaseDatabase.getInstance(firebaseUrl)
        .getReference("estadisticas")

    suspend fun insertEstadistica(estadistica: EntityEstadistica) {
        // 1) Inserta local
        db.estadisticaDao().insertEstadistica(estadistica)
        // 2) Sincroniza a remoto
        guardarEstadisticaRemota(estadistica)
    }

    private suspend fun guardarEstadisticaRemota(estadistica: EntityEstadistica) {
        try {
            withContext(NonCancellable) {

                val key = estadistica.remoteId   // debe venir ya asignado
                remoteRef
                    .child(estadistica.userId)
                    .child(key)                    // ← uso la misma clave
                    .setValue(estadistica)
                    .await()
            }
        } catch (e: Exception) {
            Log.e("RepoEstadistica", "Error guardando remoto: ${e.message}", e)
        }
    }

    suspend fun clearLocalStatsForUser(userId: String) {
        db.estadisticaDao().deleteByUser(userId)
    }

    /** Inserta *solo* local, sin tocar Firebase */
    suspend fun insertEstadisticaLocalOnly(e: EntityEstadistica) {
        db.estadisticaDao().insertEstadistica(e)
    }

    /**
     * Lee siempre del remoto. Si no hay, devuelve lista vacía.
     */
    suspend fun getEstadisticasUsuarioRemoto(userId: String): List<EntityEstadistica> {
        val firebaseUrl = "https://gameglish-default-rtdb.europe-west1.firebasedatabase.app"
        val ref = FirebaseDatabase
            .getInstance(firebaseUrl)
            .getReference("estadisticas")
            .child(userId)

        val snapshot = ref.get().await()
        return snapshot.children.mapNotNull { child ->
            // Extraemos los campos desde el snapshot
            val map = child.value as? Map<String, Any> ?: return@mapNotNull null
            val fecha = (map["fecha"] as? Long) ?: return@mapNotNull null
            val puntos = (map["puntos"] as? Long)?.toInt() ?: return@mapNotNull null
            val aciertos = (map["aciertos"] as? Long)?.toInt() ?: 0
            val errores  = (map["errores"]  as? Long)?.toInt() ?: 0

            EntityEstadistica(
                remoteId = child.key!!,
                userId   = userId,
                fecha    = fecha,
                puntos   = puntos,
                aciertos = aciertos,
                errores  = errores
            )
        }
    }

    /**
     * Lectura local (por si quieres fallback o cache).
     */
    suspend fun getEstadisticasUsuarioLocal(userId: String): List<EntityEstadistica> {
        return db.estadisticaDao()
            .getEstadisticasByUser(userId)
            .sortedByDescending { it.fecha }
    }


    suspend fun obtenerRankingGlobal(): List<EntityRanking> {
        // 1) Leemos todas las estadísticas
        val firebaseDb = FirebaseDatabase.getInstance(firebaseUrl)
        val statsRef   = firebaseDb.getReference("estadisticas")
        val statsSnap  = statsRef.get().await()
        Log.d("RepoEstadistica", "estadisticas nodes: ${statsSnap.childrenCount}")
        // Construimos un mapa userId → puntos totales
        val pointsMap = mutableMapOf<String, Int>()
        statsSnap.children.forEach { userNode ->
            val userId = userNode.key!!
            userNode.children.forEach { statNode ->
                // Mapeo manual en lugar de getValue()
                val map = statNode.value as? Map<String,Any> ?: return@forEach
                val pts = (map["puntos"] as? Number)?.toInt() ?: 0
                pointsMap[userId] = (pointsMap[userId] ?: 0) + pts
            }
        }

        // 2) Leemos todos los usuarios
        val usersRef  = firebaseDb.getReference("usuarios")
        val usersSnap = usersRef.get().await()
        Log.d("RepoEstadistica", "usuarios nodes: ${usersSnap.childrenCount}")
        val rankingList = mutableListOf<EntityRanking>()
        usersSnap.children.forEach { userChild ->
            val map = userChild.value as? Map<String,Any> ?: return@forEach
            val uidFirebase = map["uidFirebase"] as? String ?: return@forEach
            val nombre      = map["nombre"] as? String ?: "?"
            val nivel       = (map["nivel"] as? Number)?.toInt() ?: 1
            val totalPoints = pointsMap[uidFirebase] ?: 0
            rankingList += EntityRanking(
                userId = uidFirebase,
                nombre = nombre,
                nivel  = nivel,
                puntos = totalPoints
            )
        }

        // 3) Ordenamos y devolvemos
        return rankingList.sortedByDescending { it.puntos }
    }
}