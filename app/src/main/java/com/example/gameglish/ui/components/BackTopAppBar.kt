// -----------------------------------------------------------------------------
// BackTopAppBar.kt
// -----------------------------------------------------------------------------
// Composable reutilizable que muestra una barra de aplicación (TopAppBar) con
// un botón de navegación «Atrás». Se emplea en aquellas pantallas donde se
// necesita volver a la vista anterior utilizando NavController.
// -----------...


package com.example.gameglish.ui.components
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackTopAppBar(navController: NavController, title: String) {
    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }
    )
}
