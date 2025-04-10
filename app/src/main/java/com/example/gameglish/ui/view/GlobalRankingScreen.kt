package com.example.gameglish.ui.view

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gameglish.R
import com.example.gameglish.data.database.GameGlishDatabase
import com.example.gameglish.data.model.EntityRanking
import com.example.gameglish.data.repository.RepositoryEstadistica
import com.example.gameglish.ui.components.LeaderboardTabs
import com.example.gameglish.ui.components.RankingListItem
import com.example.gameglish.ui.components.Top3Row

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalRankingScreen(navController: NavController) {
    // Colores principales
    val topBarColor = Color(0xFFF39000)        // Naranja principal
    val darkBackgroundTop = Color(0xFF1D1F3E)  // Gradiente top
    val darkBackgroundBottom = Color(0xFF25294E) // Gradiente bottom

    var menuExpanded by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val db = GameGlishDatabase.getDatabase(context)
    val repositoryEstadistica = RepositoryEstadistica(db)

    var rankingList by remember { mutableStateOf<List<EntityRanking>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Carga de datos
    LaunchedEffect(Unit) {
        try {
            rankingList = repositoryEstadistica.obtenerRankingGlobal()
            Log.d("GlobalRankingScreen", "Ranking loaded: ${rankingList.size}")
        } catch (e: Exception) {
            Log.e("GlobalRankingScreen", "Error loading ranking", e)
        } finally {
            isLoading = false
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
            // Fondo con gradiente oscuro
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
                if (isLoading) {
                    // Indicador de carga
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = topBarColor)
                    }
                } else {
                    // Layout principal
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        // Pestañas de Region / National / Global
                        LeaderboardTabs()

                        Spacer(modifier = Modifier.height(16.dp))

                        // Título "Leaderboard" (o “Ranking Global”)
                        Text(
                            text = "Ranking Global",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            ),
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(bottom = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Top 3 (si hay al menos 3)
                        val podiumList = rankingList.take(3)
                        val restList = rankingList.drop(3)

                        if (podiumList.size < 3) {
                            Text(
                                text = "No hay datos para mostrar el podio",
                                style = MaterialTheme.typography.bodyLarge.copy(color = Color.Gray),
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        } else {
                            Top3Row(podiumList)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Lista del resto
                        if (restList.isEmpty()) {
                            Text(
                                text = "No hay más usuarios en el ranking.",
                                style = MaterialTheme.typography.bodyLarge.copy(color = Color.Gray),
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                itemsIndexed(restList) { index, ranking ->
                                    RankingListItem(
                                        ranking = ranking,
                                        position = index + 4
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    )

    // Manejo de showLogoutDialog si lo deseas...
}
