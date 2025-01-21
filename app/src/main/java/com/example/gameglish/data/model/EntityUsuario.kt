package com.example.gameglish.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class EntityUsuario(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val uidFirebase: String,
    val email: String,
    val nombre: String,
    val puntos: Int,
    val nivel: Int
)
