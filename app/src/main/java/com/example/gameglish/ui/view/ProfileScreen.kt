package com.example.gameglish.ui.view

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
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.runtime.*
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
    // Color principal (morado) y fondo oscuro degradado
    val themePrimary = Color(0xFF6A1B9A)
    val darkBackgroundTop = Color(0xFF1D1F3E)
    val darkBackgroundBottom = Color(0xFF25294E)
    var menuExpanded by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    val topBarColor = Color(0xFF6A1B9A)

    val context = LocalContext.current
    val db = GameGlishDatabase.getDatabase(context)
    val repositoryUsuario = remember { RepositoryUsuario(db) }
    var usuario by remember { mutableStateOf<EntityUsuario?>(null) }

    // Mapeo de niveles
    val levelMap = mapOf(
        1 to "A1",
        2 to "A2",
        3 to "B1",
        4 to "B2",
        5 to "C1",
        6 to "C2",
        7 to "NATIVE"
    )

    // Cargar datos de usuario
    LaunchedEffect(true) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@LaunchedEffect
        usuario = repositoryUsuario.obtenerUsuarioLocal(uid)
    }

    // Para el historial de estadísticas
    val repositoryEstadistica = RepositoryEstadistica(db)
    val statsViewModel = remember { StatsViewModel(repositoryUsuario, repositoryEstadistica) }
    val estadisticas by statsViewModel.estadisticas

    // Scaffold con TopBar de color morado (themePrimary)
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = topBarColor),
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
                            colors = listOf(darkBackgroundTop, darkBackgroundBottom)
                        )
                    )
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // Si ya cargamos el usuario, mostramos la info
                if (usuario != null) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            // Tarjeta principal estilo oscuro
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF2F3256))
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally  // Centramos horizontalmente todo el contenido
                                ) {
                                    // Avatar centrado en el Card
                                    Box(
                                        modifier = Modifier
                                            .size(90.dp)
                                            .clip(CircleShape)
                                            .background(themePrimary),  // Usamos el color temático
                                        contentAlignment = Alignment.Center  // Esto centra el contenido dentro del Box
                                    ) {
                                        // Imagen del avatar (placeholder)
                                        Image(
                                            painter = painterResource(id = R.drawable.ic_placeholder),
                                            contentDescription = "Avatar",
                                            modifier = Modifier
                                                .size(80.dp)
                                                .clip(CircleShape),
                                            contentScale = ContentScale.Crop
                                        )
                                    }

                                    // 2) El contenido textual (nombre, info)
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(4.dp)
                                         ,
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // Nombre
                                        Text(
                                            text = usuario!!.nombre,
                                            style = MaterialTheme.typography.headlineSmall.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = Color.White
                                        )
                                        Divider(color = Color.LightGray)
                                        // Fila: Email
                                        ProfileInfoRow(
                                            icon = Icons.Default.Email,
                                            info = "Email: ${usuario!!.email}",
                                            themeColor = themePrimary
                                        )
                                        // Fila: Puntos
                                        ProfileInfoRow(
                                            icon = Icons.Default.Stars,
                                            info = "Points: ${usuario!!.puntos}",
                                            themeColor = themePrimary
                                        )
                                        // Fila: Nivel
                                        ProfileInfoRow(
                                            icon = Icons.Default.Grade,
                                            info = "Level: ${levelMap[usuario!!.nivel] ?: "A1"}",
                                            themeColor = themePrimary
                                        )
                                    }
                                }
                            }
                        }
                        // Sección Historial de Estadísticas
                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = "Historial de Estadísticas",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp),
                                // Color dorado para que resalte más
                                color = Color(0xFFFFC107)
                            )
                        }
                        // Tarjetas del historial
                        if (estadisticas.isNotEmpty()) {
                            items(estadisticas) { estadistica ->
                                StatCard(estadistica = estadistica)
                            }
                        } else {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "No hay estadísticas registradas.", color = Color.Gray)
                                }
                            }
                        }
                    }
                } else {
                    // Indicador de carga
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = themePrimary
                    )
                }
            }
        }
    )
}

// Muestra un icono painter + texto en color blanco
@Composable
fun ProfileInfoRow(icon: ImageVector, info: String, themeColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = themeColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = info,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White
        )
    }
}

// Cada estadística en una tarjeta oscura
@Composable
fun StatCard(estadistica: EntityEstadistica) {
    val formattedDate = remember(estadistica.fecha) {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        sdf.format(Date(estadistica.fecha))
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2F3256))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = formattedDate,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Aciertos: ${estadistica.aciertos}",
                    fontSize = 14.sp,
                    color = Color.White
                )
                Text(
                    text = "Errores: ${estadistica.errores}",
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Puntos: ${estadistica.puntos}",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFFFC107)
            )
        }
    }
}
