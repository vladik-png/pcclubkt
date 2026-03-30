package com.example.pcclubkt

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PcDao {
    @Query("SELECT * FROM computers ORDER BY id ASC")
    fun getAllComputers(): Flow<List<PcEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertComputers(computers: List<PcEntity>)

    @Query("UPDATE computers SET isOccupied = :isOccupied WHERE id = :pcId")
    suspend fun updateComputerStatus(pcId: Int, isOccupied: Boolean)
}