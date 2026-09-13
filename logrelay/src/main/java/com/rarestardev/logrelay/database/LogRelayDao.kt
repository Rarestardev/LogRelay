package com.rarestardev.logrelay.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LogRelayDao {

    @Insert
    suspend fun insertLog(entity: LogEntity)

    @Delete
    suspend fun deleteSingleLog(entity: LogEntity)

    @Query("DELETE FROM log_relay_table WHERE logId IN (:ids)")
    suspend fun deleteMultiLogs(ids: List<Long>)

    @Query("DELETE FROm log_relay_table")
    suspend fun clearAllLogs()

    @Query("SELECT COUNT(*) FROM log_relay_table")
    suspend fun count(): Long

    @Query("SELECT * FROM log_relay_table ORDER BY timestamp ASC")
    suspend fun getAllLogs() : Flow<List<LogEntity>>

    @Query("SELECT * FROM log_relay_table ORDER BY timestamp ASC LIMIT :limit")
    suspend fun getLogsWithLimit(limit: Int) : Flow<List<LogEntity>>
}