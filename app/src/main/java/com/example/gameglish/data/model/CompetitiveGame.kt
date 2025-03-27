package com.example.gameglish.data.model

data class CompetitiveGame(
    val gameId: String = "",
    val hostId: String = "",
    val joinerId: String? = null,

    // Información de la pregunta actual
    val currentQuestion: String? = null,
    val answerOptions: List<String> = emptyList(), // Ej.: ["Opción A", "Opción B", ...]

    // Vidas
    val hostLives: Int = 3,
    val joinerLives: Int = 3,

    // Temporizador (en segundos)
    val timeLeft: Int = 20,

    // Estado global: "waiting", "inProgress", "finished"
    val state: String = "waiting",

    val timestamp: Long = System.currentTimeMillis()
)
