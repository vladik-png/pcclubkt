package com.example.pcclubkt.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Specs")
data class SpecsEntity(
    @PrimaryKey(autoGenerate = true)
    val SpecsID: Int? = null,
    val ComputerID: Int?,
    val Cpu: String,
    val Gpu: String,
    val RAM: String,
    val Storage: String,
    val Monitor: String,
    val Mouse: String,
    val Keyboard: String,
    val OS: String
)