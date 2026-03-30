package com.example.pcclubkt

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "computers")
data class PcEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val isOccupied: Boolean = false
)