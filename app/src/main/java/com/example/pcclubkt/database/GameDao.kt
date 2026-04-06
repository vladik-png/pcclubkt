package com.example.pcclubkt.database

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM Games")
    fun getAllGames(): Flow<List<GamesEntity>>
}