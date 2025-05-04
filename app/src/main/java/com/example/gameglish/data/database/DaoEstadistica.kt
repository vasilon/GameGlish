// DaoEstadistica.kt
package com.example.gameglish.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gameglish.data.model.EntityEstadistica

@Dao
interface DaoEstadistica {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEstadistica(est: EntityEstadistica): Long

    @Query("SELECT * FROM estadisticas WHERE userId = :userId ORDER BY fecha DESC")
    suspend fun getEstadisticasByUser(userId: String): List<EntityEstadistica>

    @Query("SELECT * FROM estadisticas")
    suspend fun getAllEstadisticas(): List<EntityEstadistica>

    @Query("DELETE FROM estadisticas WHERE userId = :userId")
    suspend fun deleteByUser(userId: String)
}