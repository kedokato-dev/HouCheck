package com.kedokato_dev.houcheck.di

import android.content.Context
import com.kedokato_dev.houcheck.local.dao.AccountDAO
import com.kedokato_dev.houcheck.local.dao.AppDatabase
import com.kedokato_dev.houcheck.local.dao.CourseResultDAO
import com.kedokato_dev.houcheck.local.dao.ExamScheduleDAO
import com.kedokato_dev.houcheck.local.dao.FeedbackDAO
import com.kedokato_dev.houcheck.local.dao.ScoreDAO
import com.kedokato_dev.houcheck.local.dao.StudentDAO
import com.kedokato_dev.houcheck.local.dao.TrainingScoreDAO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {


    @Provides
    fun provideAccountDao(database: AppDatabase): AccountDAO {
        return database.accountDAO()
    }

    @Provides
    fun provideCourseResultDao(database: AppDatabase): CourseResultDAO {
        return database.courseResultDAO()
    }

    @Provides
    fun provideExamScheduleDAO(database: AppDatabase): ExamScheduleDAO{
        return database.examScheduleDAO()
    }

    @Provides
    fun provideScoreDAO(database: AppDatabase): ScoreDAO{
        return database.scoreDAO()
    }

    @Provides
    fun provideTrainingScoreDAO(database: AppDatabase): TrainingScoreDAO {
        return database.trainingScoreDAO()
    }

    @Provides
    fun provideStudentDAO(database: AppDatabase): StudentDAO {
        return database.studentDAO()
    }

    @Provides
    fun provideFeedbackDAO(database: AppDatabase): FeedbackDAO {
        return database.feedbackDAO()
    }


    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.invoke(context)
    }
}