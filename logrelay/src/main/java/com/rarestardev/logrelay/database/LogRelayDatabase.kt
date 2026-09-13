package com.rarestardev.logrelay.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [LogEntity::class], version = 1, exportSchema = false)
abstract class LogRelayDatabase : RoomDatabase() {
    abstract fun logRelayDao(): LogRelayDao

    companion object {
        private const val DB_NAME = "LOG_RELAY"
        @Volatile
        private var INSTANCE: LogRelayDatabase? = null

        fun getInstance(context: Context): LogRelayDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LogRelayDatabase::class.java,
                    DB_NAME
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}