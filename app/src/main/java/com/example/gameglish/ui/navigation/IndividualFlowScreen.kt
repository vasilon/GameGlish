// -----------------------------------------------------------------------------
// IndividualFlowScreen.kt
// -----------------------------------------------------------------------------
// Sub‑gráfico de navegación para el *Modo Individual* de GameGlish.
// Este flujo agrupa ejercicios de Vocabulario, Gramática y Reading bajo un
// NavController independiente para mantener las rutas contenidas.
// -----------------------------------------------------------------------------


package com.example.gameglish.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gameglish.ui.view.modoindividual.ModoIndividualMainScreen
import com.example.gameglish.ui.view.modoindividual.GramaticaScreen
import com.example.gameglish.ui.view.modoindividual.QuestionsScreen
import com.example.gameglish.ui.view.modoindividual.ReadingScreen
import com.example.gameglish.ui.view.modoindividual.VocabularioScreen

/**
 * Sub‑NavHost que orquesta las pantallas del modo de práctica individual.
 * @param modifier Se propaga para permitir ajustes de layout desde el padre.
 */

@Composable
fun IndividualFlowScreen(modifier: Modifier = Modifier) {

    // ──────────────────────────────────────────────────────────────────────────
    // 1) NavController propio para evitar colisiones con el grafo global.
    // ──────────────────────────────────────────────────────────────────────────
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "individual_main",
        modifier = modifier
    ) {
        // ------------------------------------------------------------------
        // Pantalla principal con tarjetas de acceso a cada tipo de ejercicio
        // ------------------------------------------------------------------
        composable("individual_main") {
            ModoIndividualMainScreen(
                navController = navController,
                onVocabularioClick = { navController.navigate("vocabulario") },
                onGramaticaClick  = { navController.navigate("gramatica") },
                onReadingClick    = { navController.navigate("reading") }
            )
        }
        // ------------------------------------------------------------------
        // Detalle: Vocabulario
        // ------------------------------------------------------------------

        composable("vocabulario") {
            VocabularioScreen(
                navController = navController,
                onStartExercise = {
                    // navegamos a QuestionsScreen con el parámetro "Vocabulario"
                    navController.navigate("questions/Vocabulario")
                }
            )
        }


        // ------------------------------------------------------------------
        // Detalle: Gramática
        // ------------------------------------------------------------------

        composable("gramatica") {
            GramaticaScreen(
                navController = navController,
                onStartExercise = {
                    // parámetro "Gramatica"
                    navController.navigate("questions/Gramatica")
                }
            )
        }


        // ------------------------------------------------------------------
        // Detalle: Reading
        // ------------------------------------------------------------------

        composable("reading") {
            ReadingScreen(
                navController = navController,
                onStartExercise = {
                    // parámetro "Reading"
                    navController.navigate("questions/Reading")
                }
            )
        }

        // ------------------------------------------------------------------
        // Pantalla genérica que muestra preguntas basada en el parámetro {tema}
        // Aprovechamos una única ruta para todos los ejercicios.
        // ------------------------------------------------------------------
        composable(
            route = "questions/{tema}",
            arguments = listOf(navArgument("tema") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val tema = backStackEntry.arguments?.getString("tema") ?: "Gramatica"
            QuestionsScreen(
                navController = navController,
                tema = tema
            )
        }
    }
}
