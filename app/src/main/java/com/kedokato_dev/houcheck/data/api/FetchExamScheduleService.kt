package com.kedokato_dev.houcheck.data.api

import com.kedokato_dev.houcheck.data.model.ExamScheduleResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface FetchExamScheduleService {
    @GET("/api/exam-schedule")
    suspend fun fetchExamSchedule(@Query("sessionId") sessionId: String): Response<ExamScheduleResponse>

}