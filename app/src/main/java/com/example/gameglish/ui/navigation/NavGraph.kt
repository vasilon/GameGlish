package com.example.gameglish.ui.navigation

import HomeScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gameglish.ui.view.*

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object ModoIndividual : Screen("modo_individual")
    object ModoCompetitivo : Screen("modo_competitivo")
    object Settings : Screen("settings")
}

@Composable
fun GameGlishNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.ModoIndividual.route) {
            ModoIndividualScreen()
        }
        composable(Screen.ModoCompetitivo.route) {
            ModoCompetitivoScreen()
        }
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
    }
}
