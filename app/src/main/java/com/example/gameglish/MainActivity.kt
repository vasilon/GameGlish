package com.example.gameglish

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.Surface

import androidx.compose.runtime.Composable

import androidx.navigation.compose.rememberNavController
import com.example.gameglish.ui.navigation.GameGlishNavHost
import com.example.gameglish.ui.theme.GameGlishTheme

// MainActivity.kt
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            GameGlishNavHost(navController = navController)
        }
    }
}
