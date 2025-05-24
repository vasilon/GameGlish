// -----------------------------------------------------------------------------
// RepositoryUsuario_comentado.kt
// -----------------------------------------------------------------------------
// Repositorio responsable de todas las operaciones relacionadas con el usuario
// en GameGlish.
//
//   • Autenticación (email/contraseña y Google Sign‑In) usando FirebaseAuth.
//   • Persistencia y sincronización del perfil de usuario entre Room y
//     Firebase Realtime Database.
//   • Exposición reactiva (LiveData) de cambios locales sobre la entidad
//     EntityUsuario.
//
// Estructura de comentado:
//   • Encabezados de bloque explican el propósito global de cada sección.
//   • Comentarios inline detallan pasos críticos, validaciones y decisiones de
//     diseño para facilitar el mantenimiento.
// -----------------------------------------------------------------------------

package com.example.gameglish.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.gameglish.data.database.GameGlishDatabase
import com.example.gameglish.data.model.EntityUsuario
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
/**
 * Repositorio de usuario.
 * @property db Instancia de Room para operaciones locales.
 */
class RepositoryUsuario(
    private val db: GameGlishDatabase
) {
    // -------------------------------------------------------------------------
    // FirebaseAuth+RTDB
    // -------------------------------------------------------------------------
    private val auth = FirebaseAuth.getInstance()
    private val remoteDb = FirebaseDatabase.getInstance().reference
    // -------------------------------------------------------------------------
    // DAO de Room para acceso local.

    private val dao = db.usuarioDao()

    // -------------------------------------------------------------------------
    // Observación reactiva
    // -------------------------------------------------------------------------

    /**
     * Devuelve un LiveData que emite cada vez que cambia el EntityUsuario local.
     * Útil para vincular la UI y reaccionar a actualizaciones sincrónicas.
     */

    fun observeUsuario(uid: String): LiveData<EntityUsuario?> =
        dao.observeUsuarioPorUid(uid)
    // -------------------------------------------------------------------------
    // Autenticación (email / contraseña)
    // -------------------------------------------------------------------------

    /**
     * Inicia sesión con email y contraseña.
     * @return true si el login fue exitoso, false en caso contrario.
     */

    suspend fun iniciarSesionCorreo(email: String, password: String): Boolean {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user != null
        } catch (e: Exception) {
            false
        }
    }

    // -------------------------------------------------------------------------
    // Autenticación (Google)
    // -------------------------------------------------------------------------

    /**
     * Procesa el token de Google y realiza login/registro en FirebaseAuth.
     *  - Si el usuario existía → login.
     *  - Si es nuevo → se crea el usuario y additionalUserInfo.isNewUser == true.
     * @return AuthResult para poder inspeccionar isNewUser desde el ViewModel.
     */

    suspend fun signInWithGoogle(idToken: String): AuthResult {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        // ▸ Si el usuario ya existe en Auth, hace login.
        // ▸ Si no existe, lo crea y marca additionalUserInfo.isNewUser = true
        return auth.signInWithCredential(credential).await()
    }

    // -------------------------------------------------------------------------
    // Registro por email
    // -------------------------------------------------------------------------

    /**
     * Registra un nuevo usuario con email y contraseña.
     * 1. Valida que las contraseñas coincidan.
     * 2. Crea la cuenta en FirebaseAuth.
     * 3. Inserta el perfil vacío en Room y (con timeout) en RTDB.
     *
     * @return true si el registro fue exitoso, false en caso de fallo.
     */

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

    // -------------------------------------------------------------------------
    // Actualización de perfil (nombre, nivel)
    // -------------------------------------------------------------------------

    /**
     * Actualiza nombre y nivel del usuario tanto en local como en remoto.
     * Si el usuario no existía localmente, se crea un nuevo EntityUsuario.
     */

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
        usuario = usuario?.copy(nombre = nombre, nivel = nivelInt, firstLogin = false)
                ?: EntityUsuario(
                    uidFirebase = uid,
                    email = "",  // If you have the email available, pass it here.
                    nombre = nombre,
                    puntos = 0,
                    nivel = nivelInt,
                    firstLogin = false  // Set firstLogin to false since this is not the first login.
                )

        // Save the updated (or new) user data locally and remotely.
        guardarUsuarioLocal(usuario)
        guardarUsuarioRemoto(usuario)
    }
    // -------------------------------------------------------------------------
    // Persistencia local / remota (helpers)
    // -------------------------------------------------------------------------

    /** Inserta o actualiza el usuario en Room (REPLACE). */

    suspend fun guardarUsuarioLocal(usuario: EntityUsuario) {
        db.usuarioDao().insertarUsuario(usuario)
        Log.e("RepositoryUsuario", "guardarUsuarioLocal: $usuario")
    }

    /** Recupera un usuario almacenado localmente, o null si no existe. */

    suspend fun obtenerUsuarioLocal(uid: String): EntityUsuario? {
        return db.usuarioDao().getUsuarioPorUid(uid)
    }
    /** Sube/actualiza el perfil del usuario a Firebase RTDB. */
    suspend fun guardarUsuarioRemoto(usuario: EntityUsuario) {
        val uid = auth.currentUser?.uid ?: return
        remoteDb.child("usuarios").child(uid).setValue(usuario).await()
        Log.e("RepositoryUsuario", "guardarUsuarioRemoto: $uid")
    }
    /** Descarga el perfil del usuario desde RTDB (o null si no existe). */
    suspend fun obtenerUsuarioRemoto(uid: String): EntityUsuario? {
        return try {
            val snapshot = remoteDb.child("usuarios").child(uid).get().await()
            snapshot.getValue(EntityUsuario::class.java)
        } catch (e: Exception) {
            null
        }
    }

    // -------------------------------------------------------------------------
    // Sincronización de nivel + puntos
    // -------------------------------------------------------------------------

    /**
     * Sincroniza simultáneamente nivel y puntos del usuario.
     *   • Actualiza Room para reflejar los nuevos valores y disparar LiveData.
     *   • Sube los datos a RTDB.
     */

    suspend fun actualizarUsuarioNivelYPuntos(
        uid: String, nivelSeleccionado: String, nuevosPuntos: Int
    ) {
        val nivelMap = mapOf(
            "A1" to 1, "A2" to 2, "B1" to 3,
            "B2" to 4, "C1" to 5, "C2" to 6,
            "NATIVE" to 7
        )
        val nivelInt = nivelMap[nivelSeleccionado] ?: 1

        // obtén localmente, o crea uno nuevo si no existe
        val local = dao.getUsuarioPorUid(uid)
            ?: EntityUsuario(uidFirebase = uid,
                nombre = "", email = "",
                puntos = 0, nivel = nivelInt,
                firstLogin = false)

        val actualizado = local.copy(
            puntos = nuevosPuntos,
            nivel  = nivelInt
        )

        // 1) guarda en Room (dispara el LiveData)
        dao.insertarUsuario(actualizado)

        // 2) guarda en remoto
        remoteDb.child("usuarios").child(uid).setValue(actualizado).await()
    }

    // -------------------------------------------------------------------------
    // Flag firstLogin
    // -------------------------------------------------------------------------

    /**
     * Marca el primer login como completado tanto en remoto como en local.
     * Se usa para mostrar un flujo onboarding solo la primera vez.
     */

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