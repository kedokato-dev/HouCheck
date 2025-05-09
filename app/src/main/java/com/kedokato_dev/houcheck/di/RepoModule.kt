package com.kedokato_dev.houcheck.di

import android.content.Context
import android.content.SharedPreferences
import com.kedokato_dev.houcheck.local.dao.CourseResultDAO
import com.kedokato_dev.houcheck.local.dao.FeedbackDAO
import com.kedokato_dev.houcheck.local.dao.ScoreDAO
import com.kedokato_dev.houcheck.network.api.AuthService
import com.kedokato_dev.houcheck.network.api.FeedbackService
import com.kedokato_dev.houcheck.network.api.ListScoreService
import com.kedokato_dev.houcheck.network.api.ScoreService
import com.kedokato_dev.houcheck.repository.AuthRepository
import com.kedokato_dev.houcheck.repository.FeedBackRepository
import com.kedokato_dev.houcheck.repository.ListScoreRepository
import com.kedokato_dev.houcheck.repository.ScoreRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RepoModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    }

    @Provides
    fun provideFeedBackRepo(api: FeedbackService, dao: FeedbackDAO): FeedBackRepository {
        return FeedBackRepository(api, dao)
    }

    @Provides
    fun provideScoreRepo(api: ScoreService, dao: ScoreDAO): ScoreRepository {
        return ScoreRepository(api, dao)
    }

    @Provides
    fun provideAuthRepo(sharePre: SharedPreferences): AuthRepository {
        return AuthRepository(sharePre)
    }

    @Provides
    fun provideListScoreRepo(api: ListScoreService, dao: CourseResultDAO): ListScoreRepository {
        return ListScoreRepository(api, dao)
    }
}