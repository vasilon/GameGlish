package com.example.gameglish.data.repository

import android.util.Log
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

    suspend fun registrarUsuarioCorreo(email: String, password: String, confirmpassword: String): Boolean {
        return try {
            if (password != confirmpassword) {
                return false
            }
            Log.d("RepositoryUsuario", "Creating user with email: $email")
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: return false

            val usuarioData = mapOf(
                "email" to email,
                "puntos" to 0,
                "nivel" to 1
            )
            Log.d("RepositoryUsuario", "Saving user data to remote database for userId: $userId")

            // 🔹 Realizar setValue() sin bloquear el flujo
            remoteDb.child("usuarios").child(userId).setValue(usuarioData)

            Log.d("RepositoryUsuario", "User data saved successfully")
            return true
        } catch (e: Exception) {
            Log.e("RepositoryUsuario", "Error registering user: ${e.message}")
            e.printStackTrace()
            return false
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
