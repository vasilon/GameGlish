// -----------------------------------------------------------------------------
// CompetitivoFlowScreen.kt
// -----------------------------------------------------------------------------
// Sub‑grafo de navegación dedicado al modo competitivo 1 vs 1.
// Usa un NavController independiente para encapsular las rutas internas y no
// contaminar el grafo global.
// -----------------------------------------------------------------------------

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

/**
 * Monta el NavHost específico del modo *Competitivo*.
 *
 * @param modifier Permite heredar/modificar el tamaño desde el padre.
 *                 Se pasa downstream a `NavHost`.
 */

@Composable
fun CompetitivoFlowScreen(modifier: Modifier = Modifier) {
    // ──────────────────────────────────────────────────────────────────────────
    // 1) NavController local → evita interferir con el NavController global.
    // -------------------------------------------------------------------------
    val competitivoNavController = rememberNavController()
    NavHost(
        navController = competitivoNavController,
        startDestination = "competitivo_main",
        modifier = modifier
    ) {
        // ---------------------------------------------------------------------
        // 2) Pantalla principal del modo competitivo.
        //    Ofrece «Crear partida» o «Unirse a partida».
        // ---------------------------------------------------------------------
        composable("competitivo_main") {

            ModoCompetitivoMainScreen(
                onHostGame = { competitivoNavController.navigate("host_game") },
                onJoinGame = { competitivoNavController.navigate("join_game") }
            )
        }
        // ---------------------------------------------------------------------
        // 3) Pantalla para alojar una partida nueva (HOST).
        // ---------------------------------------------------------------------
        composable("host_game") {
            HostGameScreen(navController = competitivoNavController)
        }
        // ---------------------------------------------------------------------
        // 4) Pantalla para unirse a una partida existente (JOINER).
        // ---------------------------------------------------------------------
        composable("join_game") {
            JoinGameScreen(navController = competitivoNavController)
        }
        // ---------------------------------------------------------------------
        // 5) Pantalla de juego en curso.
        //    Recibe el ID de la partida como argumento de ruta.
        // ---------------------------------------------------------------------
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
