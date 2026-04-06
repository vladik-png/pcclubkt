package com.example.pcclubkt.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "login")
data class LoginEntity(
    @PrimaryKey val Username: String, val Password: String
)