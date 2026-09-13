package com.rarestardev.logrelay.api

import android.util.Log
import com.rarestardev.logrelay.core.LogConfig
import com.rarestardev.logrelay.database.LogEntity
import com.rarestardev.logrelay.model.UploadLogsRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class LogWithHttpManager(
    private val config: LogConfig,
) {

    private val scope = CoroutineScope(Dispatchers.IO)

    fun sendLogWithHttp(logs: List<LogEntity>) {
        val requestData = UploadLogsRequest(logs)

        scope.launch {
            try {
                val response = RetrofitClient.logApiRequest.uploadBatch(
                    url = config.uploadPath,
                    request = requestData
                )

                if (response.isSuccessful) {
                    Log.d("LogWithHttpManager", "Logs uploaded successfully")
                } else {
                    Log.e("LogWithHttpManager", "Failed to upload logs: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("LogWithHttpManager", "Error uploading logs: ${e.message}")
            }
        }
    }
}