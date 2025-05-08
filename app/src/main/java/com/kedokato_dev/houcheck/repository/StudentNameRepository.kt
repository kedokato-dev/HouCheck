package com.kedokato_dev.houcheck.repository

import com.kedokato_dev.houcheck.network.api.ApiClient
import com.kedokato_dev.houcheck.network.api.FetchNameService
import com.kedokato_dev.houcheck.network.model.StudentNameResponse

class StudentNameRepository {
    private val fetNameService: FetchNameService by lazy {
        ApiClient.instance.create(FetchNameService::class.java)
    }

    suspend fun fetchStudentName(sessionId: String): Result<StudentNameResponse> {
        return try {
            val response = fetNameService.fetchName(sessionId)
            if (response.isSuccessful) {
                val studentName = response.body()
                if (studentName != null) {
                    Result.success(studentName)
                } else {
                    Result.failure(Exception("Failed to parse student name"))
                }
            } else {
                Result.failure(Exception("Failed to fetch student name: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}