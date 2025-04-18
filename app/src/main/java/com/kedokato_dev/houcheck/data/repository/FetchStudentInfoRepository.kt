package com.kedokato_dev.houcheck.data.repository

import com.kedokato_dev.houcheck.data.api.ApiClient
import com.kedokato_dev.houcheck.data.api.FetchInfoStudentService
import com.kedokato_dev.houcheck.data.model.StudentResponse

class FetchStudentInfoRepository {
    private val fetchInfoStudentService: FetchInfoStudentService by lazy {
        ApiClient.instance.create(FetchInfoStudentService::class.java)
    }

    suspend fun fetchInfoStudent(sessionId: String): Result<StudentResponse> {
        return try {
            val response = fetchInfoStudentService.fetchInfoStudent(sessionId)
            if (response.isSuccessful) {
                val student = response.body()
                if (student != null) {
                    Result.success(student)
                } else {
                    Result.failure(Exception("Failed to parse student info"))
                }
            } else {
                Result.failure(Exception("Failed to fetch student info: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}