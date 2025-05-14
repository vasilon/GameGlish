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
import com.example.gameglish.ui.theme.GameGlishTheme
import com.example.gameglish.ui.theme.ScreenColor
import com.example.gameglish.ui.view.main.HomeScreen
import com.example.gameglish.ui.view.main.ProfileScreen
import com.example.gameglish.ui.view.main.GlobalRankingScreen
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
                GameGlishTheme(
                    screen = ScreenColor.Home,
                    dynamicColor = false
                ) {
                    HomeScreen(navController = globalNavController)
                }
            }
            composable("competitivo") {
                GameGlishTheme(
                    screen = ScreenColor.Competitivo,
                    dynamicColor = false
                ) {
                    CompetitivoFlowScreen()
                }
            }
            composable("individual") {
                GameGlishTheme(
                    screen = ScreenColor.Individual,
                    dynamicColor = false
                ) {
                    IndividualFlowScreen()
                }
            }
            composable("ranking") {

                GameGlishTheme(
                    screen = ScreenColor.Ranking,
                    dynamicColor = false
                ) {
                    GlobalRankingScreen(navController = bottomNavController)
                }
            }
                composable("profile") {

                    GameGlishTheme(
                        screen = ScreenColor.Profile,
                        dynamicColor = false
                    ) {
                        ProfileScreen(navController = bottomNavController)
                    }
                }
            }
        }
    }

@Composable
fun MainNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }

    }
}

