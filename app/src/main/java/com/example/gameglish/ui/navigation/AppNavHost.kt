package com.example.gameglish.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.example.gameglish.data.database.GameGlishDatabase
import com.example.gameglish.data.repository.RepositoryUsuario
import com.example.gameglish.ui.components.BackTopAppBar
import com.example.gameglish.ui.view.FirstTimeRegistrationScreen
import com.example.gameglish.ui.view.LoginScreen
import com.example.gameglish.ui.view.RegisterScreen
import com.example.gameglish.ui.view.HomeScreen
import com.example.gameglish.ui.view.ModoCompetitivoScreen
import com.example.gameglish.ui.view.HostGameScreen
import com.example.gameglish.ui.view.JoinGameScreen
import com.example.gameglish.ui.view.CompetitiveGameScreen
import com.example.gameglish.ui.view.SettingsScreen
import com.example.gameglish.ui.view.StatsScreen
import com.example.gameglish.ui.view.ProfileScreen
import com.example.gameglish.ui.view.GlobalRankingScreen
import com.example.gameglish.ui.view.ModoIndividualScreen
import com.example.gameglish.ui.view.VocabularioScreen
import com.example.gameglish.ui.view.GramaticaScreen
import com.example.gameglish.ui.view.GramaticaQuestionsScreen
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


sealed class Screen(val route: String) {
    // Auth Flow
    object Login : Screen("login")
    object Register : Screen("register")
    object FirstTimeRegistration : Screen("first_time_registration")
    // Main Flow
    object Home : Screen("home")
    object ModoCompetitivo : Screen("modo_competitivo")
    object HostGame : Screen("host_game")
    object JoinGame : Screen("join_game")
    object CompetitiveGame : Screen("competitive_game/{gameId}") {
        fun createRoute(gameId: String) = "competitive_game/$gameId"
    }
    object Settings : Screen("settings")
    object Statistics : Screen("stats")
    object Ranking : Screen("ranking")
    object Vocabulario : Screen("vocabulario")
    object Gramatica : Screen("gramatica")
    object GramaticaQuestions : Screen("gramatica_questions")
    object ModoIndividual : Screen("modo_individual")
    object Profile : Screen("profile")
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    isUserLoggedIn: Boolean,
    isFirstLogin: Boolean
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    // Determine the starting destination based on login state:
    val startDestination = when {
        !isUserLoggedIn -> "authFlow"
        isFirstLogin -> Screen.FirstTimeRegistration.route
        else -> "mainFlow"
    }

    NavHost(navController = navController, startDestination = startDestination) {
        // ---------- Auth Flow ----------
        navigation(
            startDestination = Screen.Login.route,
            route = "authFlow"
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    navController = navController,
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                    onLoginSuccess = { userHasProfile ->
                        if (userHasProfile) {
                            navController.navigate("mainFlow") {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        } else {
                            navController.navigate(Screen.FirstTimeRegistration.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
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
        }
        // ---------- First Time Registration Flow ----------
        composable(Screen.FirstTimeRegistration.route) {
            FirstTimeRegistrationScreen(
                onRegisterSuccess = {
                    navController.navigate("mainFlow") {
                        popUpTo(Screen.FirstTimeRegistration.route) { inclusive = true }
                    }
                },
                registerUser = { nombre, nivelSeleccionado ->
                    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@FirstTimeRegistrationScreen
                    // Create a RepositoryUsuario using the current context's database instance
                    val repositoryUsuario = RepositoryUsuario(GameGlishDatabase.getDatabase(context))
                    scope.launch {
                        repositoryUsuario.actualizarUsuarioProfile(uid, nombre, nivelSeleccionado)
                    }

                }
            )
        }
        // ---------- Main Flow (with Bottom Bar, etc.) ----------
        navigation(
            startDestination = Screen.Home.route,
            route = "mainFlow"
        ) {
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
                HostGameScreen(navController = navController)
            }
            composable(Screen.JoinGame.route) {
                JoinGameScreen(navController = navController)
            }
            composable(
                route = Screen.CompetitiveGame.route,
                arguments = listOf(navArgument("gameId") { type = NavType.StringType })
            ) { backStackEntry ->
                val gameId = backStackEntry.arguments?.getString("gameId") ?: ""
                CompetitiveGameScreen(navController = navController, gameId = gameId)
            }
            composable(Screen.Settings.route) { SettingsScreen() }
            composable(Screen.Statistics.route) { StatsScreen(navController = navController) }
            composable(Screen.Profile.route) { ProfileScreen(navController = navController) }
            composable(Screen.Ranking.route) { GlobalRankingScreen(navController = navController) }
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
                        onStartExercise = { navController.navigate(Screen.GramaticaQuestions.route) },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
            composable(Screen.GramaticaQuestions.route) {
                GramaticaQuestionsScreen(navController = navController)
            }
        }
    }
}
