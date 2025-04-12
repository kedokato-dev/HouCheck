package com.kedokato_dev.houcheck.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kedokato_dev.houcheck.local.DAO.ProfileDAO
import com.kedokato_dev.houcheck.model.ProfileEntity

@Database(
    entities = [ProfileEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun profileDAO (): ProfileDAO
}