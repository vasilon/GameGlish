package com.example.gameglish.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.runtime.*
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
    // Observa la partida
    LaunchedEffect(gameId) {
        viewModel.observeGame(gameId)
    }
    val gameState = viewModel.gameState.collectAsState().value

    // Variables locales para los nombres
    var hostName by remember { mutableStateOf("") }
    var joinerName by remember { mutableStateOf("") }

    // Cuando cambia el hostId, consulta el nombre
    LaunchedEffect(gameState.hostId) {
        if (gameState.hostId.isNotEmpty()) {
            viewModel.getUserName(gameState.hostId) { name ->
                hostName = name
            }
        }
    }

    // Cuando cambia el joinerId, consulta el nombre
    LaunchedEffect(gameState.joinerId) {
        gameState.joinerId?.let { id ->
            if (id.isNotEmpty()) {
                viewModel.getUserName(id) { name ->
                    joinerName = name
                }
            }
        }
    }

    // UI de la pantalla competitiva
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Scoreboard con nombres y vidas
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.Start) {
                Text(text = "Host: $hostName", style = MaterialTheme.typography.titleLarge)
                LivesRow(lives = gameState.hostLives)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = "Joiner: ${if (joinerName.isNotEmpty()) joinerName else "Waiting..."}", style = MaterialTheme.typography.titleLarge)
                LivesRow(lives = gameState.joinerLives)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Pregunta actual
        Text(
            text = gameState.currentQuestion ?: "No question available",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Ejemplo de opciones (puedes extender para más opciones)
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
