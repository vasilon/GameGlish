package com.example.gameglish.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gameglish.ui.view.modocompetitivo.CompetitiveGameScreen
import com.example.gameglish.ui.view.modocompetitivo.HostGameScreen
import com.example.gameglish.ui.view.modocompetitivo.JoinGameScreen
import com.example.gameglish.ui.view.modocompetitivo.ModoCompetitivoMainScreen

@Composable
fun CompetitivoFlowScreen(modifier: Modifier = Modifier) {
    // NavController local para el flujo competitivo
    val competitivoNavController = rememberNavController()
    NavHost(
        navController = competitivoNavController,
        startDestination = "competitivo_main",
        modifier = modifier
    ) {
        composable("competitivo_main") {

            ModoCompetitivoMainScreen(
                onHostGame = { competitivoNavController.navigate("host_game") },
                onJoinGame = { competitivoNavController.navigate("join_game") }
            )
        }
        composable("host_game") {
            HostGameScreen(navController = competitivoNavController)
        }
        composable("join_game") {
            JoinGameScreen(navController = competitivoNavController)
        }
        composable(
            route = Screen.CompetitiveGame.route, // "competitive_game/{gameId}"
            arguments = listOf(
                navArgument("gameId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val gameId = backStackEntry.arguments!!.getString("gameId")!!
            CompetitiveGameScreen(
                gameId = gameId,
                navController = competitivoNavController
            )
        }

    }
}
