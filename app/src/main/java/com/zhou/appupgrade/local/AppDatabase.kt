package com.zhou.appupgrade.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [AppItem::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): AppItemDao
}

