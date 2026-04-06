package com.example.pcclubkt.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Genderdistribution")
data class GenderdistributionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val Gender: String?,
    val Visits: Int,
    val CustomerID: Int
)

@Entity(tableName = "Agedistribution")
data class AgedistributionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val Agegroup: String?,
    val Visits: Int,
    val Customerid: Int
)

@Entity(tableName = "Monthlyhours")
data class MonthlyhoursEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val Month: String?,
    val Hours: Int,
    val VisitID: Int
)

@Entity(tableName = "Averagemonthlyhours")
data class AveragemonthlyhoursEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val Month: String?,
    val Averagehours: Double,
    val Visitid: Int
)

@Entity(tableName = "Monthlyearnings")
data class MonthlyearningsEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val Month: String?,
    val Earnings: Int,
    val PaymentID: Int
)

@Entity(tableName = "Monthlyexpenses")
data class MonthlyexpensesEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val Month: String?,
    val Expenses: Int,
    val KasaID: Int
)

@Entity(tableName = "Monthlysessions")
data class MonthlysessionsEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val Month: String?,
    val Sessions: Int,
    val Visitid: Int
)

@Entity(tableName = "Gamepopularity")
data class GamepopularityEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val GameID: Int,
    val Popularitypercentage: Double
)

@Entity(tableName = "Games")
data class GamesEntity(
    @PrimaryKey(autoGenerate = true) val GameID: Int = 0,
    val ComputerID: Int?,
    val GameName: String,
    val Description: String?,
    val New: Int,
    val Top: Int
)

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

@Entity(tableName = "Tovar")
data class TovarEntity(
    @PrimaryKey(autoGenerate = true) val TovarID: Int = 0,
    val SuppliersID: Int,
    val TovarName: String,
    val Price: Double,
    val DeliveryDate: String,
    val DeliveryTime: Int,
    val DeliveryStatus: Int
)

@Entity(tableName = "Suppliers")
data class SuppliersEntity(
    @PrimaryKey(autoGenerate = true) val SuppliersID: Int = 0,
    val SupplierName: String?,
    val Address: String?,
    val PhoneNumber: String?,
    val DeliveryTime: Int?
)