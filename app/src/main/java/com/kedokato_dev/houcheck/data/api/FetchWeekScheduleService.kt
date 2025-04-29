package com.kedokato_dev.houcheck.data.api

import com.kedokato_dev.houcheck.data.model.ScheduleResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface FetchWeekScheduleService {
    @GET("api/week-school-schedule")
    suspend fun fetchWeekSchedule(
        @Query("sessionId") sessionId: String,
        @Query("weekValue") weekValue: String
    ): Response<ScheduleResponse>
}