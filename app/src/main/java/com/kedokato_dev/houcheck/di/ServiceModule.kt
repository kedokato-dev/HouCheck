package com.kedokato_dev.houcheck.di

import com.kedokato_dev.houcheck.network.api.ApiClient
import com.kedokato_dev.houcheck.network.api.FeedbackService
import com.kedokato_dev.houcheck.network.api.ListScoreService
import com.kedokato_dev.houcheck.network.api.ScoreService
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

}

