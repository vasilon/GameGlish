package com.example.gameglish.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class UsuarioRepository {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    fun registrarUsuarioCorreo(email: String, password: String, onComplete: (Boolean) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = task.result?.user?.uid
                    // Crea una entrada en la DB con los datos básicos del usuario
                    userId?.let {
                        val usuarioData = mapOf(
                            "email" to email,
                            "puntos" to 0,
                            "nivel" to 1
                        )
                        database.child("usuarios").child(it).setValue(usuarioData)
                    }
                    onComplete(true)
                } else {
                    onComplete(false)
                }
            }
    }

    fun iniciarSesionCorreo(email: String, password: String, onComplete: (Boolean) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    // Otros métodos para leer/escribir datos del usuario en Realtime Database
}
