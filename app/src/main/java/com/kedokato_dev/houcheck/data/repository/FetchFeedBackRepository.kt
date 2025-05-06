package com.kedokato_dev.houcheck.data.repository

import com.kedokato_dev.houcheck.data.api.ApiClient
import com.kedokato_dev.houcheck.data.api.FetchFeedbackService
import com.kedokato_dev.houcheck.data.model.Feedback
import com.kedokato_dev.houcheck.data.model.FeedbackRequest
import com.kedokato_dev.houcheck.data.model.UpdateFeedbackRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FetchFeedBackRepository(
    private val api: FetchFeedbackService = ApiClient.instance.create(FetchFeedbackService::class.java)
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
                return@withContext Triple(feedbackResponse.success, feedbackResponse.message, feedbackResponse.id)
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
    ): Boolean  = withContext(Dispatchers.IO){
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

}