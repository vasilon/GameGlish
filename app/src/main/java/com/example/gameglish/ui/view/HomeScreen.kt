package com.example.gameglish.ui.view

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gameglish.R
import com.example.gameglish.data.database.GameGlishDatabase
import com.example.gameglish.data.repository.RepositoryUsuario
import com.example.gameglish.ui.viewmodel.LoginViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    loginViewModel: LoginViewModel = viewModel()
) {
    // -------------------------
    // 1) Mantener Menú y Diálogo de Logout
    // -------------------------
    var menuExpanded by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    // -------------------------
    // 2) Datos de Usuario y Progreso
    // -------------------------
    val context = LocalContext.current
    val db = GameGlishDatabase.getDatabase(context)
    val repositoryUsuario = remember { RepositoryUsuario(db) }

    var userPoints by remember { mutableIntStateOf(0) }
    var userLevel by remember { mutableStateOf("A1") }
    val levels = listOf("A1", "A2", "B1", "B2", "C1", "C2", "NATIVE")

    // Datos ficticios para logros y recomendación
    val achievements = listOf("Primer Quiz", "5 Días Consecutivos", "Nivel A2")
    val recommendedLesson = "Gramática: Presente Simple"

    // -------------------------
    // 3) Cargar Datos de Usuario con estilo “GlobalRanking”
    // -------------------------
    LaunchedEffect(true) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@LaunchedEffect
        var usuario = repositoryUsuario.obtenerUsuarioLocal(uid)
        if (usuario == null) {
            usuario = repositoryUsuario.obtenerUsuarioRemoto(uid)
            usuario?.let { repositoryUsuario.guardarUsuarioLocal(it) }
        }
        usuario?.let {
            userPoints = it.puntos
            val nivelMap = mapOf(
                1 to "A1",
                2 to "A2",
                3 to "B1",
                4 to "B2",
                5 to "C1",
                6 to "C2",
                7 to "NATIVE"
            )
            userLevel = nivelMap[it.nivel] ?: "A1"
        }
    }

    // Al superar 300 puntos sube de nivel y reinicia a 0
    LaunchedEffect(userPoints) {
        if (userPoints >= 300) {
            val currentIndex = levels.indexOf(userLevel)
            if (currentIndex < levels.lastIndex) {
                userLevel = levels[currentIndex + 1]
            }
            userPoints = 0
        }
    }

    // -------------------------
    // 4) Scaffold con TopBar y Gradiente Oscuro
    // (No se cambia la funcionalidad del TopBar)
    // -------------------------
    Scaffold(
        topBar = {
            // ¡No modificamos el color ni la funcionalidad del TopBar!
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
                            contentDescription = "Ajustes",
                            tint = Color.White
                        )
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Ajustes") },
                            onClick = {
                                menuExpanded = false
                                navController.navigate("settings")
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
            // -------------------------
            // Fondo con gradiente oscuro (como GlobalRankingScreen)
            // -------------------------
            val darkBackgroundTop = Color(0xFF1D1F3E)
            val darkBackgroundBottom = Color(0xFF25294E)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(darkBackgroundTop, darkBackgroundBottom)
                        )
                    )
                    .padding(paddingValues)
            ) {
                // -------------------------
                // Contenido principal
                // -------------------------
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header con nivel y progreso
                    DashboardHeaderDark(userLevel = userLevel, userPoints = userPoints)

                    // Card de Recomendación del Día
                    RecommendationCardDark(
                        lessonTitle = recommendedLesson,
                        onClick = { /* Navegar a la lección recomendada */ }
                    )

                    // Sección de Logros Recientes
                    AchievementsSectionDark(achievements = achievements)

                    // Sección de “Noticias” o “Consejo”
                    NewsSectionDark()
                }
            }
        }
    )

    // -------------------------
    // Diálogo de Logout
    // -------------------------
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Confirmar cierre de Sesión") },
            text = { Text("¿Estás seguro de que deseas cerrar sesión?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        loginViewModel.cerrarSesion()
                        navController.navigate("login") {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
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

/**
 * DashboardHeader con estilo oscuro y barras en color amarillo,
 * similar a la línea de diseño de GlobalRankingScreen.
 */
@Composable
fun DashboardHeaderDark(userLevel: String, userPoints: Int) {
    // Progreso normalizado (de 0.0 a 1.0)
    val progress = min(userPoints / 300f, 1f)
    // Card oscura
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2F3256)),
        elevation = cardElevation(0.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Nivel: $userLevel",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            // Barra de progreso, usando un color dorado
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp)),
                color = Color(0xFFFFC107),
                trackColor = Color(0xFF424769)
            )
            Text(
                text = "Puntos: $userPoints / 300",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFE8E8E8)
            )
        }
    }
}

/**
 * Tarjeta de recomendación con fondo oscuro
 */
@Composable
fun RecommendationCardDark(
    lessonTitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2F3256)),
        elevation = cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícono de libro
            Icon(
                painter = painterResource(id = R.drawable.baseline_menu_book_24),
                contentDescription = null,
                tint = Color(0xFFFFC107),
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
                    color = Color(0xFFE0E0E0)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107))
            ) {
                Text("Comenzar", color = Color.Black)
            }
        }
    }
}

/**
 * Logros Recientes con un estilo oscuro y tarjetas más pequeñas
 */
@Composable
fun AchievementsSectionDark(achievements: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF2F3256))
            .padding(16.dp)
    ) {
        Text(
            text = "Logros Recientes",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color(0xFFFFC107)
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(achievements) { achievement ->
                AchievementCardDark(achievement)
            }
        }
    }
}

/**
 * Tarjetita oscura para cada logro
 */
@Composable
fun AchievementCardDark(achievement: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF3C3F5B)),
        elevation = cardElevation(0.dp),
        modifier = Modifier.requiredSize(width = 120.dp, height = 80.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = achievement,
                style = MaterialTheme.typography.bodySmall.copy(color = Color.White),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Sección de Noticias / Consejo con estilo oscuro
 */
@Composable
fun NewsSectionDark() {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2F3256)),
        elevation = cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Consejo del Día",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFFFFC107)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Repasa tus lecciones anteriores para consolidar lo aprendido. La práctica constante es clave para el progreso.",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
            )
        }
    }
}
