package com.example.pcclubkt.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {
    @Query("SELECT * FROM Customer")
    fun getAllCustomers(): Flow<List<CustomerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun registerCustomer(customer: CustomerEntity)


    @Query("UPDATE Customer SET Balance = Balance + :amount WHERE CustomerID = :clientId")
    suspend fun addBalance(clientId: Int, amount: Int)

    @Query("UPDATE Customer SET LastVisit = :time WHERE CustomerID = :clientId")
    suspend fun updateLastVisit(clientId: Int, time: String)

    @Query("UPDATE Customer SET Balance = Balance - :amount WHERE CustomerID = :clientId")
    suspend fun subtractBalance(clientId: Int, amount: Int)

    @Update
    suspend fun updateCustomer(customer: CustomerEntity)
}