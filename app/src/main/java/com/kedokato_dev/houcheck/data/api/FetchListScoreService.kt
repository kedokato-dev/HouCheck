package com.kedokato_dev.houcheck.data.api

import com.kedokato_dev.houcheck.data.model.CourseResultResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface FetchListScoreService {
    @GET("api/list-score")
    suspend fun fetchListScore(@Query("sessionId") sessionId: String): CourseResultResponse

}