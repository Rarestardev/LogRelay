package com.rarestardev.logrelay

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import kotlin.jvm.java

@Database(entities = [LogEntity::class], version = 1, exportSchema = false)
abstract class LogRelayDatabase {

    companion object {
        private const val DB_NAME = "LOG_RELAY"
        @Volatile private var INSTANCE: LogRelayDatabase? = null

        fun getInstance(context: Context): LogRelayDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    LogRelayDatabase::class.java,
                    DB_NAME
                )
            }
        }
    }
}