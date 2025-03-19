// EntityEstadistica.kt
package com.example.gameglish.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "estadisticas")
data class EntityEstadistica(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String = "",
    val fecha: Long = 0L,
    val aciertos: Int = 0,
    val errores: Int = 0,
    val puntos: Int = 0
)