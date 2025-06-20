
// -----------------------------------------------------------------------------
// BottomNavItem.kt
// -----------------------------------------------------------------------------
// Composable de barra de navegación inferior personalizada para GameGlish.
// Está diseñada a mano en lugar de usar NavigationBar de M3 para permitir
// aplicar un color de fondo diferente cuando un ítem está seleccion...


package com.example.gameglish.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SportsEsports

data class BottomNavItem(
    val label: String,
    val route: String,
    val icon: ImageVector,
    // Se mantiene el color asignado para el estado seleccionado,
    // pero se pueden crear listas similares para que concuerden con el tema.
    val selectedBackgroundColor: Color,
    val selectedIconColor: Color = Color.White
)

@Composable
fun CustomBottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem(
            label = "Inicio",
            route = "home",
            icon = Icons.Filled.Home,
            selectedBackgroundColor = Color(0xFF2196F3)
        ),
        BottomNavItem(
            label = "Competitivo",
            route = "competitivo",
            icon = Icons.Filled.SportsEsports,
            selectedBackgroundColor = Color(0xFFE50D45)
        ),
        BottomNavItem(
            label = "Individual",
            route = "individual",
            icon = Icons.Filled.Person,
            selectedBackgroundColor = Color(0xFF43A047)
        ),
        BottomNavItem(
            label = "Ranking",
            route = "ranking",
            icon = Icons.Filled.Star,
            selectedBackgroundColor = Color(0xFFF39000)
        ),
        BottomNavItem(
            label = "Perfil",
            route = "profile",
            icon = Icons.Filled.AccountCircle,
            selectedBackgroundColor = Color(0xFF6A1B9A)
        )
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Row(
        modifier = Modifier
            .fillMaxWidth()
            // Usamos el color de superficie definido en el tema para el fondo general.
            .background(MaterialTheme.colorScheme.surface),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        // Si está seleccionado, se muestra el color asignado; si no, transparente.
                        color = if (selected) item.selectedBackgroundColor else Color.Transparent
                    )
                    .clickable {
                        navController.navigate(item.route) {
                            launchSingleTop = true
                        }
                    }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (selected)
                            item.selectedIconColor
                        else
                        // Se usa el color onSurface del tema con menor opacidad para no seleccionados.
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.size(30.dp)
                    )
                    Text(
                        text = item.label,
                        fontSize = 12.sp,
                        color = if (selected)
                            item.selectedIconColor
                        else
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}
