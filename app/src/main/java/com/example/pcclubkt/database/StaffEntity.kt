package com.example.pcclubkt.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Staff")
data class StaffEntity(
    @PrimaryKey(autoGenerate = true) val StaffID: Int? = null,
    val FullName: String,
    val Phone_number: String,
    val Email: String,
    val Position: String,
    val Zmina: String,
    val Salary: String
)