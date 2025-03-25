package com.example.gameglish.data.model

data class CompetitiveGame(
    val gameId: String = "",
    val hostId: String = "",
    val joinerId: String? = null,
    val currentQuestion: String? = null,
    val hostLives: Int = 3,
    val joinerLives: Int = 3,
    val state: String = "waiting", // "waiting", "inProgress", "finished"
    val timestamp: Long = System.currentTimeMillis()
)
