package com.example.gameglish.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gameglish.data.model.EntityPregunta

@Dao
interface PreguntaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPregunta(pregunta: EntityPregunta)

    @Query("SELECT * FROM preguntas WHERE tema = :tema")
    suspend fun getPreguntasByTema(tema: String): List<EntityPregunta>

    // Otras queries, como buscar por nivel, etc.
}
