package com.kedokato_dev.houcheck.di

import com.kedokato_dev.houcheck.local.dao.FeedbackDAO
import com.kedokato_dev.houcheck.network.api.FeedbackService
import com.kedokato_dev.houcheck.repository.FeedBackRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object RepoModule {

    @Provides
    fun provideFeedBackRepo(api: FeedbackService, dao: FeedbackDAO): FeedBackRepository {
        return FeedBackRepository(api, dao)
    }
}