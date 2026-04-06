package com.example.pcclubkt.database

import androidx.room.Embedded
import androidx.room.Relation

data class ComputerWithDetails(
    @Embedded val computer: ComputerEntity,

    @Relation(
        parentColumn = "SpecsID", entityColumn = "SpecsID"
    ) val specs: SpecsEntity,

    @Relation(
        parentColumn = "CurrentClientID", entityColumn = "CustomerID"
    ) val client: CustomerEntity?
)