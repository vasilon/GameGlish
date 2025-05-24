// -----------------------------------------------------------------------------
// RepositoryEstadistica_comentado.kt
// -----------------------------------------------------------------------------
// Repositorio encargado de:
//   • Persistir localmente las estadísticas de preguntas (Room).
//   • Sincronizar y consultar estadísticas en Firebase Realtime Database.
//   • Construir el ranking global de usuarios.
//
// -----------------------------------------------------------------------------

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

    // -------------------------------------------------------------------------
    // Operaciones LOCAL ↔️ REMOTO
    // -------------------------------------------------------------------------

    /**
     * Borra todas las estadísticas locales del usuario indicado.
     * Se emplea antes de una importación completa desde Firebase para evitar
     * duplicados.
     */

    suspend fun clearLocalStatsForUser(userId: String) {
        db.estadisticaDao().deleteByUser(userId)
    }


    /**
     * Inserta una estadística únicamente en la base de datos local.
     * Útil cuando la estadística ya existe en remoto y solo necesitamos
     * almacenarla en caché.
     */
    suspend fun insertEstadisticaLocalOnly(e: EntityEstadistica) {
        db.estadisticaDao().insertEstadistica(e)
    }

    // -------------------------------------------------------------------------
    // Lecturas REMOTAS
    // -------------------------------------------------------------------------

    /**
     * Descarga todas las estadísticas de un usuario desde Firebase RTDB.
     * Devuelve lista vacía si el usuario no tiene registros.
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
    // -------------------------------------------------------------------------
    // Ranking global
    // -------------------------------------------------------------------------
    /**
     * Devuelve el ranking global ordenado por puntos.
     * Algoritmo:
     * 1. Lee todas las estadísticas de Firebase.
     *   2. Construye un mapa de userId → puntos totales.
     *   3. Lee todos los usuarios de Firebase.
     *   4. Crea una lista de EntityRanking con userId, nombre, nivel y puntos.
     *   5. Ordena la lista por puntos descendentes.
     *   6. Devuelve la lista ordenada.
     */

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