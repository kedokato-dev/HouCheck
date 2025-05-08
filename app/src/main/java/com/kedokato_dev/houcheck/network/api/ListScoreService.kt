package com.kedokato_dev.houcheck.network.api

import com.kedokato_dev.houcheck.network.model.CourseResultResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ListScoreService {
    @GET("api/list-score")
    suspend fun fetchListScore(@Query("sessionId") sessionId: String): CourseResultResponse

}