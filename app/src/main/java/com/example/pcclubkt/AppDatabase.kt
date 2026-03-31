package com.example.pcclubkt

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PcEntity::class, AdminEntity::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    //abstract fun adminDao(): AdminDao
    abstract fun pcDao(): PcDao
    abstract fun adminDao() : AdminDao
}