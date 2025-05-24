// -----------------------------------------------------------------------------
// EntityRanking.kt
// -----------------------------------------------------------------------------
// Modelo de datos (no marcado con @Entity porque no se persiste en Room) que
// representa una línea de un ranking global de usuarios en GameGlish.
// Se utiliza principalmente para mostrar la clasificación en la interfaz de
// usuario una vez que los datos han sido calculados por el repositorio.
// -----------------------------------------------------------------------------

package com.example.gameglish.data.model

/**
 * Estructura sencilla para transportar la información de ranking.
 * @property userId  Identificador único del usuario (UID de Firebase).
 * @property nombre Nombre visible del usuario. Puede ser una cadena vacía si el
 *                  nombre aún no está disponible.
 * @property nivel  Nivel de competencia lingüística del usuario codificado
 *                  como entero (A1 → 1, A2 →2, … NATIVE →7).
 * @property puntos Puntuación total acumulada por el usuario a partir de sus
 *                  estadísticas. El repositorio se encarga de calcularlo.
 */

data class EntityRanking (
    val userId: String = "",
    val nombre: String = "",
    val nivel: Int = 0,
    val puntos: Int = 0
)