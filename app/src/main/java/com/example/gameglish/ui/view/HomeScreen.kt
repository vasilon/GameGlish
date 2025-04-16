package com.example.gameglish.ui.view

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    loginViewModel: LoginViewModel = viewModel()
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val db = GameGlishDatabase.getDatabase(context)
    val repositoryUsuario = remember { RepositoryUsuario(db) }

    var userPoints by remember { mutableIntStateOf(0) }
    var userLevel by remember { mutableStateOf("A1") }
    val levels = listOf("A1", "A2", "B1", "B2", "C1", "C2", "NATIVE")

    // Datos ficticios
    val achievements = listOf("Primer Quiz", "5 Días Consecutivos", "Nivel A2")
    val recommendedLesson = "Gramática: Presente Simple"

    // Carga de datos de Usuario
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

    // Subir nivel al llegar a 300 puntos
    LaunchedEffect(userPoints) {
        if (userPoints >= 300) {
            val currentIndex = levels.indexOf(userLevel)
            if (currentIndex < levels.lastIndex) {
                userLevel = levels[currentIndex + 1]
            }
            userPoints = 0
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "GameGlish",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                actions = {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_account_circle_24),
                            contentDescription = "Ajustes",
                            tint = MaterialTheme.colorScheme.onPrimary
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
            // Gradiente de fondo usando background y surface
            val gradientColors = listOf(
                MaterialTheme.colorScheme.background,
                MaterialTheme.colorScheme.surface
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(brush = Brush.verticalGradient(gradientColors))
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

    // Diálogo de Logout
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
 * DashboardHeader que se adapta a cualquier modo, usando los colores del tema.
 */
@Composable
fun DashboardHeader(userLevel: String, userPoints: Int) {
    val progress = min(userPoints / 300f, 1f)
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Nivel: $userLevel",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = "Puntos: $userPoints / 300",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * Tarjeta de recomendación usando el color primario para ícono y botón.
 */
@Composable
fun RecommendationCard(
    lessonTitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_menu_book_24),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Recomendación del Día",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = lessonTitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Comenzar", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}

/**
 * Sección de Logros con contenedor `surfaceVariant` y tarjetas en color primario
 * para que destaquen.
 */
@Composable
fun AchievementsSection(achievements: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            // surfaceVariant para que resalte un poco más que el fondo y surface
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp)
    ) {
        Text(
            text = "Logros Recientes",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(achievements) { achievement ->
                AchievementCard(achievement)
            }
        }
    }
}

/**
 * Tarjeta de logro en color primario para destacar aún más.
 */
@Composable
fun AchievementCard(achievement: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
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
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Sección de Noticias / Consejo con contenedor `surfaceVariant`.
 */
@Composable
fun NewsSection() {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Consejo del Día",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Repasa tus lecciones anteriores para consolidar lo aprendido. " +
                        "La práctica constante es clave para el progreso.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
