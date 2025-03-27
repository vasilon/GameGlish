package com.example.gameglish.ui.view

import android.widget.Toast
import androidx.compose.foundation.layout.*
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
fun HostGameScreen(
    navController: NavController,
    viewModel: CompetitiveGameViewModel = viewModel()
) {
    val context = LocalContext.current
    // Estado para almacenar el ID de la partida creada.
    var gameId by remember { mutableStateOf("") }

    // Si el gameId se ha establecido, observa el estado de la partida.
    if (gameId.isNotEmpty()) {
        LaunchedEffect(gameId) {
            viewModel.observeGameStatus(gameId) {
                // Cuando el juego pasa a "inProgress", navega a CompetitiveGameScreen usando createRoute.
                navController.navigate(Screen.CompetitiveGame.createRoute(gameId))
            }
        }
    }

    Scaffold(
        topBar = { BackTopAppBar(navController = navController, title = "Crear Partida") }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (gameId.isEmpty()) {
                Text("Host your competitive game", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        viewModel.createGame { id ->
                            if (id.isNotEmpty()) {
                                gameId = id
                                Toast.makeText(context, "Partida creada con id: $id", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Error al crear partida", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Crear Partida")
                }
            } else {
                Text("Partida creada: $gameId", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Esperando a que alguien se una...", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
