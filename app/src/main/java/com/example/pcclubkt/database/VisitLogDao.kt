package com.example.pcclubkt.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VisitLogDao {
    @Query("SELECT * FROM VisitLog WHERE CustomerID = :clientId ORDER BY StartTime DESC")
    fun getLogsForCustomer(clientId: Int): Flow<List<VisitLogEntity>>
    @Insert
    suspend fun insertLog(log: VisitLogEntity)
}