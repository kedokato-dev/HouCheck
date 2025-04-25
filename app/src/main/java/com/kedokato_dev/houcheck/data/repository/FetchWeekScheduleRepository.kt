package com.kedokato_dev.houcheck.data.repository

import android.util.Log
import com.kedokato_dev.houcheck.data.api.FetchWeekScheduleService
import com.kedokato_dev.houcheck.data.model.ScheduleResponse

class FetchWeekScheduleRepository(
    private val api : FetchWeekScheduleService,
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