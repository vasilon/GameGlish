// Kotlin
package com.example.gameglish

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.example.gameglish.data.database.GameGlishDatabase
import com.example.gameglish.data.repository.RepositoryUsuario
import com.example.gameglish.ui.navigation.GameGlishNavHost
import com.example.gameglish.ui.theme.GameGlishTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            GameGlishApp()
        }
    }
}

@Composable
fun GameGlishApp() {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()
    GameGlishTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            GameGlishNavHost(
                navController = navController,
                isUserLoggedIn = auth.currentUser != null,
                isFirstLogin = false
            )
        }
    }
}