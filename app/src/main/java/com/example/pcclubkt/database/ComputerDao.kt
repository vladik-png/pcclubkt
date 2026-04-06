package com.example.pcclubkt.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface ComputerDao {
    @Transaction
    @Query("SELECT * FROM Computers")
    fun getComputersWithDetails(): kotlinx.coroutines.flow.Flow<List<ComputerWithDetails>>

    @Update
    suspend fun updateComputer(computer: ComputerEntity)

    @Query("UPDATE Computers SET CurrentClientID = :clientId, Status = 'occupied' WHERE ComputerID = :pcId")
    suspend fun assignClientToPc(pcId: Int, clientId: Int)

    @Query("UPDATE Computers SET CurrentClientID = NULL, Status = 'available' WHERE ComputerID = :pcId")
    suspend fun freePc(pcId: Int)
}