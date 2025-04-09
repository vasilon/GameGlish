// Kotlin
package com.example.gameglish.data.repository

import android.util.Log
import com.example.gameglish.data.database.GameGlishDatabase
import com.example.gameglish.data.model.EntityUsuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

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


    fun obtenerUidUsuarioActual(): String? {
        return auth.currentUser?.uid
    }

    suspend fun registrarUsuarioCorreo(
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        if (password != confirmPassword) {
            return false
        }
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: return false

            val usuario = EntityUsuario(
                uidFirebase = uid,
                email = email,
                nombre = "",
                puntos = 0,
                nivel = 0,
                firstLogin = true
            )

            guardarUsuarioLocal(usuario)
            withTimeoutOrNull(3000L) {
                guardarUsuarioRemoto(usuario)
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    suspend fun actualizarUsuarioProfile(uid: String, nombre: String, nivelSeleccionado: String) {
        // Try to retrieve the user locally.
        var usuario = obtenerUsuarioLocal(uid)

        // Define the mapping from text levels to integers.
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

        // If the user doesn't exist locally, create a new instance.
        if (usuario == null) {
            usuario = EntityUsuario(
                uidFirebase = uid,
                email = "",  // If you have the email available, pass it here.
                nombre = nombre,
                puntos = 0,
                nivel = nivelInt,
                firstLogin = false  // Set firstLogin to false since this is not the first login.
            )
        } else {
            // Otherwise, update the existing record.
            usuario = usuario.copy(nombre = nombre, nivel = nivelInt , firstLogin = false)
        }

        // Save the updated (or new) user data locally and remotely.
        guardarUsuarioLocal(usuario)
        guardarUsuarioRemoto(usuario)
    }


    suspend fun guardarUsuarioLocal(usuario: EntityUsuario) {
        db.usuarioDao().insertarUsuario(usuario)
        Log.e("RepositoryUsuario", "guardarUsuarioLocal: $usuario")
    }

    suspend fun obtenerUsuarioLocal(uid: String): EntityUsuario? {
        return db.usuarioDao().getUsuarioPorUid(uid)
    }

    suspend fun guardarUsuarioRemoto(usuario: EntityUsuario) {
        val uid = auth.currentUser?.uid ?: return
        remoteDb.child("usuarios").child(uid).setValue(usuario).await()
        Log.e("RepositoryUsuario", "guardarUsuarioRemoto: $uid")
    }

    suspend fun obtenerUsuarioRemoto(uid: String): EntityUsuario? {
        return try {
            val snapshot = remoteDb.child("usuarios").child(uid).get().await()
            snapshot.getValue(EntityUsuario::class.java)
        } catch (e: Exception) {
            null
        }
    }
    suspend fun marcarPrimerLoginCompleto(uid: String) {
        try {
            // Primero actualizamos la base de datos remota
            remoteDb.child("usuarios").child(uid).child("firstLogin").setValue(false).await()
            Log.e("RepositoryUsuario", "Remoto actualizado: firstLogin = false")

            // Después actualizamos la base de datos local
            val usuarioLocal = obtenerUsuarioLocal(uid)
            usuarioLocal?.let {
                it.firstLogin = false
                guardarUsuarioLocal(it)
                Log.e("RepositoryUsuario", "Local actualizado: firstLogin = false")
            }
        } catch (e: Exception) {
            Log.e("RepositoryUsuario", "Error al marcar primer login completo", e)
        }
    }
}