package com.kedokato_dev.houcheck.network.api

import com.kedokato_dev.houcheck.network.model.TrainingScoreResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TrainingScoreService {
    @GET("/api/training-score")
    suspend fun fetchTrainingScore(@Query("sessionId") sessionId: String): Response<TrainingScoreResponse>
}