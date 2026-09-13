package com.rarestardev.logrelay.core

/**
 * Configuration for the LogRelay library.
 *
 * @property serverUrl The URL of the server to send logs to (WebSocket or REST).
 * @property realtimeEnabled If true, logs will be sent immediately via WebSocket.
 * @property periodicSyncEnabled If true, a background worker will sync all logs every 24 hours.
 */
data class LogConfig(
    val serverUrl: String,
    val realtimeEnabled: Boolean = true,
    val periodicSyncEnabled: Boolean = true
)
