package com.example.pcclubkt.database
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.pcclubkt.database.LoginDao

@Database(
    entities = [
        ComputerEntity::class,
        LoginEntity::class,
        StaffEntity::class,
        SpecsEntity::class,
        CustomerEntity::class,
        VisitLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun computerDao(): ComputerDao
    abstract fun loginDao(): LoginDao
    abstract fun staffDao(): StaffDao
    abstract fun customerDao(): CustomerDao
    abstract fun visitLogDao(): VisitLogDao
}