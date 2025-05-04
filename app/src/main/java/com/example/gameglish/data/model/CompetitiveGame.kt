package com.example.gameglish.data.model

data class CompetitiveGame(
    val gameId: String = "",
    val hostId: String = "",
    val joinerId: String? = null,
    val currentQuestion: String? = null,
    val answerOptions: List<String> = emptyList(),
    val hostLives: Int = 3,
    val joinerLives: Int = 3,
    val timeLeft: Int = 20,
    val state: String = "waiting",
    val winner: String? = null,          // NEW
    /* solo el host lo rellena; a los demás clientes les da igual */
    val correctId: String? = null,       // NEW   (warning desaparece)
    val timestamp: Long = System.currentTimeMillis()
)
data class CompetitiveQuestion(
    val question: String = "",
    val options: List<String> = emptyList(),   // A-D en orden
    val correctId: String = ""                 // "a" | "b" | "c" | "d"
)

fun EntityPregunta.toCompetitiveQuestion(): CompetitiveQuestion =
    CompetitiveQuestion(
        question  = enunciado,
        options   = listOf(opcionA, opcionB, opcionC, opcionD),
        correctId = opcionCorrecta
    )

