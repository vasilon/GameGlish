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
    suspend fun insertarPreguntasGramaticaDesdeJson(context: Context) {
        val preguntas = readGrammarQuestionsFromJson(context)
        withContext(Dispatchers.IO) {
            db.preguntaDao().insertPreguntas(preguntas)
        }
    }

    private fun readGrammarQuestionsFromJson(context: Context): List<EntityPregunta> {
        val inputStream = context.resources.openRawResource(R.raw.grammar_questions)
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val jsonObject = JSONObject(jsonString)
        val questionsArray = jsonObject.getJSONArray("questions")
        val questionsList = mutableListOf<EntityPregunta>()
        for (i in 0 until questionsArray.length()) {
            val questionObj = questionsArray.getJSONObject(i)
            val enunciado = questionObj.getString("question")
            val answersArray = questionObj.getJSONArray("answers")
            val opcionA = answersArray.getJSONObject(0).getString("text")
            val opcionB = answersArray.getJSONObject(1).getString("text")
            val opcionC = answersArray.getJSONObject(2).getString("text")
            val opcionCorrecta = questionObj.getString("correctAnswerId")
            // Asignamos "Gramatica" como tema para estas preguntas
            val tema = "Gramatica"
            val nivelStr = questionObj.getString("level")
            val nivel = when (nivelStr) {
                "A1" -> 1
                "A2" -> 2
                "B1" -> 3
                "B2" -> 4
                else -> 1
            }
            questionsList.add(
                EntityPregunta(
                    enunciado = enunciado,
                    opcionA = opcionA,
                    opcionB = opcionB,
                    opcionC = opcionC,
                    opcionCorrecta = opcionCorrecta,
                    tema = tema,
                    nivel = nivel
                )
            )
        }
        return questionsList
    }
}
