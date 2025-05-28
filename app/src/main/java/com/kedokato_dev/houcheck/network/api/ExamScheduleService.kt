package com.kedokato_dev.houcheck.network.api

import com.kedokato_dev.houcheck.network.model.ExamScheduleResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ExamScheduleService {
    @GET("/api/exam-schedule")
    suspend fun fetchExamSchedule(@Query("sessionId") sessionId: String): Response<ExamScheduleResponse>
}


