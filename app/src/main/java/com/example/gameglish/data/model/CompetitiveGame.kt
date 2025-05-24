// -----------------------------------------------------------------------------
// CompetitiveModels.kt
// -----------------------------------------------------------------------------
// Modelos de dominio para el modo competitivo 1vs1 de GameGlish.
// Incluye:
//   • `CompetitiveGame`   → estado completo de una partida en Firebase.
//   • `CompetitiveQuestion` → representación simplificada de una pregunta.
//   • Extensión `EntityPregunta.toCompetitiveQuestion()` para mapear desde
//     el modelo local de práctica al formato competitivo.
// -----------------------------------------------------------------------------


package com.example.gameglish.data.model

/**
 * Datos persistidos de una partida competitiva almacenada en Firebase.
 * Las claves coinciden con los nombres de los nodos para facilitar la
 * (des)serialización automática con Firebase.
 */

data class CompetitiveGame(
    /** Identificador único del nodo (key de push()). */
    val gameId: String = "",
    /** UID del jugador que creó la sala. */
    val hostId: String = "",
    /** UID del retador; `null` mientras la sala está vacía. */
    val joinerId: String? = null,
    /** Enunciado de la pregunta enviada actualmente. */
    val currentQuestion: String? = null,
    /** Opciones A–D mostradas en pantalla. */
    val answerOptions: List<String> = emptyList(),
    /** Vidas restantes del host (empieza con 3). */
    val hostLives: Int = 3,
    /** Vidas restantes del joiner (empieza con 3). */
    val joinerLives: Int = 3,
    /** Segundos restantes para responder (se reinicia a 20 en cada ronda). */
    val timeLeft: Int = 20,
    /** Estado global: "waiting" | "inProgress" | "finished". */
    val state: String = "waiting",
    /** UID del ganador cuando `state == "finished"`; "draw" para empate. */
    val winner: String? = null,
    /** Identificador de la opción correcta; solo el host lo utiliza. */
    val correctId: String? = null,
    /** Marca de tiempo de creación (ms desde epoch) para ordenar partidas. */
    val timestamp: Long = System.currentTimeMillis()
)
/**
 * Estructura simplificada de una pregunta para el modo competitivo.
 */
data class CompetitiveQuestion(
    val question: String = "",
    /** Opciones en orden A, B, C, D. */
    val options: List<String> = emptyList(),
    /** Identificador de la correcta: "a" | "b" | "c" | "d". */
    val correctId: String = ""
)

/**
 * Extensión que convierte una `EntityPregunta` (usada en el modo práctica) a
 * `CompetitiveQuestion`, reutilizando los mismos datos para evitar duplicación.
 */

fun EntityPregunta.toCompetitiveQuestion(): CompetitiveQuestion =
    CompetitiveQuestion(
        question  = enunciado,
        options   = listOf(opcionA, opcionB, opcionC, opcionD),
        correctId = opcionCorrecta
    )

