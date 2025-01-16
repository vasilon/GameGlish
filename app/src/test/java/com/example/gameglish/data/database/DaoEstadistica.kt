package com.example.gameglish.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gameglish.data.model.EntityEstadistica


@Dao
interface EstadisticaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEstadistica(estadistica: EntityEstadistica)

    @Query("SELECT * FROM estadisticas WHERE id = :userId")
    suspend fun getEstadisticasByUser(userId: String): List<EntityEstadistica>
}
