// Kotlin
package com.example.gameglish.data.repository

import com.example.gameglish.data.database.GameGlishDatabase
import com.example.gameglish.data.model.EntityUsuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class RepositoryUsuario(
    private val db: GameGlishDatabase
) {
    private val auth = FirebaseAuth.getInstance()
    private val remoteDb = FirebaseDatabase.getInstance().reference

    suspend fun iniciarSesionCorreo(email: String, password: String): Boolean {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user != null
        } catch (e: Exception) {
            false
        }
    }

    suspend fun registrarUsuarioCorreo(
        email: String,
        password: String,
        confirmPassword: String,
        nombre: String,
        nivelSeleccionado: String
    ): Boolean {
        if (password != confirmPassword) {
            return false
        }
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: return false

            val nivelMap = mapOf(
                "A1" to 1,
                "A2" to 2,
                "B1" to 3,
                "B2" to 4,
                "C1" to 5,
                "C2" to 6,
                "NATIVE" to 7
            )
            val nivelInt = nivelMap[nivelSeleccionado] ?: 1

            val usuario = EntityUsuario(
                uidFirebase = uid,
                email = email,
                nombre = nombre,
                puntos = 0,
                nivel = nivelInt
            )
            guardarUsuarioLocal(usuario)
            guardarUsuarioRemoto(usuario)
            val usuarioData = mapOf(
                "email" to email,
                "nombre" to nombre,
                "puntos" to 0,
                "nivel" to nivelSeleccionado
            )
            remoteDb.child("usuarios").child(uid).setValue(usuarioData).await()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    // New function to update user profile details.
    suspend fun actualizarUsuarioProfile(uid: String, nombre: String, nivelSeleccionado: String) {
        val usuario = obtenerUsuarioLocal(uid)
        if (usuario != null) {
            val nivelMap = mapOf(
                "A1" to 1,
                "A2" to 2,
                "B1" to 3,
                "B2" to 4,
                "C1" to 5,
                "C2" to 6,
                "NATIVE" to 7
            )
            val nivelInt = nivelMap[nivelSeleccionado] ?: usuario.nivel
            val updatedUsuario = usuario.copy(nombre = nombre, nivel = nivelInt)
            guardarUsuarioLocal(updatedUsuario)
            guardarUsuarioRemoto(updatedUsuario)
        }
    }

    suspend fun guardarUsuarioLocal(usuario: EntityUsuario) {
        db.usuarioDao().insertarUsuario(usuario)
    }

    suspend fun obtenerUsuarioLocal(uid: String): EntityUsuario? {
        return db.usuarioDao().getUsuarioPorUid(uid)
    }

    suspend fun guardarUsuarioRemoto(usuario: EntityUsuario) {
        val uid = auth.currentUser?.uid ?: return
        remoteDb.child("usuarios").child(uid).setValue(usuario).await()
    }
}