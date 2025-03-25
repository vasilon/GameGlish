package com.example.gameglish.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
            Text("Host your competitive game", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    viewModel.createGame { gameId ->
                        if (gameId.isNotEmpty()) {
                            navController.navigate(Screen.CompetitiveGame.route + "/$gameId")
                        } else {
                            // Manejo de error (puedes mostrar un Toast o Snackbar)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Crear Partida")
            }
        }
    }
}
