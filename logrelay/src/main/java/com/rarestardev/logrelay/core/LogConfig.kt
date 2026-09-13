/**
 * @author rarestardev
 * Website: rarestar.kavix-team.com
 */
package com.rarestardev.logrelay.core

/**
 * Configuration for the LogRelay library.
 *
 * @property serverUrl The URL of the server to send logs to (WebSocket or REST).
 * @property uploadPath The API endpoint path for batch uploading logs (default: "api/logs/batch").
 * @property authToken Optional Bearer token for authentication.
 * @property periodicSyncEnabled If true, a background worker will sync all logs every 24 hours.
 * @property connectionMode if normal send data with api request else WEB_SOCKET send realtime log to server with socket
 */
data class LogConfig(
    val serverUrl: String,
    val uploadPath: String = "api/logs/batch",
    val authToken: String? = null,
    val periodicSyncEnabled: Boolean = true,
    val connectionMode: LogConnectionMode = LogConnectionMode.NORMAL
)
