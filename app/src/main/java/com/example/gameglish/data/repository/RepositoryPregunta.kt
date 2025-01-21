package com.example.gameglish.data.repository

import com.example.gameglish.data.database.GameGlishDatabase
import com.example.gameglish.data.model.EntityPregunta
import android.content.Context
import com.example.gameglish.R
import com.example.gameglish.data.database.DaoPregunta
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

class RepositoryPregunta(private val db: GameGlishDatabase) {

    suspend fun insertPregunta(pregunta: EntityPregunta) {
        db.preguntaDao().insertPregunta(pregunta)
    }

    suspend fun getPreguntasPorTema(tema: String): List<EntityPregunta> {
        return db.preguntaDao().getPreguntasByTema(tema)
    }

//    suspend fun insertarPreguntasDesdeJson(context: Context) {
//        val preguntas = readQuestionsFromJson(context)
//        withContext(Dispatchers.IO) {
//            daoPregunta.insertarPreguntas(preguntas)
//        }
//    }

    private fun readQuestionsFromJson(context: Context): List<EntityPregunta> {
        val inputStream = context.resources.openRawResource(R.raw.questions)
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        val jsonString = bufferedReader.use { it.readText() }
        val jsonObject = JSONObject(jsonString)
        val questionsArray = jsonObject.getJSONArray("questions")

        val questionsList = mutableListOf<EntityPregunta>()
        for (i in 0 until questionsArray.length()) {
            val questionObj = questionsArray.getJSONObject(i)
            val questionText = questionObj.getString("question")
            val answers = questionObj.getJSONArray("answers")

            questionsList.add(
                EntityPregunta(
                    enunciado = questionText,
                    opcionA = answers.getJSONObject(0).getString("text"),
                    opcionB = answers.getJSONObject(1).getString("text"),
                    opcionC = answers.getJSONObject(2).getString("text"),
                    opcionCorrecta = questionObj.getString("correctAnswerId"),
                    tema = "General",
                    nivel = when (questionObj.optString("level", "A1")) {
                        "A1" -> 1
                        "A2" -> 2
                        "B1" -> 3
                        "B2" -> 4
                        else -> 1
                    }
                )
            )
        }
        return questionsList
    }
}
