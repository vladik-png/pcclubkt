package com.example.pcclubkt

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PcEntity::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    //abstract fun adminDao(): AdminDao
    abstract fun pcDao(): PcDao
}