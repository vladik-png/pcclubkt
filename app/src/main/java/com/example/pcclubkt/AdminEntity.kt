package com.example.pcclubkt

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "admins")
data class AdminEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val login: String,
    val password: String,
    val name: String
)