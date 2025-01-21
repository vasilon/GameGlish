// DaoPregunta.kt
package com.example.gameglish.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gameglish.data.model.EntityPregunta

@Dao
interface DaoPregunta {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPregunta(pregunta: EntityPregunta): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreguntas(preguntas: List<EntityPregunta>)

    @Query("SELECT * FROM preguntas WHERE tema = :tema")
    suspend fun getPreguntasByTema(tema: String): List<EntityPregunta>

    @Query("SELECT * FROM preguntas WHERE nivel = :nivel")
    suspend fun getPreguntasByNivel(nivel: Int): List<EntityPregunta>
}

