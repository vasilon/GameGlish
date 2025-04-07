package com.example.gameglish.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gameglish.ui.components.BackTopAppBar

@Composable
fun ModoIndividualMainScreen(
    navController: NavController,
    onVocabularioClick: () -> Unit,
    onGramaticaClick: () -> Unit,
    onReadingClick: () -> Unit,
    onListeningClick: () -> Unit
) {
    Scaffold(
        topBar = { BackTopAppBar(navController = navController, title = "Modo Individual") }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Elige tu categoría",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            ElevatedButton(
                onClick = onVocabularioClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Vocabulario", style = MaterialTheme.typography.bodyLarge)
            }

            ElevatedButton(
                onClick = onGramaticaClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Gramática", style = MaterialTheme.typography.bodyLarge)
            }

            ElevatedButton(
                onClick = onReadingClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Reading", style = MaterialTheme.typography.bodyLarge)
            }

            ElevatedButton(
                onClick = onListeningClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Listening", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
