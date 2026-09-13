package com.rarestardev.logrelay.api

import com.rarestardev.logrelay.database.LogEntity
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LogApiRequest {

    @POST("api/logs/batch")
    suspend fun uploadBatch(
        @Body logs: List<LogEntity>
    ): Response<Unit>
}