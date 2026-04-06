package com.example.pcclubkt.database

import androidx.room.Dao
import androidx.room.Query

@Dao
interface LoginDao {
    @Query("SELECT * FROM login WHERE Username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): LoginEntity?
}