// Kotlin
package com.example.gameglish.ui.view.auth

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.gameglish.R
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirstTimeLoginScreen(
    onRegisterSuccess: () -> Unit,
    registerUser: (nombre: String, nivelAsignado: String) -> Unit,
    modifier: Modifier = Modifier
) {
    // —— Estado ——
    var nombre by remember { mutableStateOf("") }
    var quizStarted by remember { mutableStateOf(false) }
    var currentIndex by remember { mutableStateOf(0) }
    var correctCount by remember { mutableStateOf(0) }
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var nivelAsignado by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    // —— Cargo las preguntas desde JSON (solo una vez) ——
    val questions by remember {
        mutableStateOf(loadFirstTimeQuestions(context))
    }

    // —— Lógica para asignar nivel ——
    fun calcularNivel(score: Int): String = when (score) {
        in 0..1 -> "A1"
        2       -> "A2"
        3       -> "B1"
        4       -> "B2"
        5       -> "C1"
        else    -> "A1"
    }

    // —— Gradient y UI común ——
    val gradient = Brush.verticalGradient(
        listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.primary.copy(alpha = .6f),
            MaterialTheme.colorScheme.surface
        )
    )

    Box(
        modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = null,
                modifier = Modifier.size(120.dp)
            )
            Spacer(Modifier.height(32.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                ),
                elevation = CardDefaults.cardElevation(0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // ————— Paso 1: Nombre —————
                    if (!quizStarted && nivelAsignado == null) {
                        Text(
                            text = "Completa tu perfil",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(24.dp))

                        OutlinedTextField(
                            value = nombre,
                            onValueChange = { nombre = it },
                            label = { Text("Nombre") },
                            singleLine = true,
                            leadingIcon = { Icon(Icons.Filled.AccountCircle, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                focusedLabelColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        Spacer(Modifier.height(24.dp))

                        Button(
                            onClick = { quizStarted = true },
                            enabled = nombre.isNotBlank(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Empezar prueba de nivel", style = MaterialTheme.typography.titleMedium)
                        }
                    }

                    // ————— Paso 2: Cuestionario —————
                    else if (quizStarted && nivelAsignado == null) {
                        val q = questions[currentIndex]
                        Text(
                            text = "Pregunta ${currentIndex + 1} de ${questions.size}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(q.question, style = MaterialTheme.typography.bodyLarge)
                        Spacer(Modifier.height(16.dp))

                        q.answers.forEach { answer ->
                            OutlinedButton(
                                onClick = { selectedAnswer = answer.id },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                border = if (selectedAnswer == answer.id)
                                    ButtonDefaults.outlinedButtonBorder.copy(width = 2.dp)
                                else
                                    ButtonDefaults.outlinedButtonBorder
                            ) {
                                Text(answer.text)
                            }
                        }

                        Spacer(Modifier.height(24.dp))
                        Button(
                            onClick = {
                                if (selectedAnswer == q.correctAnswerId) correctCount++
                                selectedAnswer = null
                                currentIndex++
                                if (currentIndex >= questions.size) {
                                    nivelAsignado = calcularNivel(correctCount)
                                }
                            },
                            enabled = selectedAnswer != null,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                if (currentIndex < questions.size - 1) "Siguiente" else "Ver resultado",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }

                    // ————— Paso 3: Resultado —————
                    else if (nivelAsignado != null) {
                        Text(
                            text = "Resultados",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(16.dp))
                        Text("Has acertado $correctCount de ${questions.size} preguntas.")
                        Spacer(Modifier.height(8.dp))
                        Text("Tu nivel asignado es:", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            nivelAsignado!!,
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(24.dp))

                        Button(
                            onClick = {
                                registerUser(nombre, nivelAsignado!!)
                                onRegisterSuccess()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Guardar perfil", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
        }
    }
}

// —— Función auxiliar para leer JSON de raw/first_time_questions.json ——
private fun loadFirstTimeQuestions(context: Context): List<AssessmentQuestion> {
    val inputStream = context.resources.openRawResource(R.raw.first_time_questions)
    val jsonString = inputStream.bufferedReader().use { it.readText() }
    val questionsArray = JSONObject(jsonString).getJSONArray("questions")
    val list = mutableListOf<AssessmentQuestion>()

    for (i in 0 until questionsArray.length()) {
        val obj = questionsArray.getJSONObject(i)
        val enun = obj.getString("question")
        val answersJson = obj.getJSONArray("answers")
        val answers = (0 until answersJson.length()).map { idx ->
            val a = answersJson.getJSONObject(idx)
            Answer(a.getString("id"), a.getString("text"))
        }
        val correctId = obj.getString("correctAnswerId")
        list += AssessmentQuestion(
            question = enun,
            answers = answers,
            correctAnswerId = correctId
        )
    }
    return list
}

// —— Clases auxiliares ——
data class AssessmentQuestion(
    val question: String,
    val answers: List<Answer>,
    val correctAnswerId: String
)
data class Answer(val id: String, val text: String)