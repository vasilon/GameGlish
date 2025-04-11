package com.example.gameglish.ui.view

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardElevation
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
import com.example.gameglish.data.model.EntityEstadistica
import com.example.gameglish.data.model.EntityUsuario
import com.example.gameglish.data.repository.RepositoryEstadistica
import com.example.gameglish.data.repository.RepositoryUsuario
import com.example.gameglish.ui.components.BackTopAppBar
import com.example.gameglish.ui.viewmodel.StatsViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    // COLOR TEMÁTICO: Color(0xFF6A1B9A)
    val themePrimary = Color(0xFF6A1B9A)
    // Fondo suave para el contenido (degradado basado en el tema)
    val lightTheme = Color(0xFFF3E5F5)

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

    // Cargamos la información del usuario
    LaunchedEffect(true) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@LaunchedEffect
        usuario = repositoryUsuario.obtenerUsuarioLocal(uid)
    }

    // Estados para el menú desplegable y diálogo de cierre de sesión
    var menuExpanded by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Para la sección de estadísticas, creamos el repositorio de estadísticas y el ViewModel.
    val repositoryEstadistica = RepositoryEstadistica(db)
    // Nota: Este StatsViewModel se instancia de forma manual sin un Factory; por lo tanto, no sobrevivirá a cambios de configuración.
    val statsViewModel = remember { StatsViewModel(repositoryUsuario, repositoryEstadistica) }
    // Observamos el listado de estadísticas.
    val estadisticas by statsViewModel.estadisticas

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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = themePrimary),

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
                            colors = listOf(lightTheme, Color.White)
                        )
                    )
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                if (usuario != null) {
                    // Usamos una LazyColumn para que el contenido sea scrollable.
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            // Tarjeta principal con la información del usuario
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    // Avatar
                                    Box(
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clip(RoundedCornerShape(50))
                                            .background(themePrimary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = "Avatar",
                                            tint = Color.White,
                                            modifier = Modifier.size(40.dp)
                                        )
                                    }
                                    // Nombre
                                    Text(
                                        text = usuario!!.nombre,
                                        style = MaterialTheme.typography.headlineSmall.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = Color(0xFF424242)
                                    )
                                    Divider()
                                    // Fila de información: Email
                                    ProfileInfoRow(
                                        icon = Icons.Default.Email,
                                        info = "Email: ${usuario!!.email}",
                                        themeColor = themePrimary
                                    )
                                    // Fila de información: Points
                                    ProfileInfoRow(
                                        icon = Icons.Default.Stars,
                                        info = "Points: ${usuario!!.puntos}",
                                        themeColor = themePrimary
                                    )
                                    // Fila de información: Level
                                    ProfileInfoRow(
                                        icon = Icons.Default.Grade,
                                        info = "Level: ${levelMap[usuario!!.nivel] ?: "A1"}",
                                        themeColor = themePrimary
                                    )
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
                                color = themePrimary
                            )
                        }
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
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = themePrimary
                    )
                }
            }
        }
    )
}

@Composable
fun ProfileInfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, info: String, themeColor: Color) {
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
            color = Color(0xFF424242)
        )
    }
}

@Composable
fun StatCard(estadistica: EntityEstadistica) {
    // Se formatea la fecha para mostrarla en formato legible.
    val formattedDate = remember(estadistica.fecha) {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        sdf.format(Date(estadistica.fecha))
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = formattedDate,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Aciertos: ${estadistica.aciertos}",
                    fontSize = 14.sp
                )
                Text(
                    text = "Errores: ${estadistica.errores}",
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Puntos: ${estadistica.puntos}",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
