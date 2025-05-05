package com.example.gameglish.ui.view.modoindividual

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gameglish.ui.components.BackTopAppBar

@Composable
fun VocabularioScreen(
    navController: NavController,
    onStartExercise: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        BackTopAppBar(navController = navController, title = "Vocabulario")
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Mejora tu vocabulario con ejercicios de palabras clave, sinónimos, antónimos y más. ¡Desafía tu memoria y aprende nuevas expresiones!",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onStartExercise,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar Ejercicio")
        }
    }
}
