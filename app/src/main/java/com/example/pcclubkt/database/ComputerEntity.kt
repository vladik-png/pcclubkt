package com.example.pcclubkt.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "Computers", foreignKeys = [ForeignKey(
        entity = SpecsEntity::class, parentColumns = ["SpecsID"], childColumns = ["SpecsID"]
    ), ForeignKey(
        entity = CustomerEntity::class,
        parentColumns = ["CustomerID"],
        childColumns = ["CurrentClientID"],
    )]
)
data class ComputerEntity(
    @PrimaryKey(autoGenerate = true) val ComputerID: Int? = null,
    val SpecsID: Int,
    val Status: String? = "available",
    val CurrentClientID: Int? = null
)