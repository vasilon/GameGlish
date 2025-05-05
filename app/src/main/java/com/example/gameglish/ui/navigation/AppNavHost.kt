package com.example.gameglish.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.gameglish.ui.view.auth.FirstTimeLoginScreen
import com.example.gameglish.ui.view.auth.LoginScreen
import com.example.gameglish.ui.view.auth.RegisterScreen
import com.example.gameglish.ui.view.SettingsScreen
import com.example.gameglish.ui.viewmodel.LoginViewModel
import com.google.firebase.auth.FirebaseAuth

sealed class Screen(val route: String) {
    // Auth Flow
    object Login : Screen("login")
    object Register : Screen("register")
    object FirstTimeLogin: Screen("first_time_login")
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
    loginViewModel: LoginViewModel
) {
    val isUserLoggedIn by loginViewModel.isUserLoggedIn.collectAsState()
    val isFirstLogin by loginViewModel.isFirstLogin.collectAsState()

    // Si el usuario está logueado:
    //   - Si es primer login, queremos mostrar el flujo de autenticación (authFlow)
    //   - Si no, mostramos mainFlow.
    val startDestination = if (isUserLoggedIn == true) {
        if (isFirstLogin == true) "authFlow" else "mainFlow"
    } else {
        "authFlow"
    }

    NavHost(navController = navController, startDestination = startDestination) {
        // Flujo de autenticación (destino directo del NavHost principal)
        navigation(
            // Seleccionamos dinámicamente el startDestination según el flag firstLogin:
            startDestination = if (isUserLoggedIn == true && isFirstLogin == true)
                Screen.FirstTimeLogin.route
            else
                Screen.Login.route,
            route = "authFlow"
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    navController = navController,
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) }
                )
            }
            composable(Screen.Register.route) {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate("authFlow") {
                            popUpTo("authFlow") { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.FirstTimeLogin.route) {
                FirstTimeLoginScreen(
                    onRegisterSuccess = {
                        navController.navigate("mainFlow") {
                            popUpTo(Screen.FirstTimeLogin.route) { inclusive = true }
                        }
                    },
                    registerUser = { nombre, nivelSeleccionado ->
                        val uid = FirebaseAuth.getInstance().currentUser?.uid
                        if (uid != null) {
                            loginViewModel.actualizarNombreUsuario(uid, nombre, nivelSeleccionado)
                            loginViewModel.marcarPrimerLoginCompleto()
                            navController.navigate("mainFlow") {
                                popUpTo(Screen.FirstTimeLogin.route) { inclusive = true }
                            }
                        }
                    }
                )
            }
            }
        // Flujo principal (con Bottom Navigation)
        composable("mainFlow") {
            MainFlowScreen(globalNavController = navController)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController)  // Asegúrate de que exista tu SettingsScreen
        }
    }

}
