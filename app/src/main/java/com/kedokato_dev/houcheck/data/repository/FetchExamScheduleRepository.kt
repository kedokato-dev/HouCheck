package com.kedokato_dev.houcheck.data.repository

import com.kedokato_dev.houcheck.data.api.ApiClient
import com.kedokato_dev.houcheck.data.api.FetchExamScheduleService
import com.kedokato_dev.houcheck.data.model.ExamSchedule

class FetchExamScheduleRepository {
    private val fetchExamScheduleService: FetchExamScheduleService by lazy {
        ApiClient.instance.create(FetchExamScheduleService::class.java)
    }

    suspend fun fetchExamSchedule(sessionId: String): List<ExamSchedule> {
        val response = fetchExamScheduleService.fetchExamSchedule(sessionId)
        return if (response.isSuccessful) {
            response.body()?.data?.examSchedules ?: throw Exception("No exam schedule data available")
        } else {
            throw Exception("Error fetching exam schedule: ${response.message()}")
        }
    }
}