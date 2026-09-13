/**
 * @author rarestardev
 * Website: rarestar.kavix-team.com
 */
package com.rarestardev.logrelay.api

import com.rarestardev.logrelay.database.LogEntity
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

interface LogApiRequest {

    @POST
    suspend fun uploadBatch(
        @Url url: String,
        @Body logs: List<LogEntity>
    ): Response<Unit>
}