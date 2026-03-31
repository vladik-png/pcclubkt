package com.example.pcclubkt

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AdminDao {
    @Query("SELECT * FROM admins WHERE login = :login LIMIT 1")
    suspend fun getAdminByLogin(login: String): AdminEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdmin(admins: AdminEntity)
}