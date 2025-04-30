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

// -------------- seed-colors que usarás desde el NavHost ----------------
enum class ScreenColor(val seed: Color) {
    Home        (Color(0xFF2196F3)),   // azul
    Competitivo (Color(0xFFE50D45)),   // rojo
    Individual  (Color(0xFF43A047)),   // verde
    Ranking     (Color(0xFFF39000)),   // naranja
    Profile     (Color(0xFF6A1B9A))    // morado
}

// -------------- helpers de esquema (ya los tenías) ---------------------
private fun lightScheme(seed: Color): ColorScheme = lightColorScheme(
    primary            = seed,
    onPrimary          = Color.White,
    primaryContainer   = seed.copy(alpha = 0.25f),
    onPrimaryContainer = Color.Black,

    secondary          = seed,
    onSecondary        = Color.White,

    background         = Color(0xFFF5F5F5),
    surface            = Color.White,
    surfaceVariant     = Color(0xFFF0F0F0),
    onBackground       = Color.Black,
    onSurface          = Color.Black
)

private fun darkScheme(seed: Color): ColorScheme = darkColorScheme(
    primary            = seed,
    onPrimary          = Color.White,
    primaryContainer   = seed.darken(0.25f),
    onPrimaryContainer = Color.White,

    secondary          = seed,
    onSecondary        = Color.Black,

    background         = Color(0xFF1D1F3E),
    surface            = Color(0xFF25294E),
    surfaceVariant     = Color(0xFF3B3F6C),
    onBackground       = Color.White,
    onSurface          = Color.White
)

// oscurecer un color un % determinado
private fun Color.darken(amount: Float = .2f): Color =
    copy(
        red   = (red   * (1f - amount)).coerceIn(0f, 1f),
        green = (green * (1f - amount)).coerceIn(0f, 1f),
        blue  = (blue  * (1f - amount)).coerceIn(0f, 1f)
    )

// --------------------------- Shape global --------------------------------
val Shapes = Shapes(
    small  = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(8.dp),
    large  = RoundedCornerShape(16.dp)
)

// =========================== THEME FINAL ================================
@Composable
fun GameGlishTheme(
    screen: ScreenColor = ScreenColor.Home,          // ← color de la vista
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,                   // Material-You opcional
    content: @Composable () -> Unit
) {
    // 1. Elegir esquema
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val ctx = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
        }
        darkTheme -> darkScheme(screen.seed)
        else      -> lightScheme(screen.seed)
    }

    // 2. Tintar status-bar para que combine
    val view = LocalView.current
    if (!view.isInEditMode) {
        val window = (view.context as Activity).window
        window.statusBarColor = colorScheme.primary.toArgb()
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
    }

    // 3. Aplicar tema
    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        shapes      = Shapes,
        content     = content
    )
}
