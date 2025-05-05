package com.example.gameglish.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gameglish.ui.view.modoindividual.ModoIndividualMainScreen
import com.example.gameglish.ui.view.modoindividual.GramaticaScreen
import com.example.gameglish.ui.view.modoindividual.GramaticaQuestionsScreen
import com.example.gameglish.ui.view.modoindividual.ListeningScreen
import com.example.gameglish.ui.view.modoindividual.ReadingScreen
import com.example.gameglish.ui.view.modoindividual.VocabularioScreen

@Composable
fun IndividualFlowScreen(modifier: Modifier = Modifier) {
    val individualNavController = rememberNavController()
    NavHost(
        navController = individualNavController,
        startDestination = "individual_main",
        modifier = modifier
    ) {
        composable("individual_main") {
            ModoIndividualMainScreen(
                navController = individualNavController,
                onVocabularioClick = { individualNavController.navigate("vocabulario") },
                onGramaticaClick = { individualNavController.navigate("gramatica") },
                onReadingClick = { individualNavController.navigate("reading") },
                onListeningClick = { individualNavController.navigate("listening") }
            )
        }
        composable("vocabulario") {
            VocabularioScreen(navController = individualNavController,   onStartExercise = { individualNavController.navigate("gramatica_questions") })
        }
        composable("gramatica") {
            GramaticaScreen(
                navController = individualNavController,
                onStartExercise = { individualNavController.navigate("gramatica_questions") }
            )
        }
        composable("gramatica_questions") {
            GramaticaQuestionsScreen(navController = individualNavController)
        }
        composable("reading") {
            ReadingScreen(navController = individualNavController,
                onStartExercise = { individualNavController.navigate("gramatica_questions") })
        }
        composable("listening") {
            ListeningScreen(navController = individualNavController,
                onStartExercise = { individualNavController.navigate("gramatica_questions") })
        }
    }
}


