// -----------------------------------------------------------------------------
// AppNavHost.kt
// -----------------------------------------------------------------------------
// Gráfico de navegación principal de GameGlish usando Navigation‑Compose.
// Gestiona dos flujos bien diferenciados:
//   • authFlow  → Pantallas de autenticación/registro y primer login.
//   • mainFlow  → Pantallas de la app con BottomNav.
//
// Selecciona dinámicamente el flujo inicial en función de:
//   • isUserLoggedIn  (¿hay sesión activa?)
//   • isFirstLogin    (¿es la primera vez que el usuario entra?)
// -----------------------------------------------------------------------------


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
import com.example.gameglish.ui.view.main.SettingsScreen
import com.example.gameglish.ui.viewmodel.LoginViewModel
import com.google.firebase.auth.FirebaseAuth

// -----------------------------------------------------------------------------
// 1) Declaración de rutas tipadas (selladas) para evitar errores de strings.
// -----------------------------------------------------------------------------

sealed class Screen(val route: String) {
    // Auth Flow
    object Login : Screen("login")
    object Register : Screen("register")
    object FirstTimeLogin: Screen("first_time_login")
    // Main Flow
    object Home : Screen("home")

    /**
     * Ruta con argumento. Se expone helper `createRoute()` para construirla
     * evitando concatenaciones manuales.
     */
    object CompetitiveGame : Screen("competitive_game/{gameId}") {
        fun createRoute(gameId: String) = "competitive_game/$gameId"
    }
    object Settings : Screen("settings")
    object Profile : Screen("profile")
}

// -----------------------------------------------------------------------------
// 2) Composable raíz que monta NavHost y decide el grafo inicial.
// -----------------------------------------------------------------------------

@Composable
fun AppNavHost(
    navController: NavHostController,
    loginViewModel: LoginViewModel
) {

    // Observamos StateFlow como State para recomposiciones reactivas.
    val isUserLoggedIn by loginViewModel.isUserLoggedIn.collectAsState()
    val isFirstLogin by loginViewModel.isFirstLogin.collectAsState()
    /* ---------------------------------------------------------------
     *  Lógica de elección del grafo de inicio
     * ---------------------------------------------------------------
     *  • Usuario NO logueado   → authFlow
     *  • Usuario logueado y  1ª vez → authFlow (pantalla FirstTimeLogin)
     *  • Usuario logueado y NO 1ª vez → mainFlow
     * --------------------------------------------------------------- */
    val startDestination = if (isUserLoggedIn == true) {
        if (isFirstLogin == true) "authFlow" else "mainFlow"
    } else {
        "authFlow"
    }

    /* ---------------------------------------------------------------
    *  NavHost principal
    * --------------------------------------------------------------- */

    NavHost(navController = navController, startDestination = startDestination) {
        /* -----------------------------------------------------------
        *  AUTH FLOW
        * -----------------------------------------------------------
        *  El destino startDestination interno depende de si venimos
        *  de un primer login obligatorio o del login normal.
        * ----------------------------------------------------------- */
        navigation(
            // Seleccionamos dinámicamente el startDestination según el flag firstLogin:
            startDestination = if (isUserLoggedIn == true && isFirstLogin == true)
                Screen.FirstTimeLogin.route
            else
                Screen.Login.route,
            route = "authFlow"
        ) {
            // --- Login --------------------------------------------------
            composable(Screen.Login.route) {
                LoginScreen(
                    navController = navController,
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) }
                )
            }
            // --- Registro ----------------------------------------------
            composable(Screen.Register.route) {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate("authFlow") {
                            popUpTo("authFlow") { inclusive = true }
                        }
                    }
                )
            }
            // --- Primer login ------------------------------------------
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
        /* -----------------------------------------------------------
         *  MAIN FLOW (Pantalla con Bottom Navigation)
         * ----------------------------------------------------------- */
        composable("mainFlow") {
            MainFlowScreen(globalNavController = navController)
        }
        /* -----------------------------------------------------------
         *  Ajustes (desde icono engranaje, por ejemplo)
         * ----------------------------------------------------------- */
        composable(Screen.Settings.route) {
            SettingsScreen(navController)
        }
    }

}