package com.example.gameglish.data.repository

import com.example.gameglish.data.database.GameGlishDatabase
import com.example.gameglish.data.model.EntityEstadistica

class EstadisticaRepository(private val db: GameGlishDatabase) {

    suspend fun insertEstadistica(estadistica: EntityEstadistica) {
        db.estadisticaDao().insertEstadistica(estadistica)
    }

    suspend fun getEstadisticasUsuario(userId: String): List<EntityEstadistica> {
        return db.estadisticaDao().getEstadisticasByUser(userId)
    }
}
