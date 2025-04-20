package com.kedokato_dev.houcheck.database.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kedokato_dev.houcheck.database.entity.StudentEntity
import com.kedokato_dev.houcheck.database.entity.TrainingScoreEntity

private const val DATABASE_NAME = "houCheck_database"
private const val DATABASE_VERSION = 1

@Database(
    entities = [StudentEntity::class, TrainingScoreEntity::class],
    version = DATABASE_VERSION,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun studentDAO(): StudentDAO
    abstract fun trainingScoreDAO(): TrainingScoreDAO

    companion object {
        private var instance: AppDatabase? = null

        operator fun invoke(context: Context) = instance ?: synchronized(this) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        fun buildDatabase(context: Context): AppDatabase = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}