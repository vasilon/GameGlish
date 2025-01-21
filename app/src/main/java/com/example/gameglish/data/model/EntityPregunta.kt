package com.example.gameglish.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "preguntas")
data class EntityPregunta(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val enunciado: String,
    val opcionA: String,
    val opcionB: String,
    val opcionC: String,
    val opcionCorrecta: String,
    val tema: String,
    val nivel: Int
)
