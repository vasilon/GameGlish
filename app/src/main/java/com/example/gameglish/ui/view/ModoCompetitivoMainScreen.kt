package com.example.gameglish.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gameglish.ui.components.BackTopAppBar

@Composable
fun ModoCompetitivoMainScreen(
    onHostGame: () -> Unit,
    onJoinGame: () -> Unit
) {
    // Diseño moderno: se muestran tres botones o tarjetas para cada opción.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text("Modo Competitivo", style = MaterialTheme.typography.headlineMedium)
        Button(
            onClick = onHostGame,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Crear Partida")
        }
        Button(
            onClick = onJoinGame,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Unirse a Partida")
        }
    }
}


