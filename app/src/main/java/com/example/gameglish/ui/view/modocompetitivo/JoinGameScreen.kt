package com.example.gameglish.ui.view.modocompetitivo

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gameglish.ui.components.BackTopAppBar
import com.example.gameglish.ui.navigation.Screen
import com.example.gameglish.ui.viewmodel.CompetitiveGameViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun JoinGameScreen(
    navController: NavController,
    viewModel: CompetitiveGameViewModel = viewModel()
) {
    var gameCode by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Inicia la consulta de partidas disponibles
    LaunchedEffect(Unit) {
        viewModel.fetchAvailableGames()
    }

    val availableGames by viewModel.availableGames.collectAsState()

    // Variable para controlar el diálogo de confirmación
    var selectedGameId by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = { BackTopAppBar(navController = navController, title = "Unirse a Partida") }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // 1. Campo para unirse por código
            Text("Ingresa el código de la partida", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = gameCode,
                onValueChange = { gameCode = it },
                label = { Text("Código de Partida") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    viewModel.joinGame(gameCode) { success ->
                        if (success) {
                            navController.navigate(Screen.CompetitiveGame.createRoute(gameCode))
                        } else {
                            Toast.makeText(context, "Error al unirse a la partida", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Unirse a Partida")
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 2. Lista de partidas disponibles
            Text("Partidas disponibles:", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))

            if (availableGames.isEmpty()) {
                Text("No hay partidas disponibles", style = MaterialTheme.typography.bodyMedium)
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(availableGames) { game ->
                        // Cada partida, consultamos el perfil del host
                        var hostName by remember { mutableStateOf("Cargando...") }
                        var hostLevelString by remember { mutableStateOf("A1") }

                        // Llamamos a getUserProfile en un LaunchedEffect
                        LaunchedEffect(game.hostId) {
                            viewModel.getUserProfile(game.hostId) { profile ->
                                hostName = profile.nombre
                                hostLevelString = viewModel.intToLevelString(profile.nivel)
                            }
                        }

                        // Ahora mostramos una Card con hostName y hostLevelString
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    selectedGameId = game.gameId
                                },
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Host: $hostName", style = MaterialTheme.typography.titleMedium)
                                Text("Nivel: $hostLevelString", style = MaterialTheme.typography.bodyMedium)
                                Text("Vidas: ${game.hostLives}", style = MaterialTheme.typography.bodyMedium)
                                Text("Estado: ${game.state}", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }

        // Diálogo para confirmar unirse a una partida
        if (selectedGameId != null) {
            AlertDialog(
                onDismissRequest = { selectedGameId = null },
                title = { Text("Unirse a Partida") },
                text = { Text("¿Deseas unirte a esta partida?") },
                confirmButton = {
                    TextButton(onClick = {
                        val gameIdToJoin = selectedGameId
                        if (gameIdToJoin != null) {
                            viewModel.joinGame(gameIdToJoin) { success ->
                                if (success) {
                                    navController.navigate(Screen.CompetitiveGame.createRoute(gameIdToJoin))
                                } else {
                                    Toast.makeText(context, "Error al unirse a la partida", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        selectedGameId = null
                    }) {
                        Text("Sí")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { selectedGameId = null }) {
                        Text("No")
                    }
                }
            )
        }
    }
}