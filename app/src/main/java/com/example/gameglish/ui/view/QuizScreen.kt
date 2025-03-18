package com.example.gameglish.ui.view



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gameglish.R
import com.example.gameglish.data.model.EntityPregunta

@Composable
fun QuizScreen(onComplete: (Int) -> Unit) {
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedAnswer by remember { mutableStateOf("") }

    val preguntas = listOf(
        EntityPregunta(enunciado = "What is the capital of France?", opcionA = "Paris", opcionB = "London", opcionC = "Rome", opcionD = "Madrid",opcionCorrecta = "A", tema = "General", nivel = 1),
        EntityPregunta(enunciado = "What is 2+2?", opcionA = "3", opcionB = "4", opcionC = "5", opcionCorrecta = "B", tema = "Math", opcionD = "3", nivel = 1)
    )

    if (currentQuestionIndex < preguntas.size) {
        val pregunta = preguntas[currentQuestionIndex]

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(pregunta.enunciado, style = MaterialTheme.typography.headlineSmall)

            Spacer(modifier = Modifier.height(16.dp))

            RadioButtonRow("A) ${pregunta.opcionA}", selectedAnswer == "A") { selectedAnswer = "A" }
            RadioButtonRow("B) ${pregunta.opcionB}", selectedAnswer == "B") { selectedAnswer = "B" }
            RadioButtonRow("C) ${pregunta.opcionC}", selectedAnswer == "C") { selectedAnswer = "C" }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                if (selectedAnswer == pregunta.opcionCorrecta) {
                    currentQuestionIndex++
                    selectedAnswer = ""
                } else {
                    // Manejar respuesta incorrecta si es necesario
                }

                if (currentQuestionIndex == preguntas.size) {
                    onComplete(2)  // Asignar un nivel basado en el resultado
                }
            }) {
                Text("Next")
            }
        }
    }
}

@Composable
fun RadioButtonRow(text: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Text(text, modifier = Modifier.padding(start = 8.dp))
    }
}
