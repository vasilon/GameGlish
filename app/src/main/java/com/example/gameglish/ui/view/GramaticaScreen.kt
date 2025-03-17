package com.example.gameglish.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gameglish.ui.navigation.Screen

@Composable
fun GramaticaScreen(
    navController: NavController,
    onStartExercise: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Gramática",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Perfecciona tus habilidades gramaticales con ejercicios de tiempos verbales, uso de preposiciones, formación de oraciones y mucho más.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                // Navigate directly, the questions will be loaded in the destination screen.
                navController.navigate(Screen.GramaticaQuestions.route)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar Ejercicio")
        }
    }
}