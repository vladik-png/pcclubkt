package com.example.pcclubkt.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Events")
data class EventsEntity(
    @PrimaryKey(autoGenerate = true) val EventID: Int = 0,
    val GameID: Int?,
    val EventDate: String?,
    val Prize: Int?,
    val CountPeople: Int?,
    val CustomerID: Int?,
    val Status: Int?
)