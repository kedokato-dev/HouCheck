package com.kedokato_dev.houcheck.local.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kedokato_dev.houcheck.local.entity.AccountEntity
import com.kedokato_dev.houcheck.local.entity.CourseResultEntity
import com.kedokato_dev.houcheck.local.entity.ExamScheduleEntity
import com.kedokato_dev.houcheck.local.entity.FeedbackEntity
import com.kedokato_dev.houcheck.local.entity.ScoreEntity
import com.kedokato_dev.houcheck.local.entity.StudentEntity
import com.kedokato_dev.houcheck.local.entity.TrainingScoreEntity
import com.kedokato_dev.houcheck.local.entity.WeekScheduleEntity

private const val DATABASE_NAME = "houCheck_database"
private const val DATABASE_VERSION = 2

@Database(
    entities = [StudentEntity::class, TrainingScoreEntity::class, ScoreEntity::class, CourseResultEntity::class,
               ExamScheduleEntity::class, AccountEntity::class, FeedbackEntity::class],
    version = DATABASE_VERSION,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun studentDAO(): StudentDAO
    abstract fun trainingScoreDAO(): TrainingScoreDAO
    abstract fun scoreDAO(): ScoreDAO
    abstract fun courseResultDAO(): CourseResultDAO
    abstract fun examScheduleDAO(): ExamScheduleDAO
    abstract fun accountDAO(): AccountDAO
    abstract fun feedbackDAO(): FeedbackDAO
//    abstract fun weekScheduleDAO(): WeekScheduleDAO

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