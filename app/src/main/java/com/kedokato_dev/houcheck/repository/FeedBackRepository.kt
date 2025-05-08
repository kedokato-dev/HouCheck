package com.kedokato_dev.houcheck.repository

import com.kedokato_dev.houcheck.local.dao.FeedbackDAO
import com.kedokato_dev.houcheck.local.entity.FeedbackEntity
import com.kedokato_dev.houcheck.network.api.ApiClient
import com.kedokato_dev.houcheck.network.api.FeedbackService
import com.kedokato_dev.houcheck.network.model.Feedback
import com.kedokato_dev.houcheck.network.model.FeedbackRequest
import com.kedokato_dev.houcheck.network.model.UpdateFeedbackRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FeedBackRepository(
    private val api: FeedbackService = ApiClient.instance.create(FeedbackService::class.java),
    private val feedBackDAO: FeedbackDAO
) {

    suspend fun getFeedbackByEmail(email: String): List<Feedback> = withContext(Dispatchers.IO) {
        val response = api.fetchFeedback(email)
        if (response.isSuccessful) {
            val feedback = response.body()
            if (feedback != null) {
                return@withContext feedback.data
            } else {
                throw Exception("No feedback data available")
            }
        } else {
            throw Exception("Failed to fetch feedback: ${response.errorBody()}")
        }
    }


    suspend fun postFeedback(
        name: String,
        email: String,
        message: String,
        createdAt: String = ""
    ): Triple<Boolean, String, String> = withContext(Dispatchers.IO) {
        val response = api.sendFeedback(
            FeedbackRequest(
                name = name,
                email = email,
                message = message,
                createdAt = createdAt
            )
        )
        if (response.isSuccessful) {
            val feedbackResponse = response.body()
            if (feedbackResponse != null) {
                return@withContext Triple(
                    feedbackResponse.success,
                    feedbackResponse.message,
                    feedbackResponse.id
                )
            } else {
                throw Exception("No feedback data available")
            }
        } else {
            throw Exception("Failed to post feedback: ${response.errorBody()}")
        }
    }

    suspend fun updateFeedback(
        id: String,
        newMessage: String
    ): Boolean = withContext(Dispatchers.IO) {
        val response = api.updateFeedback(
            UpdateFeedbackRequest(
                id = id,
                newMessage = newMessage
            )
        )
        if (response.isSuccessful) {
            val feedbackResponse = response.body()
            if (feedbackResponse != null) {
                return@withContext feedbackResponse.success
            } else {
                throw Exception("No feedback data available")
            }
        } else {
            throw Exception("Failed to update feedback: ${response.errorBody()}")
        }
    }



    suspend fun deleteFeedback(
        id: String
    ): Boolean = withContext(Dispatchers.IO) {
        val response = api.deleteFeedback(id)
        if (response.isSuccessful) {
            val feedbackResponse = response.body()
            if (feedbackResponse != null) {
                return@withContext feedbackResponse.success
            } else {
                throw Exception("No feedback data available")
            }
        } else {
            throw Exception("Failed to delete feedback: ${response.errorBody()}")
        }
    }


    fun getFeedbackByEmailLocal(email: String) = feedBackDAO.getFeedbackByEmail(email)

    suspend fun insertFeedbackLocal(feedback: List<FeedbackEntity>) = withContext(Dispatchers.IO) {
        try {
            feedBackDAO.insertFeedback(feedback)
        } catch (e: Exception) {
            throw Exception("Failed to insert feedback: ${e.message}")
        }
    }


    suspend fun updateFeedbackLocal(feedback: FeedbackEntity) = withContext(Dispatchers.IO) {
        try {
            val result = feedBackDAO.updateFeedback(feedback)
            if (result > 0)
                return@withContext true
            else {
                return@withContext false
            }
        } catch (e: Exception) {
            return@withContext false
        }
    }

    suspend fun deleteFeedbackByIdLocal(id: String) = withContext(Dispatchers.IO) {
        try {
            feedBackDAO.deleteFeedbackById(id)
        } catch (e: Exception) {
            throw Exception("Failed to delete all feedback: ${e.message}")
        }
    }

}