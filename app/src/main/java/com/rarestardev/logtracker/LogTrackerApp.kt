package com.rarestardev.logtracker

import android.app.Application
import com.rarestardev.logrelay.core.LogConfig
import com.rarestardev.logrelay.core.LogTracker

/**
 * Example Application class to show how to initialize LogTracker.
 */
class LogTrackerApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // 1. Define the configuration
        // In this library, the user can write logs, which are saved and sent via WebSocket.
        // We have two sync options: 24h background sync and real-time.
        val config = LogConfig(
            serverUrl = "wss://your-log-server.com", // Replace with your WebSocket server URL
            realtimeEnabled = true, // Send logs instantly
            periodicSyncEnabled = true // Sync unsent logs every 24 hours in background
        )

        // 2. Initialize the library
        LogTracker.initialize(this, config)

        // 3. Example log
        LogTracker.i("AppInit", "LogTracker has been initialized successfully.")
    }
}
