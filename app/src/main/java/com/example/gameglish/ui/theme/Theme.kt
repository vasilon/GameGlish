package com.example.gameglish.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

val Shapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(16.dp)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF2196F3),     // Azul para la TopBar, botones, etc.
    secondary = Color(0xFF25294E),
    background = Color(0xFF1D1F3E),
    surface = Color(0xFF2F3256),
    // surfaceVariant servirá para contenedores que destaquen más que surface
    surfaceVariant = Color(0xFF3B3F6C),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(

    primary = Color(0xFF2196F3),     // Mismo azul para modo claro
    secondary = Color(0xFF90CAF9),
    background = Color(0xFFF5F5F5),
    surface = Color.White,
    // surfaceVariant para contenedores en modo claro, un tono gris muy claro
    surfaceVariant = Color(0xFFF0F0F0),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black
)

@Composable
fun GameGlishTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }


    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
