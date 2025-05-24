// -----------------------------------------------------------------------------
// DaoEstadistica.kt
// -----------------------------------------------------------------------------
// Data‑Access‑Object (DAO) para la entidad EntityEstadistica.
// Define las operaciones CRUD que Room implementará en tiempo de compilación.
// Estas operaciones son usadas por los repositorios y ViewModels de GameGlish.
// Los métodos son `suspend` para ejecutarse en corrutinas de Kotlin y evitar
// bloqueos del hilo principal. Los comentarios explican el propósito de cada
// principales de la sentencia SQL.
// --------...
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

    @Query("DELETE FROM estadisticas WHERE userId = :userId")
    suspend fun deleteByUser(userId: String)
}