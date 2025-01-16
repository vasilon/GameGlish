package com.example.gameglish.data.repository

import com.example.gameglish.data.database.GameGlishDatabase
import com.example.gameglish.data.model.EntityPregunta

class PreguntaRepository(private val db: GameGlishDatabase) {

    suspend fun insertPregunta(pregunta: EntityPregunta) {
        db.preguntaDao().insertPregunta(pregunta)
    }

    suspend fun getPreguntasPorTema(tema: String): List<EntityPregunta> {
        return db.preguntaDao().getPreguntasByTema(tema)
    }

    // Agrega métodos según necesites (buscar por nivel, etc.)
}
