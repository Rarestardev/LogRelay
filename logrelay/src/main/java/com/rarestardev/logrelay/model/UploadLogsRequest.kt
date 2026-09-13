package com.rarestardev.logrelay.model

import com.google.gson.annotations.SerializedName
import com.rarestardev.logrelay.database.LogEntity

data class UploadLogsRequest(
    @SerializedName("logs") val logs : List<LogEntity>
)
