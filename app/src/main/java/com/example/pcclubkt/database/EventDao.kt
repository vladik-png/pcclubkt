package com.example.pcclubkt.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Query("SELECT * FROM Events ORDER BY EventID DESC")
    fun getAllEvents(): Flow<List<EventsEntity>>

    @Insert
    suspend fun insertEvent(event: EventsEntity)

    @Query("UPDATE Events SET Status = 1 WHERE EventID = :eventId")
    suspend fun completeEvent(eventId: Int)
}