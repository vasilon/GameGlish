// Kotlin
package com.example.gameglish.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gameglish.R
import com.example.gameglish.data.database.GameGlishDatabase
import com.example.gameglish.data.repository.RepositoryUsuario
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    // State to control dropdown menu & logout dialog.
    var menuExpanded by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Kotlin
    val context = LocalContext.current
    val db = GameGlishDatabase.getDatabase(context)
    val repositoryUsuario = remember { RepositoryUsuario(db) }
    var userPoints by remember { mutableIntStateOf(0) }
    var userLevel by remember { mutableStateOf("A1") }
    // Ordered list of levels.
    val levels = listOf("A1", "A2", "B1", "B2", "C1", "C2", "NATIVE")

    LaunchedEffect(true) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@LaunchedEffect
        val usuario = repositoryUsuario.obtenerUsuarioLocal(uid)
        if (usuario != null) {
            userPoints = usuario.puntos
            val nivelMap = mapOf(
                1 to "A1",
                2 to "A2",
                3 to "B1",
                4 to "B2",
                5 to "C1",
                6 to "C2",
                7 to "NATIVE"
            )
            userLevel = nivelMap[usuario.nivel] ?: "A1"
        }
    }

    LaunchedEffect(userPoints) {
        if (userPoints >= 300) {
            val currentIndex = levels.indexOf(userLevel)
            if (currentIndex < levels.lastIndex) {
                userLevel = levels[currentIndex + 1]
            }
            userPoints = 0
            // Update to database can be added here if needed.
        }
    }

    // List of home navigation buttons.
    val buttons = listOf(
        HomeButtonData("Modo Individual", R.drawable.baseline_menu_book_24, Color(0xFF4CAF50), "modo_individual"),
        HomeButtonData("Modo Competitivo", R.drawable.baseline_sports_esports_24, Color(0xFFFF9800), "modo_competitivo"),
        HomeButtonData("Estadísticas", R.drawable.baseline_insert_chart_24, Color(0xFF9C27B0), "stats"),
        HomeButtonData("Ajustes", R.drawable.baseline_settings_24, Color(0xFF607D8B), "settings")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "GameGlish",
                        color = Color.White,
                        fontSize = 20.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF2196F3)),
                actions = {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_account_circle_24),
                            contentDescription = "Perfil",
                            tint = Color.White
                        )
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Perfil") },
                            onClick = {
                                menuExpanded = false
                                navController.navigate("profile")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Cerrar Sesión") },
                            onClick = {
                                menuExpanded = false
                                showLogoutDialog = true
                            }
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            // Main content with progress bar section and grid of buttons.
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF5F5F5))
                    .padding(paddingValues)
            ) {
                // Progress bar and level display.
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Level: $userLevel", style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { userPoints / 300f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Points: $userPoints / 300", style = MaterialTheme.typography.bodyMedium)
                }
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(buttons) { button ->
                        HomeCard(
                            text = button.text,
                            icon = button.icon,
                            backgroundColor = button.color,
                            onClick = { navController.navigate(button.route) }
                        )
                    }
                }
            }
        }
    )

    // Logout confirmation dialog.
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Confirmar cierre de Sesión") },
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
fun HomeCard(
    text: String,
    icon: Int,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = text,
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = text,
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

data class HomeButtonData(
    val text: String,
    val icon: Int,
    val color: Color,
    val route: String
)