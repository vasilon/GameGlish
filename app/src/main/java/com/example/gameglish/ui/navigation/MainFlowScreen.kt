package com.example.gameglish.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.example.gameglish.ui.components.CustomBottomNavigationBar
import com.example.gameglish.ui.view.HomeScreen
import com.example.gameglish.ui.view.ProfileScreen
import com.example.gameglish.ui.view.GlobalRankingScreen
@Composable
fun MainFlowScreen(globalNavController: NavHostController) {
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
                // Usa el globalNavController, que conoce "login"
                HomeScreen(navController = globalNavController)
            }
            composable("competitivo") {
                CompetitivoFlowScreen()
            }
            composable("individual") {
                IndividualFlowScreen()
            }
            composable("ranking") {
                GlobalRankingScreen(navController = bottomNavController)
            }
            composable("profile") {
                ProfileScreen(navController = bottomNavController)
            }
        }
    }
}