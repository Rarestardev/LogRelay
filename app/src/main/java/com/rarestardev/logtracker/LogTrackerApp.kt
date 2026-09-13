/**
 * Author: rarestardev
 * Website: rarestar.kavix-team.com
 */
package com.rarestardev.logtracker

import android.app.Application
import com.rarestardev.logrelay.core.LogConfig
import com.rarestardev.logrelay.core.LogConnectionMode
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
            serverUrl = "http://ronakfilm.dlmax.ir/", // Replace with your WebSocket server URL
            connectionMode = LogConnectionMode.NORMAL, // Send logs instantly
            periodicSyncEnabled = true, // Sync unsent logs every 24 hours in background
            uploadPath = "api/v1.5/logTracker",
            authToken = "f5ee8be9edfc7d6a718134e924fb8eaf98ceb5fa77ab9f40db0f8b8326466fe8"
        )

        // 2. Initialize the library
        LogTracker.initialize(this, config)

        // 3. Example log
        LogTracker.i("AppInit", "LogTracker has been initialized successfully.")
    }

    override fun onTerminate() {
        super.onTerminate()
        LogTracker.stop()
    }
}
