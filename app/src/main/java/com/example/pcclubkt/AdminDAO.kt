package com.example.pcclubkt;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;


@Dao
interface AdminDAO {
    @Insert
    suspend fun
            insertAdmin(admin: Admin)
    @Query("SELECT * FROM admins LIMIT 1")
    suspend fun getFirstAdmin(): Admin?
}