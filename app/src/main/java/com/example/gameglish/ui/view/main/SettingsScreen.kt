// -----------------------------------------------------------------------------
// SettingsScreen.kt
// -----------------------------------------------------------------------------
// Pantalla de ajustes de la aplicación GameGlish.
// Permite al usuario ver información de la aplicación y acceder a opciones como
// "Acerca de" o "Política de privacidad". Utiliza un diseño con fondo degradado
// y una lista desplazable para organizar los elementos de configuración.
// -----------------------------------------------------------------------------


package com.example.gameglish.ui.view.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.gameglish.ui.components.BackTopAppBar


@Composable
fun SettingsScreen(navController: NavController) {
    // Usamos BackTopAppBar (ya creado) para volver atrás, el TopBar se conserva.
    Scaffold(
        topBar = {
            BackTopAppBar(
                navController = navController,
                title = "Ajustes"
            )
        },
        content = { paddingValues ->
            // Fondo oscuro en degradado similar a GlobalRankingScreen.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF1D1F3E), Color(0xFF25294E))
                        )
                    )
                    .padding(paddingValues)
            ) {
                // Usamos LazyColumn para que la pantalla sea desplazable si hay muchos ítems.
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Sección Información
                    item {
                        Text(
                            text = "Información",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF2F3256)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Versión: 1.0.0",
                                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.White)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Desarrollado por: Javier Huélamo Gracia",
                                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.White)
                                )
                            }
                        }
                    }
                    // Sección Acerca de
                    item {
                        Button(
                            onClick = { /* Navegar a pantalla "Acerca de" o mostrar diálogo */ },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107))
                        ) {
                            Text("Acerca de", color = Color.Black)
                        }
                    }
                }
            }
        }
    )
}
