package com.example.gameglish.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun QuestionTimer(
    timeLeft: Int,
    onTimeUp: () -> Unit
) {
    // Si timeLeft llega a 0, se llama a onTimeUp.
    // Normalmente, este decremento vendría del ViewModel.
    Text("Tiempo restante: $timeLeft s", style = MaterialTheme.typography.bodyMedium)
}