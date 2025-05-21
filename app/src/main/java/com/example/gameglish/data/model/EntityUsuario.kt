package com.example.gameglish.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class EntityUsuario(
    @PrimaryKey val uidFirebase: String = "",  // Se utiliza uidFirebase como clave principal
    val email: String = "",
    val nombre: String = "",
    var puntos: Int = 0,
    var nivel: Int = 0,
    var firstLogin: Boolean = true  // Nuevo campo para indicar el primer login
)