package com.kedokato_dev.houcheck.di

import com.kedokato_dev.houcheck.network.api.ApiClient
import com.kedokato_dev.houcheck.network.api.FeedbackService
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

}

