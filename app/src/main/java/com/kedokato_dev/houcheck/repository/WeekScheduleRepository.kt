package com.kedokato_dev.houcheck.repository

import android.util.Log
import com.kedokato_dev.houcheck.network.api.WeekScheduleService
import com.kedokato_dev.houcheck.network.model.ScheduleResponse

class WeekScheduleRepository(
    private val api : WeekScheduleService,
) {

    suspend fun fetchWeekSchedule(sessionId: String, weekValue: String): Result<ScheduleResponse> {
        return try {
            val response = api.fetchWeekSchedule(sessionId, weekValue)
            if (response.isSuccessful) {
                val weekSchedule = response.body()
                Log.d("FetchWeekScheduleRepository", "Week schedule: $weekSchedule")
                weekSchedule?.let { schedule ->
                    return Result.success(schedule)
                } ?: return Result.failure(Exception("Week schedule data is null"))
            } else {
                return Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}