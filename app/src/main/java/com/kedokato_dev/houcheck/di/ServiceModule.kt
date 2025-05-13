package com.kedokato_dev.houcheck.di

import com.kedokato_dev.houcheck.network.api.ApiClient
import com.kedokato_dev.houcheck.network.api.AuthService
import com.kedokato_dev.houcheck.network.api.ExamScheduleService
import com.kedokato_dev.houcheck.network.api.FeedbackService
import com.kedokato_dev.houcheck.network.api.InfoStudentService
import com.kedokato_dev.houcheck.network.api.ListScoreService
import com.kedokato_dev.houcheck.network.api.ScoreService
import com.kedokato_dev.houcheck.network.api.TrainingScoreService
import com.kedokato_dev.houcheck.network.api.WeekScheduleService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {

    @Provides
    fun provideFeedbackService(): FeedbackService {
        return ApiClient.instance.create(FeedbackService::class.java)
    }

    @Provides
    fun provideScoreService(): ScoreService {
        return ApiClient.instance.create(ScoreService::class.java)
    }

    @Provides
    fun provideListScoreService(): ListScoreService {
        return ApiClient.instance.create(ListScoreService::class.java)
    }

    @Provides
    fun provideAuthService(): AuthService {
        return ApiClient.instance.create(AuthService::class.java)
    }

    @Provides
    fun provideInfoStudentService(): InfoStudentService {
        return ApiClient.instance.create(InfoStudentService::class.java)
    }

    @Provides
    fun provideWeekScheduleService(): WeekScheduleService {
        return ApiClient.instance.create(WeekScheduleService::class.java)
    }

    @Provides
    fun provideTrainingScoreService(): TrainingScoreService {
        return ApiClient.instance.create(TrainingScoreService::class.java)
    }

    @Provides
    fun provideExamScheduleService(): ExamScheduleService {
        return ApiClient.instance.create(ExamScheduleService::class.java)
    }

}

