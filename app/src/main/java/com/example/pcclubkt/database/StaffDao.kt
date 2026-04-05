package com.example.pcclubkt.database
import androidx.room.Dao
import androidx.room.Query

@Dao
interface StaffDao {
    @Query("SELECT * FROM Staff WHERE Email LIKE :username || '%' LIMIT 1")
    suspend fun getStaffByUsername(username: String): StaffEntity?
}