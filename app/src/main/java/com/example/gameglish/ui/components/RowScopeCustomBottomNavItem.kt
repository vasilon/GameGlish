package com.example.gameglish.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*


import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.gameglish.ui.navigation.Screen

// --- Definición de los items de navegación inferior ---

data class BottomNavItem(
    val label: String,
    val route: String,
    val icon: ImageVector,
    val selectedBackgroundColor: Color,
    val selectedIconColor: Color = Color.White
)

// --- Custom Bottom Navigation Bar ---

@Composable
fun CustomBottomNavigationBar(navController: NavHostController, currentRoute: String?) {
    val items = listOf(
        BottomNavItem("Home", Screen.Home.route, Icons.Filled.Home, Color(0xFFE50D45)),
        BottomNavItem("Individual", Screen.ModoIndividual.route, Icons.Filled.AccountCircle, Color(0xFF229840)),
        BottomNavItem("Competitivo", Screen.ModoCompetitivo.route, Icons.Filled.SportsEsports, Color(0xFF4298D3)),
        BottomNavItem("Ranking", Screen.Ranking.route, Icons.Filled.Star, Color(0xFFF39000)),
        BottomNavItem("Stats", Screen.Statistics.route, Icons.Filled.Bookmarks, Color(0xFF6A1B9A))
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route
            RowScopeCustomBottomNavItem(
                item = item,
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

@Composable
fun RowScope.RowScopeCustomBottomNavItem(
    item: BottomNavItem,
    selected: Boolean,
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .weight(1f)
            .padding(4.dp)
            .background(if (selected) item.selectedBackgroundColor else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = if (selected) item.selectedIconColor else Color.Gray,
                modifier = Modifier.size(30.dp)
            )
            Text(
                text = item.label,
                fontSize = 12.sp,
                color = if (selected) item.selectedIconColor else Color.Gray
            )
        }
    }
}
