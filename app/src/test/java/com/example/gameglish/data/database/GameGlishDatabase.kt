package com.example.gameglish.data.database

import androidx.room.Room


import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.gameglish.data.model.EntityPregunta
import com.example.gameglish.data.model.EntityEstadistica

@Database(entities = [EntityPregunta::class, EntityEstadistica::class], version = 1, exportSchema = false)
abstract class GameGlishDatabase : RoomDatabase() {
    abstract fun preguntaDao(): PreguntaDao
    abstract fun estadisticaDao(): EstadisticaDao

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
