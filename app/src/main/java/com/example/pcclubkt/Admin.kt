package com.example.pcclubkt

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("admins")
data class Admin (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val login: String,
    val pass: String
)
