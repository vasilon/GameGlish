// -----------------------------------------------------------------------------
// QuestionsScreen.kt
// -----------------------------------------------------------------------------
// Pantalla que presenta una serie de preguntas de opción múltiple basadas en un
// tema (Gramática, Vocabulario, Reading, etc.) y adaptadas al nivel del usuario.
// Al finalizar, envía las estadísticas al ViewModel para puntuar.
// -----------------------------------------------------------------------------


package com.example.gameglish.ui.view.modoindividual

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gameglish.data.database.GameGlishDatabase
import com.example.gameglish.data.repository.RepositoryUsuario
import com.example.gameglish.ui.viewmodel.PreguntaViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun QuestionsScreen(
    navController: NavController,
    tema : String,
    viewModel: PreguntaViewModel = viewModel()
) {
    val context = LocalContext.current

    // Load grammar questions.
    LaunchedEffect(tema) {
        viewModel.cargarPreguntasPorTema(context, tema)
    }
    val listaPreguntas by viewModel.preguntas.collectAsState()

    // Retrieve current user level.
    val db = GameGlishDatabase.getDatabase(context)
    val repositoryUsuario = remember { RepositoryUsuario(db) }
    var userLevel by remember { mutableStateOf("A1") }
    LaunchedEffect(Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@LaunchedEffect
        val usuario = repositoryUsuario.obtenerUsuarioLocal(uid)
        usuario?.let {
            // Convert numeric level to level string.
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

    // Create mapping for questions and filter by matching user level.
    val levelMap = mapOf(
        1 to "A1",
        2 to "A2",
        3 to "B1",
        4 to "B2",
        5 to "C1",
        6 to "C2",
        7 to "NATIVE"
    )
    val questionsForUser = listaPreguntas.filter { levelMap[it.nivel] == userLevel }

    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var correctCount by remember { mutableStateOf(0) }
    var statsSubmitted by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Score: $correctCount / ${if (questionsForUser.isEmpty()) 0 else questionsForUser.size}",
            style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Ejercicios de $tema", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        if (questionsForUser.isEmpty()) {
            Text("No hay preguntas de gramática disponibles para tu nivel: $userLevel.")
        } else {
            if (currentQuestionIndex < questionsForUser.size) {
                val pregunta = questionsForUser[currentQuestionIndex]
                Text("Pregunta: ${pregunta.enunciado}", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(16.dp))
                Column {
                    Button(
                        onClick = {
                            if (selectedAnswer == null) {
                                selectedAnswer = "a"
                                if ("a" == pregunta.opcionCorrecta) correctCount++
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when {
                                selectedAnswer == null -> MaterialTheme.colorScheme.primary
                                "a" == pregunta.opcionCorrecta && selectedAnswer == "a" -> androidx.compose.ui.graphics.Color.Green
                                "a" != pregunta.opcionCorrecta && selectedAnswer == "a" -> androidx.compose.ui.graphics.Color.Red
                                else -> MaterialTheme.colorScheme.primary
                            }
                        )
                    ) { Text(pregunta.opcionA) }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            if (selectedAnswer == null) {
                                selectedAnswer = "b"
                                if ("b" == pregunta.opcionCorrecta) correctCount++
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when {
                                selectedAnswer == null -> MaterialTheme.colorScheme.primary
                                "b" == pregunta.opcionCorrecta && selectedAnswer == "b" -> androidx.compose.ui.graphics.Color.Green
                                "b" != pregunta.opcionCorrecta && selectedAnswer == "b" -> androidx.compose.ui.graphics.Color.Red
                                else -> MaterialTheme.colorScheme.primary
                            }
                        )
                    ) { Text(pregunta.opcionB) }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            if (selectedAnswer == null) {
                                selectedAnswer = "c"
                                if ("c" == pregunta.opcionCorrecta) correctCount++
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when {
                                selectedAnswer == null -> MaterialTheme.colorScheme.primary
                                "c" == pregunta.opcionCorrecta && selectedAnswer == "c" -> androidx.compose.ui.graphics.Color.Green
                                "c" != pregunta.opcionCorrecta && selectedAnswer == "c" -> androidx.compose.ui.graphics.Color.Red
                                else -> MaterialTheme.colorScheme.primary
                            }
                        )
                    ) { Text(pregunta.opcionC) }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            if (selectedAnswer == null) {
                                selectedAnswer = "d"
                                if ("d" == pregunta.opcionCorrecta) correctCount++
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when {
                                selectedAnswer == null -> MaterialTheme.colorScheme.primary
                                "d" == pregunta.opcionCorrecta && selectedAnswer == "d" -> androidx.compose.ui.graphics.Color.Green
                                "d" != pregunta.opcionCorrecta && selectedAnswer == "d" -> androidx.compose.ui.graphics.Color.Red
                                else -> MaterialTheme.colorScheme.primary
                            }
                        )
                    ) { Text(pregunta.opcionD) }
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (selectedAnswer != null) {
                    val feedback = if (selectedAnswer == pregunta.opcionCorrecta) "Respuesta correcta" else "Respuesta incorrecta"
                    Text(feedback, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            currentQuestionIndex++
                            selectedAnswer = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Siguiente") }
                }
            } else {
                if (!statsSubmitted) {
                    LaunchedEffect(Unit) {
                        viewModel.submitEstadistica(correctCount, questionsForUser.size)
                        statsSubmitted = true
                    }
                }
                Text("Test completado. Estadísticas guardadas y puntos añadidos.", style = MaterialTheme.typography.bodyLarge)

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        navController.navigate("individual_main")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Volver al menú principal") }

            }
        }
    }
}