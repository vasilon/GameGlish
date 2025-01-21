import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gameglish.R

@Composable
fun HomeScreen(navController: NavController) {
    val buttons = listOf(
        HomeButtonData("Modo Individual", R.drawable.baseline_menu_book_24, Color(0xFF4CAF50), "modo_individual"),
        HomeButtonData("Modo Competitivo", R.drawable.baseline_person_24, Color(0xFFFF9800), "modo_competitivo"),
        HomeButtonData("Estadísticas", R.drawable.baseline_insert_chart_24, Color(0xFF9C27B0), "stats"),
        HomeButtonData("Ajustes", R.drawable.baseline_settings_24, Color(0xFF607D8B), "settings")
    )

    var showLogoutDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)) // Fondo gris claro
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF2196F3)) // Azul
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "GameGlish",
                fontSize = 24.sp,
                color = Color.White
            )
        }

        // Contenido principal
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(buttons) { button ->
                HomeButton(
                    text = button.text,
                    icon = button.icon,
                    color = button.color,
                    onClick = { navController.navigate(button.route) }
                )
            }
        }

        // Pie de página
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFE0E0E0)) // Gris claro
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = { showLogoutDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)), // Rojo
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_exit_to_app_24),
                    contentDescription = "Cerrar Sesión",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar Sesión", color = Color.White)
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Confirmar Cierre de Sesión") },
            text = { Text("¿Estás seguro de que deseas cerrar sesión?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false

                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                ) {
                    Text("Sí")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false }
                ) {
                    Text("No")
                }
            }
        )
    }
}

@Composable
fun HomeButton(
    text: String,
    icon: Int,
    color: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f), // Hacer que el botón sea cuadrado
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = text,
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text, color = Color.White)
        }
    }
}

data class HomeButtonData(
    val text: String,
    val icon: Int,
    val color: Color,
    val route: String
)
