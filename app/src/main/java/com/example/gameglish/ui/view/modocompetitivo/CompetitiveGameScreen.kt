// -----------------------------------------------------------------------------
// CompetitiveGameScreen.kt
// -----------------------------------------------------------------------------
// Pantalla que muestra una partida *on‑going* del modo competitivo 1vs1.
// Usa el GameState emitido por CompetitiveGameViewModel para renderizar las
// vidas, la pregunta y las respuestas en tiempo real.
// -----------------------------------------------------------------------------

package com.example.gameglish.ui.view.modocompetitivo

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

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
    /* ─── 1. Suscripciones ───────────────────────────────────────────── */
    LaunchedEffect(gameId) { viewModel.observeGame(gameId) }

    val gameState by viewModel.gameState.collectAsState()

    /* ─── 2. Estado local de UI ───────────────────────────────────────── */
    var hostName   by remember { mutableStateOf("") }
    var joinerName by remember { mutableStateOf("") }
    var myAnswer   by remember(gameState.currentQuestion) { mutableStateOf<String?>(null) }

    /* ─── 3. Resolver nombres ────────────────────────────────────────── */
    LaunchedEffect(gameState.hostId) {
        if (gameState.hostId.isNotEmpty())
            viewModel.getUserName(gameState.hostId) { hostName = it }
    }
    LaunchedEffect(gameState.joinerId) {
        gameState.joinerId?.takeIf { it.isNotEmpty() }?.let { id ->
            viewModel.getUserName(id) { joinerName = it }
        }
    }

    /* ─── 4. Diálogo de resultado cuando state == finished ───────────── */
    val showResultDialog = gameState.state == "finished"
    if (showResultDialog) {

        val ganadorId = gameState.winner
        val titulo = when {
            ganadorId == "draw"                    -> "¡Empate!"
            ganadorId == viewModel.currentUserId   -> "¡Has ganado!"
            else                                   -> "Has perdido"
        }

        /** ── VIDAS DEL GANADOR ─────────────────────────────────────────── **/
        val vidasGanador = when (ganadorId) {
            gameState.hostId   -> gameState.hostLives
            gameState.joinerId -> gameState.joinerLives
            else               -> null            // empate u otro caso
        }
        val vidasText = vidasGanador?.let { "Vidas restantes: $it" } ?: ""

        AlertDialog(
            onDismissRequest = {},
            title = { Text(titulo) },
            text  = { if (vidasText.isNotEmpty()) Text(vidasText) },
            confirmButton = {
                Button(
                    onClick = {
                        navController.popBackStack(
                            route = "competitivo_main",
                            inclusive = false
                        )
                    }
                ) { Text("Continuar") }
            }
        )
    }

    /* ─── 5. UI principal de partida ─────────────────────────────────── */
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        /* 5.1 Scoreboard */
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.Start) {
                Text("Anfitrión: $hostName", style = MaterialTheme.typography.titleLarge)
                LivesRow(lives = gameState.hostLives)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "Invitado: ${if (joinerName.isNotEmpty()) joinerName else "Waiting..."}",
                    style = MaterialTheme.typography.titleLarge
                )
                LivesRow(lives = gameState.joinerLives)
            }
        }

        Spacer(Modifier.height(24.dp))

        /* 5.2 Pregunta */
        Text(
            text = gameState.currentQuestion ?: "Esperando pregunta…",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        /* 5.3 Opciones de respuesta */
        val letters = listOf("a", "b", "c", "d")
        gameState.answerOptions.forEachIndexed { idx, texto ->
            val letter = letters[idx]
            Button(
                enabled = myAnswer == null && gameState.state == "inProgress",
                onClick = {
                    myAnswer = letter
                    viewModel.sendAnswer(gameId, letter)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
            ) { Text(texto) }
        }

        /* 5.4 Mensaje de espera */
        if (myAnswer != null && gameState.state == "inProgress") {
            Spacer(Modifier.height(8.dp))
            Text("Respuesta enviada. Esperando al oponente…")
        }

        Spacer(Modifier.height(16.dp))

        /* 5.5 Temporizador */
        Text("Tiempo restante: ${gameState.timeLeft}s",
            style = MaterialTheme.typography.bodyMedium)
    }
}
