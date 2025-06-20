// -----------------------------------------------------------------------------
// DaoPregunta.kt
// -----------------------------------------------------------------------------
// Data‑Access‑Object (DAO) para la entidad `EntityPregunta` utilizada en GameGlish.
// Proporciona métodos suspend que Room implementa en tiempo de compilación para
// interactuar con la tabla `preguntas`.
// -----------------------------------------------------------------------------

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
}

