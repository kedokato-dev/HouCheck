package com.kedokato_dev.houcheck.data.repository

import com.kedokato_dev.houcheck.data.api.ApiClient
import com.kedokato_dev.houcheck.data.api.FetchTrainingScoreService
import com.kedokato_dev.houcheck.data.model.TrainingScoreResponse

class FetchTrainingScoreRepository {
    private val fetchTrainingScoreService: FetchTrainingScoreService by lazy {
        ApiClient.instance.create(FetchTrainingScoreService::class.java)
    }

    suspend fun fetchTrainingScore(sessionId: String): Result<TrainingScoreResponse> {
        return try {
            val response = fetchTrainingScoreService.fetchTrainingScore(sessionId)
            if (response.isSuccessful) {
                val scores = response.body()
                if (scores != null) {
                    Result.success(scores)
                } else {
                    Result.failure(Exception("Failed to parse training score data"))
                }
            } else {
                Result.failure(Exception("Failed to fetch training score: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}