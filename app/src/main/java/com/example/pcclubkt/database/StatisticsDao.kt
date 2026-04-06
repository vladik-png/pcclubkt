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

    @Query("SELECT * FROM Averagemonthlyhours")
    fun getAveragemonthlyhours(): Flow<List<AveragemonthlyhoursEntity>>

    @Query("SELECT * FROM Gamepopularity")
    fun getGamepopularity(): Flow<List<GamepopularityEntity>>
}