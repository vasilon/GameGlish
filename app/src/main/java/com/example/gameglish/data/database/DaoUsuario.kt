// DaoUsuario.kt
package com.example.gameglish.data.database

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
}