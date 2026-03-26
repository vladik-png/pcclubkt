package com.example.pcclubkt

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Admin::class],
    version = 1, exportSchema = false)
abstract class AppDatabase :
        RoomDatabase(){
    abstract fun AdminDAO(): AdminDAO
}