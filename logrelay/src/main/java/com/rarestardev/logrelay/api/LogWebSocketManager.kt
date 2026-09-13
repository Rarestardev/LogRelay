/**
 * @author rarestardev
 * Website: rarestar.kavix-team.com
 */
package com.rarestardev.logrelay.api

import android.util.Log
import com.google.gson.Gson
import com.rarestardev.logrelay.core.LogConfig
import com.rarestardev.logrelay.core.LogConnectionMode
import com.rarestardev.logrelay.database.LogEntity
import com.rarestardev.logrelay.model.UploadLogsRequest
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody.Companion.toResponseBody
import java.util.concurrent.TimeUnit

/**
 * Manages WebSocket connection for real-time log transmission.
 */
internal class LogWebSocketManager(private val config: LogConfig) {

    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()

    private var webSocket: WebSocket? = null
    private val gson = RetrofitClient.gson

    /**
     * Connects to the WebSocket server.
     */
    fun connect() {
        if (webSocket != null) return

        val requestBuilder = Request.Builder().url(config.serverUrl)

        config.authToken?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }

        val request = requestBuilder.build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("LogWebSocketManager", "WebSocket Connected")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("LogWebSocketManager", "Message received: $text")
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                webSocket.close(1000, null)
                Log.d("LogWebSocketManager", "WebSocket Closing: $reason")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("LogWebSocketManager", "WebSocket Failure: ${t.message}")
                this@LogWebSocketManager.webSocket = null
                // Attempt to reconnect could be implemented here
            }
        })
    }

    /**
     * Sends a single log entry to the server.
     */
    fun sendLog(logEntity: LogEntity) {
        val json = gson.toJson(logEntity)
        webSocket?.send(json) ?: Log.w(
            "LogWebSocketManager",
            "WebSocket not connected. Log cached in DB."
        )
    }

    /**
     * Closes the WebSocket connection.
     */
    fun disconnect() {
        webSocket?.close(1000, "App Closing")
        webSocket = null
    }
}
