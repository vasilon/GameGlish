package com.example.gameglish.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.example.gameglish.ui.components.CustomBottomNavigationBar
import com.example.gameglish.ui.view.HomeScreen
import com.example.gameglish.ui.view.ProfileScreen

@Composable
fun MainFlowScreen() {
    // Creamos un navController local para el flujo principal
    val bottomNavController = rememberNavController()
    Scaffold(
        bottomBar = { CustomBottomNavigationBar(navController = bottomNavController) }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(navController = bottomNavController)
            }
            composable("competitivo") {
                // Este será el flujo Competitivo anidado
                CompetitivoFlowScreen()
            }
            composable("individual") {
                // Este será el flujo Individual anidado
                IndividualFlowScreen()
            }
            composable("profile") {
                ProfileScreen(navController = bottomNavController)
            }
        }
    }
}
