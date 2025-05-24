// -----------------------------------------------------------------------------
// MainActivity.kt
// -----------------------------------------------------------------------------
// Punto de entrada de la aplicación GameGlish.
// • Inicializa Firebase.
// • Monta el árbol Compose.
//
// La función composable `GameGlishApp()` aplica el tema y configura el
// NavController junto al AppNavHost.
// -----------------------------------------------------------------------------

package com.example.gameglish

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.example.gameglish.ui.navigation.AppNavHost
import com.example.gameglish.ui.theme.GameGlishTheme
import com.example.gameglish.ui.viewmodel.LoginViewModel
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        setContent {
            GameGlishApp()
        }
    }
}

@Composable
fun GameGlishApp() {
    val navController = rememberNavController()
    GameGlishTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            AppNavHost(
                navController = navController,
                loginViewModel = LoginViewModel(
                    application = LocalContext.current.applicationContext as Application
                ),
            )
        }
    }
}
