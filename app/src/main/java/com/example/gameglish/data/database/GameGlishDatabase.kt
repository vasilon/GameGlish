// GameGlishDatabase.kt
package com.example.gameglish.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.gameglish.data.model.EntityUsuario
import com.example.gameglish.data.model.EntityPregunta
import com.example.gameglish.data.model.EntityEstadistica

@Database(
    entities = [
        EntityUsuario::class,
        EntityPregunta::class,
        EntityEstadistica::class
    ],
    version = 1,
    exportSchema = false
)
abstract class GameGlishDatabase : RoomDatabase() {

    abstract fun usuarioDao(): DaoUsuario
    abstract fun preguntaDao(): DaoPregunta
    abstract fun estadisticaDao(): DaoEstadistica

    companion object {
        @Volatile
        private var INSTANCE: GameGlishDatabase? = null

        fun getDatabase(context: Context): GameGlishDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GameGlishDatabase::class.java,
                    "gameglish_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}