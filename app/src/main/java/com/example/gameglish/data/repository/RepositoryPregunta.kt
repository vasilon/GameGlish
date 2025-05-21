package com.example.gameglish.data.repository

import com.example.gameglish.data.database.GameGlishDatabase
import com.example.gameglish.data.model.EntityPregunta
import android.content.Context
import androidx.annotation.RawRes
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
    /**
     * Inserta desde el JSON correspondiente según el tema:
     * - "Gramatica" → grammar_questions.json
     * - "Vocabulario" → vocabulary_questions.json
     * - "Reading" → reading_questions.json
     */
    suspend fun insertarPreguntasDesdeJson(context: Context, tema: String) {
        val lista: List<EntityPregunta> = when (tema) {
            "Gramatica"   -> readGrammarQuestionsFromJson(context)
            "Vocabulario" -> readVocabularyQuestionsFromJson(context)
            "Reading"     -> readReadingQuestionsFromJson(context)
            else          -> throw IllegalArgumentException("Tema desconocido: $tema")
        }
        withContext(Dispatchers.IO) {
            db.preguntaDao().insertPreguntas(lista)
        }
    }

    // Cada uno de estos métodos lee su JSON y asigna el tema recibido
    private fun readGrammarQuestionsFromJson(context: Context): List<EntityPregunta> =
        readQuestionsFromJson(context, R.raw.grammar_questions, "Gramatica")

    private fun readVocabularyQuestionsFromJson(context: Context): List<EntityPregunta> =
        readQuestionsFromJson(context, R.raw.vocabulary_questions, "Vocabulario")

    private fun readReadingQuestionsFromJson(context: Context): List<EntityPregunta> =
        readQuestionsFromJson(context, R.raw.reading_questions, "Reading")

    // Función genérica para parsear cualquier JSON de preguntas
    private fun readQuestionsFromJson(
        context: Context,
        @RawRes rawResId: Int,
        tema: String
    ): List<EntityPregunta> {
        val inputStream = context.resources.openRawResource(rawResId)
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val questionsArray = JSONObject(jsonString).getJSONArray("questions")

        return List(questionsArray.length()) { i ->
            val obj = questionsArray.getJSONObject(i)
            val enunciado = obj.getString("question")
            val answers = obj.getJSONArray("answers")
            val opcionA = answers.getJSONObject(0).getString("text")
            val opcionB = answers.getJSONObject(1).getString("text")
            val opcionC = answers.getJSONObject(2).getString("text")
            val opcionD = answers.getJSONObject(3).getString("text")
            val opcionCorrecta = obj.getString("correctAnswerId")
            val nivel = when (obj.getString("level")) {
                "A1"     -> 1
                "A2"     -> 2
                "B1"     -> 3
                "B2"     -> 4
                "C1"     -> 5
                "C2"     -> 6
                "NATIVE" -> 7
                else     -> 1
            }

            EntityPregunta(
                enunciado       = enunciado,
                opcionA         = opcionA,
                opcionB         = opcionB,
                opcionC         = opcionC,
                opcionD         = opcionD,
                opcionCorrecta  = opcionCorrecta,
                tema            = tema,
                nivel           = nivel
            )
        }
    }
}
