package com.rarestardev.logrelay.core

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rarestardev.logrelay.api.RetrofitClient
import com.rarestardev.logrelay.database.LogRelayDatabase
import com.rarestardev.logrelay.model.UploadLogsRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Worker that handles periodic syncing of logs to the server.
 * It fetches all logs from the local database and uploads them in a batch.
 */
class LogSyncWorker(
    context: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val database = LogRelayDatabase.getInstance(applicationContext)
        val dao = database.logRelayDao()

        // Fetch all logs currently stored in the database
        val logs = dao.getAllLogsSync()

        if (logs.isEmpty()) {
            return@withContext Result.success()
        }

        return@withContext try {
            // Attempt to upload the logs to the server
            val path = inputData.getString("uploadPath") ?: "api/logs/batch"
            val response = RetrofitClient.logApiRequest.uploadBatch(path, UploadLogsRequest(logs))

            if (response.isSuccessful) {
                // If successful, delete the logs from the local database to save space
                val ids = logs.map { it.logId }
                dao.deleteMultiLogs(ids)
                Log.d("LogSyncWorker", "Successfully synced ${logs.size} logs.")
                Result.success()
            } else {
                Log.e("LogSyncWorker", "Failed to sync logs: ${response.code()}")
                Result.retry() // Retry later if the server is down
            }
        } catch (e: Exception) {
            Log.e("LogSyncWorker", "Error syncing logs: ${e.message}")
            Result.retry() // Retry later if there's a network error
        }
    }
}
