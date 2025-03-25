package com.example.gameglish.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun CompetitiveGameScreen(navController: NavController) {
    // Aquí obtendrías el estado del juego desde el ViewModel que escucha Firebase.
    // Por simplicidad, mostramos un texto de ejemplo:
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Partida en curso", style = MaterialTheme.typography.headlineMedium)
        // Aquí se mostrarían la pregunta actual, vidas, temporizador, etc.
        // También botones para enviar respuestas y ver resultados.
    }
}