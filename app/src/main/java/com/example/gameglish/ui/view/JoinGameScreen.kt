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
fun JoinGameScreen(
    navController: NavController,
    viewModel: CompetitiveGameViewModel = viewModel()
) {
    var gameCode by remember { mutableStateOf("") }
    val context = LocalContext.current

    Scaffold(
        topBar = { BackTopAppBar(navController = navController, title = "Unirse a Partida") }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
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
        }
    }
}
