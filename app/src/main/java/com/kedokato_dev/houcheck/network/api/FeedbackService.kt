package com.kedokato_dev.houcheck.network.api

import com.kedokato_dev.houcheck.network.model.DeleteFeedbackResponse
import com.kedokato_dev.houcheck.network.model.FeedbackRequest
import com.kedokato_dev.houcheck.network.model.FeedbackResponse
import com.kedokato_dev.houcheck.network.model.GetFeedBackByEmail
import com.kedokato_dev.houcheck.network.model.UpdateFeedbackRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface FeedbackService {

    @GET("/api/feedback")
    suspend fun fetchFeedback(@Query("email") email: String): Response<GetFeedBackByEmail>

    @POST("/api/feedback")
    suspend fun sendFeedback(
      @Body request: FeedbackRequest): Response<FeedbackResponse>

    @PUT("/api/feedback")
    suspend fun updateFeedback(
        @Body request: UpdateFeedbackRequest
    ): Response<DeleteFeedbackResponse>

    @DELETE("/api/feedback")
    suspend fun deleteFeedback(
        @Query("id") id: String
    ): Response<DeleteFeedbackResponse>

}


