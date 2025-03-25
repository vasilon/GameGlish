// Kotlin
package com.example.gameglish.ui.navigation

import StatsScreen
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gameglish.ui.components.BackTopAppBar
import com.example.gameglish.ui.view.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object ModoIndividual : Screen("modo_individual")
    object ModoCompetitivo : Screen("modo_competitivo")
    object HostGame : Screen("host_game")
    object JoinGame : Screen("join_game")
    object CompetitiveGame : Screen("competitive_game")
    object Settings : Screen("settings")
    object Register : Screen("register")
    object Statistics : Screen("stats")
    object Ranking : Screen("ranking")
    object Vocabulario : Screen("vocabulario")
    object Gramatica : Screen("gramatica")
    object GramaticaQuestions : Screen("gramatica_questions")
    object FirstTimeRegistration : Screen("first_time_registration")
    object Profile : Screen("profile")
}

@Composable
fun GameGlishNavHost(
    navController: NavHostController,
    isUserLoggedIn: Boolean,
    isFirstLogin: Boolean
) {
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(
                navController = navController,
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        // First time registration composable.
        composable(Screen.FirstTimeRegistration.route) {
            val context = androidx.compose.ui.platform.LocalContext.current
            val scope = rememberCoroutineScope()
            FirstTimeRegistrationScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.FirstTimeRegistration.route) { inclusive = true }
                    }
                },
                registerUser = { nombre, nivelSeleccionado ->
                    val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
                        ?: return@FirstTimeRegistrationScreen
                    val repositoryUsuario = com.example.gameglish.data.repository.RepositoryUsuario(
                        com.example.gameglish.data.database.GameGlishDatabase.getDatabase(context)
                    )
                    scope.launch {
                        repositoryUsuario.actualizarUsuarioProfile(uid, nombre, nivelSeleccionado)
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.ModoCompetitivo.route) {
            ModoCompetitivoScreen(
                navController = navController,
                onHostGame = { navController.navigate(Screen.HostGame.route) },
                onJoinGame = { navController.navigate(Screen.JoinGame.route) }
            )
        }
        composable(Screen.HostGame.route) {
            // Pantalla para crear la partida, donde se invoca RepositoryCompetitivo.createGame
            HostGameScreen(navController = navController)
        }
        composable(Screen.JoinGame.route) {
            // Pantalla para unirse a una partida, por ejemplo, con un campo para ingresar el código de partida
            JoinGameScreen(navController = navController)
        }
        composable(Screen.CompetitiveGame.route) {
            CompetitiveGameScreen(navController = navController)
        }
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
        composable(Screen.Statistics.route) {
            StatsScreen(navController = navController)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }
        composable(Screen.Ranking.route) {
            GlobalRankingScreen(navController = navController)
        }
        composable(Screen.ModoIndividual.route) {
            ModoIndividualScreen(
                navController = navController,
                onVocabularioClick = { navController.navigate(Screen.Vocabulario.route) },
                onGramaticaClick = { navController.navigate(Screen.Gramatica.route) },
                onReadingClick = { /* Navigation code */ },
                onListeningClick = { /* Navigation code */ }
            )
        }
        composable(Screen.Vocabulario.route) {
            Scaffold(
                topBar = { BackTopAppBar(navController = navController, title = "Vocabulario") }
            ) { innerPadding ->
                VocabularioScreen(
                    navController = navController,
                    onStartExercise = { /* Navigation code */ }
                )
            }
        }
        composable(Screen.Gramatica.route) {
            Scaffold(
                topBar = { BackTopAppBar(navController = navController, title = "Gramática") }
            ) { innerPadding ->
                GramaticaScreen(
                    navController = navController,
                    onStartExercise = {
                        navController.navigate(Screen.GramaticaQuestions.route)
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
        composable(Screen.GramaticaQuestions.route) {
            GramaticaQuestionsScreen(navController = navController)
        }
    }
}


