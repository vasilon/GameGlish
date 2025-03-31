package com.example.gameglish.ui.view

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.gameglish.R
import com.example.gameglish.data.database.GameGlishDatabase
import com.example.gameglish.data.model.EntityEstadistica
import com.example.gameglish.data.model.EntityUsuario
import com.example.gameglish.data.repository.RepositoryEstadistica
import com.example.gameglish.data.repository.RepositoryUsuario
import com.example.gameglish.ui.navigation.Screen
import com.example.gameglish.ui.viewmodel.StatsViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Pantalla de estadísticas del usuario.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(navController: NavController) {
    // Obtenemos el contexto y la instancia de la base de datos.
    val context = LocalContext.current
    val db = GameGlishDatabase.getDatabase(context)

    // Creamos los repositorios a partir de la base de datos.
    val repositoryUsuario = RepositoryUsuario(db)
    val repositoryEstadistica = RepositoryEstadistica(db)

    // Instanciamos el ViewModel manualmente sin utilizar un Factory.
    // Nota: De esta forma, el ViewModel no sobrevive a cambios de configuración.
    val statsViewModel = remember {
        StatsViewModel(repositoryUsuario, repositoryEstadistica)
    }

    // Observamos los estados del usuario y sus estadísticas.
    val usuario by statsViewModel.usuario
    val estadisticas by statsViewModel.estadisticas

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Estadísticas") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // Tarjeta con la información del usuario.
                if (usuario != null) {
                    Button(
                        onClick = { navController.navigate(Screen.Ranking.route) },
                        modifier = Modifier.padding(vertical = 16.dp)
                    ) {
                        Text(text = "Global Ranking")
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        elevation = cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Usuario: ${usuario?.nombre}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Email: ${usuario?.email}",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Puntos: ${usuario?.puntos}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "Nivel: ${usuario?.nivel}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Historial de Estadísticas",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Lista de estadísticas.
                if (estadisticas.isNotEmpty()) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(estadisticas) { estadistica ->
                            StatCard(estadistica = estadistica)
                        }
                    }
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "No hay estadísticas registradas.")
                    }
                }
            }
        }
    )
}

/**
 * Composable para mostrar cada tarjeta de estadística
 */
@Composable
fun StatCard(estadistica: EntityEstadistica) {
    // Formatea la fecha (almacenada como Long) a un formato legible.
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