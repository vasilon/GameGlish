package com.example.gameglish.ui.view

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.gameglish.ui.components.CustomBottomNavigationBar
import com.example.gameglish.ui.navigation.Screen
import com.example.gameglish.ui.navigation.MainNavHost
import androidx.navigation.NavHostController

@Composable
fun MainScreen(navController: NavHostController) {
    // Create a nested NavController for main content.
    val mainNavController = rememberNavController()
    Scaffold(
        bottomBar = {
            val currentBackStackEntry = mainNavController.currentBackStackEntryAsState().value
            val currentRoute = currentBackStackEntry?.destination?.route
            CustomBottomNavigationBar(
                navController = mainNavController,
                currentRoute = currentRoute
            )
        }
    ) { innerPadding ->
        MainNavHost(
            navController = mainNavController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
