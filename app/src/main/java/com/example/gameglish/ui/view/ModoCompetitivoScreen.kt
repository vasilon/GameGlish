package com.example.gameglish.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ModoCompetitivoScreen(
    navController: NavController,
    onHostGame: () -> Unit,
    onJoinGame: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Modo Competitivo", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))
        // Botón para crear una partida (hostear)
        Button(
            onClick = onHostGame,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Crear Partida")
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Botón para unirse a una partida existente
        Button(
            onClick = onJoinGame,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Unirse a Partida")
        }
    }
}
