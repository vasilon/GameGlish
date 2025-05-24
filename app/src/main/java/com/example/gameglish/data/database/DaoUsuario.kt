// -----------------------------------------------------------------------------
// DaoUsuario.kt
// -----------------------------------------------------------------------------
// Data‑Access‑Object (DAO) para la entidad `EntityUsuario`.
// Expone operaciones CRUD y de observación usadas por los repositorios y
// ViewModels de GameGlish. Los métodos son `suspend` para ejecutarse en
// corrutinas de Kotlin y evitar bloqueos del hilo...
package com.example.gameglish.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gameglish.data.model.EntityUsuario

@Dao
interface DaoUsuario {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarUsuario(usuario: EntityUsuario): Long

    @Query("SELECT * FROM usuarios WHERE uidFirebase = :uid")
    suspend fun getUsuarioPorUid(uid: String): EntityUsuario?

    @Query("SELECT * FROM usuarios WHERE uidFirebase = :uid")
    fun observeUsuarioPorUid(uid: String): LiveData<EntityUsuario?>

    @Query("UPDATE usuarios SET firstLogin = :firstLogin WHERE uidFirebase = :uid")
    suspend fun actualizarIsFirstLogin(uid: String, firstLogin: Boolean)
}