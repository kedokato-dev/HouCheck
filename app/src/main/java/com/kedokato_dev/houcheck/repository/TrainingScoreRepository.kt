package com.kedokato_dev.houcheck.repository

import com.kedokato_dev.houcheck.network.api.TrainingScoreService
import com.kedokato_dev.houcheck.network.model.TrainingScore
import com.kedokato_dev.houcheck.network.model.TrainingScoreResponse
import com.kedokato_dev.houcheck.local.dao.TrainingScoreDAO
import com.kedokato_dev.houcheck.local.entity.TrainingScoreEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TrainingScoreRepository(
    private val api: TrainingScoreService,
    private val dao: TrainingScoreDAO
) {

    suspend fun fetchTrainingScore(sessionId: String): Result<TrainingScoreResponse> =
        withContext(Dispatchers.IO) {
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
                    return@withContext Result.success(
                        TrainingScoreResponse(
                            success = true,
                            data = converted
                        )
                    )
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

    suspend fun refreshData(sessionId: String) = withContext(Dispatchers.IO) {
        try {
            // call API to fetch new data
            val response = api.fetchTrainingScore(sessionId)
            if (response.isSuccessful) {
                dao.deleteAllTrainingScores()
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
                }
            }
            else {
                return@withContext Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }

    }
}
