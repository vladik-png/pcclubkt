package com.example.pcclubkt.database

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StatisticsDao {
    @Query("SELECT * FROM Genderdistribution")
    fun getGenderdistribution(): Flow<List<GenderdistributionEntity>>

    @Query("SELECT * FROM Agedistribution")
    fun getAgedistribution(): Flow<List<AgedistributionEntity>>

    @Query("SELECT * FROM Monthlyhours")
    fun getMonthlyhours(): Flow<List<MonthlyhoursEntity>>

    @Query("SELECT * FROM Monthlyearnings")
    fun getMonthlyearnings(): Flow<List<MonthlyearningsEntity>>

    @Query("SELECT * FROM Monthlyexpenses")
    fun getMonthlyexpenses(): Flow<List<MonthlyexpensesEntity>>

    @Query("SELECT * FROM Monthlysessions")
    fun getMonthlysessions(): Flow<List<MonthlysessionsEntity>>

    @Query("UPDATE Genderdistribution SET Visits = Visits + 1 WHERE Gender = :gender")
    suspend fun incrementGenderVisits(gender: String)

    @Query("UPDATE Agedistribution SET Visits = Visits + 1 WHERE Agegroup = :ageGroup")
    suspend fun incrementAgeGroupVisits(ageGroup: String)

    @Query("UPDATE Monthlyearnings SET Earnings = Earnings + :amount WHERE Month = :month")
    suspend fun addMonthlyEarnings(month: String, amount: Int)

    @Query("UPDATE Monthlyexpenses SET Expenses = Expenses + :amount WHERE Month = :month")
    suspend fun addMonthlyExpenses(month: String, amount: Int)

    @Query("UPDATE Monthlysessions SET Sessions = Sessions + 1 WHERE Month = :month")
    suspend fun incrementMonthlySessions(month: String)

    @Query("UPDATE Monthlyhours SET Hours = Hours + :hours WHERE Month = :month")
    suspend fun addMonthlyHours(month: String, hours: Int)

    @Query("UPDATE Gamepopularity SET Popularitypercentage = Popularitypercentage + 1.0 WHERE GameID = :gameId")
    suspend fun increaseGamePopularity(gameId: Int)
}