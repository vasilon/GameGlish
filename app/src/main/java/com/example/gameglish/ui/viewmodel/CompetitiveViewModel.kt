package com.example.gameglish.ui.viewmodel

import android.app.Application
import android.content.Context
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gameglish.data.database.GameGlishDatabase
import com.example.gameglish.data.model.CompetitiveGame
import com.example.gameglish.data.model.CompetitiveQuestion
import com.example.gameglish.data.model.toCompetitiveQuestion
import com.example.gameglish.data.repository.RepositoryCompetitivo
import com.example.gameglish.data.repository.RepositoryPregunta
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class CompetitiveGameViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = RepositoryCompetitivo()
    // UID del usuario actual
    val currentUserId: String = FirebaseAuth.getInstance().currentUser?.uid ?: "unknown"
    private var answersListenerAdded = false
    private val dbLocal = GameGlishDatabase.getDatabase(getApplication())
    private val repositoryPregunta = RepositoryPregunta(dbLocal)
    private var countDownTimer: CountDownTimer? = null
    // StateFlow para exponer el estado del juego
    private val _gameState = MutableStateFlow(CompetitiveGame())
    val gameState: StateFlow<CompetitiveGame> = _gameState
    private val _preguntas = mutableListOf<CompetitiveQuestion>()
    private var indicePreguntaActual = 0
    private val firebaseUrl = "https://gameglish-default-rtdb.europe-west1.firebasedatabase.app"
    private val dbRef = FirebaseDatabase.getInstance(firebaseUrl).getReference("competitivo/games")
    private var rondaCerrada = false
    // StateFlow para exponer la lista de juegos disponibles
    private val _availableGames = MutableStateFlow<List<CompetitiveGame>>(emptyList())
    val availableGames: StateFlow<List<CompetitiveGame>> = _availableGames

    // Mapa de niveles de idioma
    private val levelMap = mapOf(
        1 to "A1",
        2 to "A2",
        3 to "B1",
        4 to "B2",
        5 to "C1",
        6 to "C2",
        7 to "NATIVE"
    )

    fun createGame(onResult: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val gameId = repository.createGame(currentUserId)
                onResult(gameId)
            } catch (e: Exception) {
                Log.e("CompetitiveGameVM", "Error creating game: ${e.message}")
                onResult("")
            }
        }
    }

    fun joinGame(gameId: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                repository.joinGame(gameId, currentUserId)
                onResult(true)
            } catch (e: Exception) {
                Log.e("CompetitiveGameVM", "Error joining game: ${e.message}")
                onResult(false)
            }
        }
    }

    fun observeGame(gameId: String) {
        dbRef.child(gameId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val game = snapshot.getValue(CompetitiveGame::class.java) ?: return
                _gameState.value = game

                // ── NUEVO BLOQUE ───────────────────────────────────────────
                val soyHost   = currentUserId == game.hostId
                val joinerOk  = !game.joinerId.isNullOrEmpty()
                if (soyHost && joinerOk && game.state == "waiting") {
                    // 1) cambio de estado
                    dbRef.child(gameId).child("state").setValue("inProgress")
                    // 2) cargo preguntas y envío la primera (una sola vez)
                    viewModelScope.launch {
                        cargarYEnviarPreguntas(gameId)  // ya no necesita context
                    }
                }
                if (!answersListenerAdded) {
                    escucharRespuestas(gameId)
                    answersListenerAdded = true
                }
                // ──────────────────────────────────────────────────────────
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("CompetitiveGameVM", "observeGame cancel: ${error.message}")
            }
        })
    }
    fun observeGameStatus(gameId: String, onGameStarted: () -> Unit) {
        val gameRef = dbRef.child(gameId)
        gameRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val game = snapshot.getValue(CompetitiveGame::class.java) ?: return
                if (game.state == "inProgress" && !game.joinerId.isNullOrEmpty()) {
                    onGameStarted()
                    gameRef.removeEventListener(this)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("CompetitiveGameVM", "observeGameStatus cancelled: ${error.message}")
            }
        })
    }


    fun getUserName(uid: String, onResult: (String) -> Unit) {
        val userRef = FirebaseDatabase.getInstance(firebaseUrl)
            .getReference("usuarios")
            .child(uid)
            .child("nombre")
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.getValue(String::class.java) ?: "Unknown"
                onResult(name)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("CompetitiveGameVM", "getUserName cancelled: ${error.message}")
                onResult("Unknown")
            }
        })
    }

    fun sendAnswer(gameId: String, letter: String) {
        val role = if (currentUserId == _gameState.value.hostId) "host" else "joiner"
        dbRef.child(gameId).child("answers").child(role).setValue(letter)
    }

    fun handleTimeUp(gameId: String) {
        viewModelScope.launch {
            val currentGame = _gameState.value
            val isHost = (currentUserId == currentGame.hostId)
            val newLives = if (isHost) currentGame.hostLives - 1 else currentGame.joinerLives - 1

            if (isHost) {
                dbRef.child(gameId).child("hostLives").setValue(newLives)
            } else {
                dbRef.child(gameId).child("joinerLives").setValue(newLives)
            }
        }
    }

    fun fetchAvailableGames() {
        val query = FirebaseDatabase.getInstance(firebaseUrl)
            .getReference("competitivo/games")
            .orderByChild("state")
            .equalTo("waiting")
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val games = mutableListOf<CompetitiveGame>()
                for (child in snapshot.children) {
                    val game = child.getValue(CompetitiveGame::class.java)
                    if (game != null) games.add(game)
                }
                _availableGames.value = games
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("CompetitiveGameVM", "fetchAvailableGames cancelled: ${error.message}")
            }
        })
    }
    fun getUserProfile(uid: String, onResult: (UserProfile) -> Unit) {
        val userRef = FirebaseDatabase.getInstance(firebaseUrl)
            .getReference("usuarios")
            .child(uid)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val nombre = snapshot.child("nombre").getValue(String::class.java) ?: "Unknown"
                val nivel = snapshot.child("nivel").getValue(Int::class.java) ?: 1
                onResult(UserProfile(nombre, nivel))
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("CompetitiveGameVM", "getUserProfile cancelled: ${error.message}")
                onResult(UserProfile()) // valores por defecto
            }
        })
    }
    fun intToLevelString(nivel: Int): String {
        return levelMap[nivel] ?: "A1"
    }

    private fun arrancarTimer(gameId: String) {
        if (currentUserId != _gameState.value.hostId) return   // solo el host

        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(20_000, 1_000) {
            override fun onTick(ms: Long) {
                dbRef.child(gameId)
                    .child("timeLeft")
                    .setValue((ms / 1000).toInt())
            }

            override fun onFinish() {
                /*  ⬇️ 1) fuerza timeLeft = 0 para ambas apps */
                dbRef.child(gameId).child("timeLeft").setValue(0)

                /*  ⬇️ 2) Escribe “noAnswer” al que no contestó todavía */
                dbRef.child(gameId).child("answers")
                    .runTransaction(object : Transaction.Handler {
                        override fun doTransaction(m: MutableData): Transaction.Result {

                            /* ⬇️ Si todavía no hay valor para host o joiner → “noAnswer” */
                            if (m.child("host").getValue(String::class.java) == null)
                                m.child("host").value = "noAnswer"

                            if (m.child("joiner").getValue(String::class.java) == null)
                                m.child("joiner").value = "noAnswer"

                            return Transaction.success(m)
                        }
                        override fun onComplete(
                            e: DatabaseError?, committed: Boolean, snap: DataSnapshot?
                        ) {
                            cerrarRonda(gameId)
                        }
                    })
            }
        }.start()
    }

    fun pushPreguntaActual(gameId: String) {
         if (_preguntas.isEmpty() || indicePreguntaActual !in _preguntas.indices) return
         val pregunta = _preguntas[indicePreguntaActual]
        dbRef.child(gameId).apply {
            child("currentQuestion").setValue(pregunta.question)
            child("answerOptions").setValue(pregunta.options)
            child("correctId").setValue(pregunta.correctId)   // ⚠️ solo host
            child("timeLeft").setValue(20)
            child("answers").setValue(null)                   // limpia respuestas
        }
        arrancarTimer(gameId)                                 // si ya la creaste
    }

    private suspend fun cargarYEnviarPreguntas(gameId: String) {
        val ctx = getApplication<Application>().applicationContext

        val tema = "Gramatica"
        var preguntasDb = withContext(Dispatchers.IO) {
            repositoryPregunta.getPreguntasPorTema(tema)
        }

        // ⬇️  Si está vacía, importamos el JSON una sola vez
        if (preguntasDb.isEmpty()) {
            withContext(Dispatchers.IO) {
                repositoryPregunta.insertarPreguntasDesdeJson(ctx, tema)
                preguntasDb = repositoryPregunta.getPreguntasPorTema(tema)
            }
        }

        // … resto sin cambios …
        val listaCompetitivo = preguntasDb
            .map { it.toCompetitiveQuestion() }
            .shuffled(Random(gameId.hashCode()))

        _preguntas.clear(); _preguntas += listaCompetitivo
        indicePreguntaActual = 0

        if (_preguntas.isNotEmpty()) {
            pushPreguntaActual(gameId)
        } else {
            // Algo anda mal: no hay preguntas siquiera tras la importación
            Log.e("CompetitiveGameVM", "No se pudieron cargar preguntas de gramática")
            dbRef.child(gameId).child("state").setValue("finished")
        }
    }

    private fun escucharRespuestas(gameId: String) {
        dbRef.child(gameId).child("answers")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snap: DataSnapshot) {
                    // 👉 solo el HOST procesa la ronda
                    if (currentUserId != _gameState.value.hostId) return

                    val host = snap.child("host").getValue(String::class.java)
                    val join = snap.child("joiner").getValue(String::class.java)
                    if (host != null && join != null && _gameState.value.timeLeft > 0) {
                        cerrarRonda(gameId)
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }


    private fun cerrarRonda(gameId: String) {

        if (rondaCerrada) return
        rondaCerrada = true

        countDownTimer?.cancel()
        dbRef.child(gameId).runTransaction(object : Transaction.Handler {
            override fun doTransaction(m: MutableData): Transaction.Result {
                val correct = m.child("correctId").getValue(String::class.java) ?: ""
                val hostAns = m.child("answers").child("host").getValue(String::class.java)
                val joinAns = m.child("answers").child("joiner").getValue(String::class.java)

                var hostLives = m.child("hostLives").getValue(Int::class.java) ?: 3
                var joinLives = m.child("joinerLives").getValue(Int::class.java) ?: 3

                if (hostAns != correct) hostLives--
                if (joinAns != correct) joinLives--

                m.child("hostLives").value = hostLives
                m.child("joinerLives").value = joinLives

                // Fin de partida
                if (hostLives == 0 || joinLives == 0) {
                    m.child("state").value = "finished"
                    m.child("winner").value = when {
                        hostLives > joinLives -> m.child("hostId").value
                        joinLives > hostLives -> m.child("joinerId").value
                        else -> "draw"
                    }
                    return Transaction.success(m)
                }

                // Siguiente pregunta
                indicePreguntaActual++
                val preguntasLeft = _preguntas.size - indicePreguntaActual
                if (preguntasLeft == 0) {              // sin más preguntas → empate
                    m.child("state").value = "finished"
                    m.child("winner").value = "draw"
                    return Transaction.success(m)
                }

                val next = _preguntas[indicePreguntaActual]
                m.child("currentQuestion").value = next.question
                m.child("answerOptions").value = next.options
                m.child("correctId").value = next.correctId
                m.child("timeLeft").value = 20
                m.child("answers").value = null        // limpia respuestas
                return Transaction.success(m)
            }
            override fun onComplete(e: DatabaseError?, committed: Boolean, snap: DataSnapshot?) {
                /* 2. Si sigue la partida, reinicia flag y timer */
                if (committed && _gameState.value.state == "inProgress") {
                    rondaCerrada = false
                    arrancarTimer(gameId)
                }
            }
        })
    }


}


data class UserProfile(
    val nombre: String = "Unknown",
    val nivel: Int = 1
)