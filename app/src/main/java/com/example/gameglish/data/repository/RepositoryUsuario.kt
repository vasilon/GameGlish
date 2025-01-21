package com.example.gameglish.data.repository

import com.example.gameglish.data.database.DaoUsuario
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

    suspend fun registrarUsuarioCorreo(email: String, password: String): Boolean {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: return false

            val usuarioData = mapOf(
                "email" to email,
                "puntos" to 0,
                "nivel" to 1
            )
            remoteDb.child("usuarios").child(userId).setValue(usuarioData).await()
            true
        } catch (e: Exception) {
            false
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
