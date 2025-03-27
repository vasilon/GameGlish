package com.example.gameglish.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gameglish.ui.viewmodel.CompetitiveGameViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gameglish.ui.components.LivesRow

@Composable
fun CompetitiveGameScreen(
    navController: NavController,
    gameId: String,
    viewModel: CompetitiveGameViewModel = viewModel()
) {
    // Observamos la partida (cambios en hostLives, joinerLives, etc.)
    LaunchedEffect(gameId) {
        viewModel.observeGame(gameId)
    }

    val gameState = viewModel.gameState.collectAsState().value

    // Host y Joiner
    val hostDisplay = gameState.hostId
    val joinerDisplay = gameState.joinerId ?: "Waiting..."

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Scoreboard
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.Start) {
                Text(text = "Host: $hostDisplay", style = MaterialTheme.typography.titleLarge)
                LivesRow(lives = gameState.hostLives)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = "Joiner: $joinerDisplay", style = MaterialTheme.typography.titleLarge)
                LivesRow(lives = gameState.joinerLives)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Pregunta
        Text(
            text = gameState.currentQuestion ?: "No question available",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Ejemplo de opciones
        Button(
            onClick = { viewModel.sendAnswer(gameId, "a") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Option A")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Temporizador
        Text(
            text = "Time left: ${gameState.timeLeft} s",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

