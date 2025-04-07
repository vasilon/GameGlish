package com.example.gameglish.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gameglish.ui.view.ModoIndividualMainScreen
import com.example.gameglish.ui.view.GramaticaScreen
import com.example.gameglish.ui.view.GramaticaQuestionsScreen
import com.example.gameglish.ui.view.ListeningScreen
import com.example.gameglish.ui.view.ReadingScreen
import com.example.gameglish.ui.view.VocabularioScreen

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
            VocabularioScreen(navController = individualNavController, onStartExercise = { /* Código */ })
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
            ReadingScreen(navController = individualNavController)
        }
        composable("listening") {
            ListeningScreen(navController = individualNavController)
        }
    }
}


