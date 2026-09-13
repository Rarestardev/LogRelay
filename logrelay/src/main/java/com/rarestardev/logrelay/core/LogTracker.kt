/**
 * Author: rarestardev
 * Website: rarestar.kavix-team.com
 */
package com.rarestardev.logrelay.core

import android.content.Context
import android.util.Log
import androidx.work.*
import com.rarestardev.logrelay.api.LogWebSocketManager
import com.rarestardev.logrelay.database.LogEntity
import com.rarestardev.logrelay.database.LogRelayDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

/**
 * The main entry point for the LogRelay library.
 */
object LogTracker {

    private var config: LogConfig? = null
    private var database: LogRelayDatabase? = null
    private var webSocketManager: LogWebSocketManager? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    /**
     * Initializes the LogTracker with the given configuration.
     * This should be called in the Application's onCreate method.
     */
    fun initialize(context: Context, config: LogConfig) {
        this.config = config
        this.database = LogRelayDatabase.getInstance(context)

        if (config.realtimeEnabled) {
            webSocketManager = LogWebSocketManager(config.serverUrl)
            webSocketManager?.connect()
        }

        if (config.periodicSyncEnabled) {
            schedulePeriodicSync(context)
        }
    }

    /**
     * Schedules a periodic sync worker to run every 24 hours.
     */
    private fun schedulePeriodicSync(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<LogSyncWorker>(24, TimeUnit.HOURS)
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 15, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "LogSyncWorker",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }

    /**
     * Logs a message to the database and optionally sends it via WebSocket.
     */
    fun log(tag: String, message: String, level: String = "INFO", throwable: Throwable? = null) {
        val entity = LogEntity(
            tag = tag,
            message = message,
            level = level,
            throwable = throwable?.stackTraceToString()
        )

        scope.launch {
            // Always save to database for persistence and periodic sync
            database?.logRelayDao()?.insertLog(entity)

            // If realtime is enabled, attempt to send via WebSocket
            if (config?.realtimeEnabled == true) {
                webSocketManager?.sendLog(entity)
            }
        }
    }

    /**
     * Returns a Flow of all logs stored in the database, ordered by timestamp.
     */
    fun getAllLogs(): Flow<List<LogEntity>> {
        return database?.logRelayDao()?.getAllLogs() ?: emptyFlow()
    }

    /**
     * Helper for logging errors easily.
     */
    fun e(tag: String, message: String, throwable: Throwable? = null) {
        log(tag, message, "ERROR", throwable)
        Log.e(tag, message, throwable)
    }

    /**
     * Helper for logging info messages easily.
     */
    fun i(tag: String, message: String) {
        log(tag, message, "INFO")
        Log.i(tag, message)
    }

    /**
     * Helper for logging debug messages easily.
     */
    fun d(tag: String, message: String) {
        log(tag, message, "DEBUG")
        Log.d(tag, message)
    }

    fun w(tag: String, message: String, throwable: Throwable? = null) {
        log(tag, message, "WARNING", throwable)
        Log.w(tag, message, throwable)
    }

    fun wtf(tag: String, message: String, throwable: Throwable? = null) {
        log(tag, message, "ASSERT", throwable)
        Log.wtf(tag, message, throwable)
    }

    fun v(tag: String, message: String, throwable: Throwable? = null) {
        log(tag, message, "VERBOSE", throwable)
        Log.v(tag, message, throwable)
    }
}
