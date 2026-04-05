package com.example.pcclubkt.database
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ComputerDao {
    @Query("SELECT * FROM Computers")
    fun getAllComputersFlow(): Flow<List<ComputerEntity>>

    @Update
    suspend fun updateComputer(computer: ComputerEntity)
}