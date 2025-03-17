// Kotlin
package com.example.gameglish.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gameglish.ui.viewmodel.PreguntaViewModel
import kotlinx.coroutines.launch

@Composable
fun GramaticaQuestionsScreen(
    navController: NavController,
    viewModel: PreguntaViewModel = viewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.cargarPreguntasGramatica(context)
    }

    val listaPreguntas by viewModel.preguntas.collectAsState()
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var correctCount by remember { mutableStateOf(0) }
    var statsSubmitted by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Score: $correctCount / ${if (listaPreguntas.isEmpty()) 0 else listaPreguntas.size}",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Ejercicios de Gramática", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        if (listaPreguntas.isEmpty()) {
            Text("No hay preguntas de gramática disponibles.")
        } else {
            if (currentQuestionIndex < listaPreguntas.size) {
                val pregunta = listaPreguntas[currentQuestionIndex]
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
                                "a" == pregunta.opcionCorrecta && selectedAnswer == "a" -> Color.Green
                                "a" != pregunta.opcionCorrecta && selectedAnswer == "a" -> Color.Red
                                else -> MaterialTheme.colorScheme.primary
                            }
                        )
                    ) {
                        Text(pregunta.opcionA)
                    }
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
                                "b" == pregunta.opcionCorrecta && selectedAnswer == "b" -> Color.Green
                                "b" != pregunta.opcionCorrecta && selectedAnswer == "b" -> Color.Red
                                else -> MaterialTheme.colorScheme.primary
                            }
                        )
                    ) {
                        Text(pregunta.opcionB)
                    }
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
                                "c" == pregunta.opcionCorrecta && selectedAnswer == "c" -> Color.Green
                                "c" != pregunta.opcionCorrecta && selectedAnswer == "c" -> Color.Red
                                else -> MaterialTheme.colorScheme.primary
                            }
                        )
                    ) {
                        Text(pregunta.opcionC)
                    }
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
                    ) {
                        Text("Siguiente")
                    }
                }
            } else {
                // When finished, submit statistics and add points to the user.
                if (!statsSubmitted) {
                    LaunchedEffect(Unit) {
                        val puntos = correctCount * 10
                        viewModel.submitEstadistica(correctCount, listaPreguntas.size)
                        viewModel.addPuntosToUsuario(puntos)
                        statsSubmitted = true
                    }
                }
                Text("Test completado. Estadísticas guardadas y puntos añadidos.", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}