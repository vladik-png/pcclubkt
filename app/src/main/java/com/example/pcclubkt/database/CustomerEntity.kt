package com.example.pcclubkt.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Customer")
data class CustomerEntity(
    @PrimaryKey(autoGenerate = true)
    val CustomerID: Int? = null,
    val FullName: String,
    val Email: String,
    val HappyBirthday: String,
    val PhoneNumber: String,
    val MembershipStatus: Int = 1,
    val Balance: Int = 0,
    val Sex: String? = "male",
    val Registration: String,
    val LastVisit: String
)