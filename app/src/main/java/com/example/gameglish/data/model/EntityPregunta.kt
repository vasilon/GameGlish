// -----------------------------------------------------------------------------
// EntityPregunta.kt
// -----------------------------------------------------------------------------
// Entidad Room que define la estructura de la tabla `preguntas` en la base de
// datos local de GameGlish.
// Cada fila representa una pregunta de opción múltiple que puede utilizarse en
// los distintos modos de juego (práctica, competitivo, etc.).
//
//  • La anotación @Entity genera automáticamente la tabla al compilar.
//  •`id` es la clave primaria y se autogenera (autoIncrement).
//  •Los campos `opcionA`‑`opcionD` almacenan los posibles distractores.
//  •`opcionCorrecta` guarda la letra que identifica la respuesta correcta
//    ("a", "b", "c" o "d"), simplificando las comparaciones en el código.
//  •`tema` y `nivel` permiten filtrar preguntas por categoría y dificultad.
// -----------------------------------------------------------------------------


package com.example.gameglish.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "preguntas")
data class EntityPregunta(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // PK autoincremental
    val enunciado: String,                            // Texto de la pregunta
    val opcionA: String,                              // Respuesta A
    val opcionB: String,                              // Respuesta B
    val opcionC: String,                              // Respuesta C
    val opcionD: String,                              // Respuesta D
    val opcionCorrecta: String,                       // Letra de la correcta
    val tema: String,                                 // Categoría (Gramática, Vocabulario…)
    val nivel: Int                                    // Nivel CEFR (1=A1 … 7=NATIVE)
)