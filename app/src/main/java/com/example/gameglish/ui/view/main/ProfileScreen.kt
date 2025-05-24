// File: ProfileScreen.kt
// -----------------------------------------------------------------------------
// Composable que muestra el perfil del usuario, incluyendo su información
// personal, estadísticas y un historial de puntuaciones.
// También permite cerrar sesión y navegar a otras pantallas.
// -----------------------------------------------------------------------------


package com.example.gameglish.ui.view.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gameglish.R
import com.example.gameglish.data.database.GameGlishDatabase
import com.example.gameglish.data.model.EntityEstadistica
import com.example.gameglish.data.model.EntityUsuario
import com.example.gameglish.data.repository.RepositoryEstadistica
import com.example.gameglish.data.repository.RepositoryUsuario
import com.example.gameglish.ui.viewmodel.StatsViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    // Morado principal fijo
    val topBarColor = Color(0xFF6A1B9A)

    // Degradado del fondo usando el tema
    val gradientColors = listOf(
        MaterialTheme.colorScheme.background,
        MaterialTheme.colorScheme.surfaceVariant
    )

    var menuExpanded by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val db = GameGlishDatabase.getDatabase(context)
    val repositoryUsuario = remember { RepositoryUsuario(db) }
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

    val usuario by repositoryUsuario
        .observeUsuario(uid)
        .observeAsState(initial = null)

    // Niveles
    val levelMap = mapOf(
        1 to "A1", 2 to "A2", 3 to "B1",
        4 to "B2", 5 to "C1", 6 to "C2", 7 to "NATIVE"
    )

    if (usuario == null) {
        CircularProgressIndicator()
    } else {
        Text("Nivel: ${levelMap[usuario!!.nivel]}")
        Text("Puntos: ${usuario!!.puntos}")
    }





    // Historial de estadísticas
    val statsViewModel: StatsViewModel = viewModel()
    val estadisticas by statsViewModel.estadisticas

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "GameGlish",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = topBarColor),
                actions = {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_account_circle_24),
                            contentDescription = "Perfil",
                            tint = MaterialTheme.colorScheme.onPrimary
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
                    .background(Brush.verticalGradient(gradientColors))
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                if (usuario == null) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = topBarColor
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            // Card principal
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(8.dp),
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    // Avatar con fondo morado
                                    Box(
                                        modifier = Modifier
                                            .size(90.dp)
                                            .clip(CircleShape)
                                            .background(topBarColor),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Image(
                                            painter = painterResource(R.drawable.ic_placeholder),
                                            contentDescription = "Avatar",
                                            modifier = Modifier
                                                .size(80.dp)
                                                .clip(CircleShape),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                    Spacer(Modifier.height(12.dp))
                                    Text(
                                        usuario!!.nombre,
                                        style = MaterialTheme.typography.headlineSmall.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
                                    Spacer(Modifier.height(8.dp))
                                    // Info rows
                                    ProfileInfoRow(
                                        icon = Icons.Default.Email,
                                        info = "Email: ${usuario!!.email}",
                                        iconTint = topBarColor
                                    )
                                    ProfileInfoRow(
                                        icon = Icons.Default.Stars,
                                        info = "Puntos: ${usuario!!.puntos}",
                                        iconTint = topBarColor
                                    )
                                    ProfileInfoRow(
                                        icon = Icons.Default.Grade,
                                        info = "Nivel: ${levelMap[usuario!!.nivel] ?: "A1"}",
                                        iconTint = topBarColor
                                    )
                                }
                            }
                        }
                        item {
                            Spacer(Modifier.height(24.dp))
                            Text(
                                "Historial de Estadísticas",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = topBarColor
                            )
                        }
                        if (estadisticas.isNotEmpty()) {
                            items(estadisticas) { estadistica ->
                                StatCard(
                                    estadistica = estadistica,
                                    scoreColor = topBarColor
                                )
                            }
                        } else {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "No hay estadísticas registradas.",
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    )

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Confirmar cierre de sesión") },
            text = { Text("¿Estás seguro de que deseas cerrar sesión?") },
            confirmButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
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
fun ProfileInfoRow(icon: ImageVector, info: String, iconTint: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            info,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun StatCard(estadistica: EntityEstadistica, scoreColor: Color) {
    val formattedDate = remember(estadistica.fecha) {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        sdf.format(Date(estadistica.fecha))
    }
    Card(
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                formattedDate,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Aciertos: ${estadistica.aciertos}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "Errores: ${estadistica.errores}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "Puntos: ${estadistica.puntos}",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = scoreColor
            )
        }
    }
}
