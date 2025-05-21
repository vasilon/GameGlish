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
import com.example.gameglish.ui.view.modoindividual.ListeningScreen
import com.example.gameglish.ui.view.modoindividual.ReadingScreen
import com.example.gameglish.ui.view.modoindividual.VocabularioScreen

@Composable
fun IndividualFlowScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "individual_main",
        modifier = modifier
    ) {
        composable("individual_main") {
            ModoIndividualMainScreen(
                navController = navController,
                onVocabularioClick = { navController.navigate("vocabulario") },
                onGramaticaClick  = { navController.navigate("gramatica") },
                onReadingClick    = { navController.navigate("reading") }
            )
        }

        composable("vocabulario") {
            VocabularioScreen(
                navController = navController,
                onStartExercise = {
                    // navegamos a QuestionsScreen con el parámetro "Vocabulario"
                    navController.navigate("questions/Vocabulario")
                }
            )
        }

        composable("gramatica") {
            GramaticaScreen(
                navController = navController,
                onStartExercise = {
                    // parámetro "Gramatica"
                    navController.navigate("questions/Gramatica")
                }
            )
        }

        composable("reading") {
            ReadingScreen(
                navController = navController,
                onStartExercise = {
                    // parámetro "Reading"
                    navController.navigate("questions/Reading")
                }
            )
        }

        // Ruta única para mostrar preguntas de cualquier tema
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
