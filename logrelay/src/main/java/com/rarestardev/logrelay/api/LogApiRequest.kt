/**
 * @author rarestardev
 * Website: rarestar.kavix-team.com
 */
package com.rarestardev.logrelay.api

import com.rarestardev.logrelay.model.UploadLogsRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

interface LogApiRequest {

    @POST
    suspend fun uploadBatch(
        @Url url: String,
        @Body request: UploadLogsRequest
    ): Response<Unit>
}