package com.example.gameglish

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.example.gameglish.ui.navigation.AppNavHost
import com.example.gameglish.ui.theme.GameGlishTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

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
    val auth = FirebaseAuth.getInstance()
    // You can determine if the user is logged in and if it's their first login.
    // For now, we pass false for isFirstLogin. Adjust this logic as needed.
    GameGlishTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            AppNavHost(
                navController = navController,
                isUserLoggedIn = auth.currentUser != null,
                isFirstLogin = false
            )
        }
    }
}
