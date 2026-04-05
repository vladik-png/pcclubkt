package com.example.pcclubkt.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerDao {
    @Query("SELECT * FROM Customer ORDER BY FullName ASC")
    fun getAllCustomers(): Flow<List<CustomerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun registerCustomer(customer: CustomerEntity)

    @Query("UPDATE Customer SET Balance = Balance + :amount WHERE CustomerID = :id")
    suspend fun topUpBalance(id: Int, amount: Int)
}