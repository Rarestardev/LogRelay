/**
 * @author rarestardev
 * Website: rarestar.kavix-team.com
 */
package com.rarestardev.logrelay.api

import android.util.Log
import com.google.gson.Gson
import com.rarestardev.logrelay.database.LogEntity
import okhttp3.*
import java.util.concurrent.TimeUnit

/**
 * Manages WebSocket connection for real-time log transmission.
 */
class LogWebSocketManager(private val serverUrl: String) {

    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()

    private var webSocket: WebSocket? = null
    private val gson = Gson()

    /**
     * Connects to the WebSocket server.
     */
    fun connect() {
        if (webSocket != null) return

        val request = Request.Builder()
            .url(serverUrl)
            .build()

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
        webSocket?.send(json) ?: Log.w("LogWebSocketManager", "WebSocket not connected. Log cached in DB.")
    }

    /**
     * Closes the WebSocket connection.
     */
    fun disconnect() {
        webSocket?.close(1000, "App Closing")
        webSocket = null
    }
}
