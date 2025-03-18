// Kotlin
package com.example.gameglish.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.example.gameglish.data.database.GameGlishDatabase
import com.example.gameglish.data.repository.RepositoryUsuario
import com.example.gameglish.data.model.EntityUsuario
import com.example.gameglish.ui.components.BackTopAppBar
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: androidx.navigation.NavController) {
    val context = LocalContext.current
    val db = GameGlishDatabase.getDatabase(context)
    val repositoryUsuario = remember { RepositoryUsuario(db) }
    var usuario by remember { mutableStateOf<EntityUsuario?>(null) }

    // Map numeric level to string level.
    val levelMap = mapOf(
        1 to "A1",
        2 to "A2",
        3 to "B1",
        4 to "B2",
        5 to "C1",
        6 to "C2",
        7 to "NATIVE"
    )

    LaunchedEffect(true) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@LaunchedEffect
        usuario = repositoryUsuario.obtenerUsuarioLocal(uid)
    }

    Scaffold(
        topBar = {
            BackTopAppBar(navController = navController, title = "Profile")
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                usuario?.let { user ->
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "User Information",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Divider()

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Name: ${user.nombre}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Email: ${user.email}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stars,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Points: ${user.puntos}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Grade,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Level: ${levelMap[user.nivel] ?: "A1"}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                } ?: run {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    )
}