package com.kedokato_dev.houcheck.data.repository

import com.kedokato_dev.houcheck.data.api.FetchTrainingScoreService
import com.kedokato_dev.houcheck.data.model.TrainingScore
import com.kedokato_dev.houcheck.data.model.TrainingScoreResponse
import com.kedokato_dev.houcheck.database.dao.TrainingScoreDAO
import com.kedokato_dev.houcheck.database.entity.TrainingScoreEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FetchTrainingScoreRepository(
    private val api: FetchTrainingScoreService,
    private val dao: TrainingScoreDAO
) {

    suspend fun fetchTrainingScore(sessionId: String): Result<TrainingScoreResponse> = withContext(Dispatchers.IO) {
        try {
            val localData = dao.getTrainingScores()
            if (localData.isNotEmpty()) {
                val converted = localData.map {
                    TrainingScore(
                        semester = it.semester,
                        academicYear = it.academicYear,
                        totalScore = it.totalScore,
                        rank = it.rank
                    )
                }
                return@withContext Result.success(TrainingScoreResponse(success = true, data = converted))
            }

            val response = api.fetchTrainingScore(sessionId)
            if (response.isSuccessful) {
                val trainingScores = response.body()
                trainingScores?.let { scores ->
                    // Lưu vào DB
                    val entities = scores.data.map {
                        TrainingScoreEntity(
                            semester = it.semester,
                            academicYear = it.academicYear,
                            totalScore = it.totalScore,
                            rank = it.rank
                        )
                    }
                    for (entity in entities) {
                        dao.insertTrainingScore(entity)
                    }
                    return@withContext Result.success(scores)
                } ?: return@withContext Result.failure(Exception("Training score data is null"))
            } else {
                return@withContext Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }
}
