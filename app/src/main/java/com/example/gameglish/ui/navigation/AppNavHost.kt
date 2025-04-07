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
import com.example.gameglish.ui.view.HomeScreen
import com.example.gameglish.ui.view.JoinGameScreen
import com.example.gameglish.ui.view.LoginScreen
import com.example.gameglish.ui.view.ModoCompetitivoMainScreen
import com.example.gameglish.ui.view.ModoIndividualMainScreen
import com.example.gameglish.ui.view.ProfileScreen
import com.example.gameglish.ui.view.RegisterScreen
import com.example.gameglish.ui.view.SettingsScreen
import com.example.gameglish.ui.view.StatsScreen
import com.example.gameglish.ui.view.GlobalRankingScreen
import com.example.gameglish.ui.view.VocabularioScreen
import com.example.gameglish.ui.view.GramaticaScreen
import com.example.gameglish.ui.view.GramaticaQuestionsScreen
import com.example.gameglish.ui.view.HostGameScreen
import com.example.gameglish.ui.view.CompetitiveGameScreen
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
    // Contexto y scope
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val startDestination = when {
        !isUserLoggedIn -> "authFlow"
        isFirstLogin -> Screen.FirstTimeRegistration.route
        else -> "mainFlow"
    }

    NavHost(navController = navController, startDestination = startDestination) {
        // ---------- Authentication Flow ----------
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
                    val repositoryUsuario = RepositoryUsuario(GameGlishDatabase.getDatabase(context))
                    scope.launch {
                        repositoryUsuario.actualizarUsuarioProfile(uid, nombre, nivelSeleccionado)
                    }
                }
            )
        }

        // ---------- Main Flow (with Bottom Navigation) ----------
        // Aquí solo delegamos a MainFlowScreen, que gestiona internamente todas las rutas principales.
        composable("mainFlow") {
            MainFlowScreen()
        }
    }
}
