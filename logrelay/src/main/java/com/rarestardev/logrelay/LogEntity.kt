package com.rarestardev.logrelay

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("log_relay_table")
data class LogEntity(
    @PrimaryKey(autoGenerate = true) val logId: Long = 0,
    val tag: String,
    val message: String,
    val level: String,
    val throwable: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
