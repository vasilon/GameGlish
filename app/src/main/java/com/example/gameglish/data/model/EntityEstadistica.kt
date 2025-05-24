// -----------------------------------------------------------------------------
// EntityEstadistica.kt
// -----------------------------------------------------------------------------
// Entidad Room que representa una partida de práctica finalizada o un resultado
// de test en GameGlish. Cada registro almacena la puntuación de un usuario en
// un momento concreto para permitir mostrar estadísticas y rankings.
//
package com.example.gameglish.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "estadisticas")
data class EntityEstadistica(
    @PrimaryKey val remoteId: String,
    val userId: String = "",
    val fecha: Long = 0L,
    val aciertos: Int = 0,
    val errores: Int = 0,
    val puntos: Int = 0
)