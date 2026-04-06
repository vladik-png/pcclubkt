package com.example.pcclubkt.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "VisitLog", foreignKeys = [ForeignKey(
        entity = CustomerEntity::class,
        parentColumns = ["CustomerID"],
        childColumns = ["CustomerID"]
    ), ForeignKey(
        entity = ComputerEntity::class,
        parentColumns = ["ComputerID"],
        childColumns = ["ComputerID"]
    )]
)
data class VisitLogEntity(
    @PrimaryKey(autoGenerate = true) val VisitID: Int? = null,
    val CustomerID: Int,
    val ComputerID: Int,
    val StartTime: String,
    val EndTime: String
)