package com.example.gameglish.ui.view

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gameglish.R
import com.example.gameglish.data.database.GameGlishDatabase
import com.example.gameglish.data.repository.RepositoryUsuario
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    // Estados para el menú y el diálogo de cierre de sesión
    var menuExpanded by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Datos de usuario y progreso
    val context = LocalContext.current
    val db = GameGlishDatabase.getDatabase(context)
    val repositoryUsuario = remember { RepositoryUsuario(db) }
    var userPoints by remember { mutableIntStateOf(0) }
    var userLevel by remember { mutableStateOf("A1") }
    val levels = listOf("A1", "A2", "B1", "B2", "C1", "C2", "NATIVE")

    // Datos de ejemplo para logros y recomendación
    val achievements = listOf("Primer Quiz", "5 Días Consecutivos", "Nivel A2")
    val recommendedLesson = "Gramática: Presente Simple"
// Cargar datos de usuario: primero se intenta con los datos locales,
    // si son nulos se recuperan los datos de forma remota.
    LaunchedEffect(true) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@LaunchedEffect
        var usuario = repositoryUsuario.obtenerUsuarioLocal(uid)
        if (usuario == null) {
            // Si no hay datos locales, se obtiene de la base de datos remota.
            usuario = repositoryUsuario.obtenerUsuarioRemoto(uid)
            // Si se obtuvo remoto, guardarlo localmente para futuras consultas.
            usuario?.let { repositoryUsuario.guardarUsuarioLocal(it) }
        }
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

    // Actualizar nivel si alcanza 300 puntos
    LaunchedEffect(userPoints) {
        if (userPoints >= 300) {
            val currentIndex = levels.indexOf(userLevel)
            if (currentIndex < levels.lastIndex) {
                userLevel = levels[currentIndex + 1]
            }
            userPoints = 0
        }
    }

    // Scaffold con TopAppBar y contenido con fondo degradado
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "GameGlish",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFE3F2FD), // Azul muy claro
                                Color.White
                            )
                        )
                    )
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DashboardHeader(userLevel = userLevel, userPoints = userPoints)
                    RecommendationCard(
                        lessonTitle = recommendedLesson,
                        onClick = { /* Navegar a la lección recomendada */ }
                    )
                    AchievementsSection(achievements = achievements)
                    NewsSection()
                }
            }
        }
    )

    // Diálogo de confirmación de cierre de sesión
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
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("No")
                }
            }
        )
    }
}

@Composable
fun DashboardHeader(userLevel: String, userPoints: Int) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Nivel: $userLevel",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF1A237E)
            )
            LinearProgressIndicator(
                progress = userPoints / 300f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp)),
                color = Color(0xFF2196F3),
                trackColor = Color(0xFFE3F2FD)
            )
            Text(
                text = "Puntos: $userPoints / 300",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF424242)
            )
        }
    }
}

@Composable
fun RecommendationCard(lessonTitle: String, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF90CAF9)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_menu_book_24),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Recomendación del Día",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = lessonTitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
            ) {
                Text("Comenzar", color = Color.White)
            }
        }
    }
}

@Composable
fun AchievementsSection(achievements: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = "Logros Recientes",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color(0xFF0D47A1)
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(achievements) { achievement ->
                AchievementCard(achievement = achievement)
            }
        }
    }
}

@Composable
fun AchievementCard(achievement: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF42A5F5)),
        modifier = Modifier
            .requiredSize(width = 120.dp, height = 80.dp) // Fija un tamaño uniforme
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),  // Asegura que haya un poco de espacio interno
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = achievement,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis  // Si el texto es muy largo
            )
        }
    }
}


@Composable
fun NewsSection() {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Consejo del Día",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF0D47A1)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Repasa tus lecciones anteriores para consolidar lo aprendido. La práctica constante es clave para el progreso.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF424242)
            )
        }
    }
}
