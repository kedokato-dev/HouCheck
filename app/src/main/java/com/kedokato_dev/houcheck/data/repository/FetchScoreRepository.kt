package com.kedokato_dev.houcheck.data.repository

import android.util.Log
import com.kedokato_dev.houcheck.data.api.FetchScoreService
import com.kedokato_dev.houcheck.data.model.Score
import com.kedokato_dev.houcheck.data.model.ScoreResponse
import com.kedokato_dev.houcheck.database.dao.ScoreDAO
import com.kedokato_dev.houcheck.database.entity.ScoreEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FetchScoreRepository(
    private val api: FetchScoreService,
    private val dao: ScoreDAO
) {

    suspend fun fetchScore(sessionId: String): Result<ScoreResponse> = withContext(Dispatchers.IO) {
        try {
            // Check local data
            val localData = dao.getScore()
            if (localData != null) {
                val converted = Score(
                    gpa4 = localData.gpa4,
                    academicRank4 = localData.academicRank4,
                    gpa4Current = localData.gpa4Current,
                    academicRank4Current = localData.academicRank4Current,
                    accumulatedCredits = localData.accumulatedCredits,
                    gpa10Current = localData.gpa10Current,
                    academicRank10Current = localData.academicRank10Current ?: "",
                    retakeSubjects = localData.retakeSubjects,
                    repeatSubjects = localData.repeatSubjects,
                    pendingSubjects = localData.pendingSubjects
                )
                return@withContext Result.success(ScoreResponse(success = true, data = converted))
            }

            // Fetch from API
            val response = api.fetchScore(sessionId)
            if (response.isSuccessful) {
                val scoreResponse = response.body()
                scoreResponse?.let { score ->
                    // Save to database
                    val entity = ScoreEntity(
                        gpa4 = score.data.gpa4,
                        academicRank4 = score.data.academicRank4,
                        gpa4Current = score.data.gpa4Current,
                        academicRank4Current = score.data.academicRank4Current,
                        accumulatedCredits = score.data.accumulatedCredits,
                        gpa10Current = score.data.gpa10Current,
                        academicRank10Current = score.data.academicRank10Current,
                        retakeSubjects = score.data.retakeSubjects,
                        repeatSubjects = score.data.repeatSubjects,
                        pendingSubjects = score.data.pendingSubjects
                    )
                    dao.insertScore(entity)
                    return@withContext Result.success(score)
                } ?: return@withContext Result.failure(Exception("Score data is null"))
            } else {
                return@withContext Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        }
    }

    suspend fun refreshData(sessionId: String) = withContext(Dispatchers.IO) {
        try {
            val response = api.fetchScore(sessionId)
            if (response.isSuccessful) {
                dao.deleteScore()
                val scoreResponse = response.body()
                scoreResponse?.let { score ->

                    val entity = ScoreEntity(
                        gpa4 = score.data.gpa4,
                        academicRank4 = score.data.academicRank4,
                        gpa4Current = score.data.gpa4Current,
                        academicRank4Current = score.data.academicRank4Current,
                        accumulatedCredits = score.data.accumulatedCredits,
                        gpa10Current = score.data.gpa10Current,
                        academicRank10Current = score.data.academicRank10Current,
                        retakeSubjects = score.data.retakeSubjects,
                        repeatSubjects = score.data.repeatSubjects,
                        pendingSubjects = score.data.pendingSubjects
                    )
                    dao.insertScore(entity)
                }
                return@withContext Result.success(scoreResponse)
            } else {
                return@withContext Result.failure(Exception("No course results found"))
            }
        } catch (e: Exception) {
            Log.e("FetchListScoreRepository", "Error fetching or processing data: ${e.message}")
            return@withContext Result.failure(e)
        }
    }
}