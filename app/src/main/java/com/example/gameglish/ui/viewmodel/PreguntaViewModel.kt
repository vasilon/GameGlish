package com.example.gameglish.ui.viewmodel

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import com.example.gameglish.data.database.GameGlishDatabase
import com.example.gameglish.data.model.EntityPregunta
import com.example.gameglish.data.repository.RepositoryPregunta
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.gameglish.data.repository.RepositoryUsuario


class PreguntaViewModel(application: Application) : AndroidViewModel(application) {

    private val preguntaRepository: RepositoryPregunta

    init {
        val db = GameGlishDatabase.getDatabase(application)
        preguntaRepository = RepositoryPregunta(db)
    }

//    fun cargarPreguntas(context: Context) {
//        viewModelScope.launch {
//            preguntaRepository.insertarPreguntasDesdeJson(context)
//        }
//    }

    fun obtenerPreguntasPorTema(tema: String): LiveData<List<EntityPregunta>> {
        val preguntasLiveData = MutableLiveData<List<EntityPregunta>>()
        viewModelScope.launch {
            preguntasLiveData.postValue(preguntaRepository.getPreguntasPorTema(tema))
        }
        return preguntasLiveData
    }
}
