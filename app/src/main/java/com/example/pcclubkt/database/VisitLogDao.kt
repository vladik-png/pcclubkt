package com.example.pcclubkt.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface VisitLogDao {
    @Query("SELECT * FROM VisitLog WHERE CustomerID = :clientId ORDER BY StartTime DESC")
    fun getLogsForCustomer(clientId: Int): Flow<List<VisitLogEntity>>

    @Insert
    suspend fun insertLog(log: VisitLogEntity)

    @Query("SELECT * FROM VisitLog WHERE ComputerID = :pcId ORDER BY VisitID DESC LIMIT 1")
    suspend fun getActiveLogForComputer(pcId: Int): VisitLogEntity?

    @Update
    suspend fun updateLog(log: VisitLogEntity)
}