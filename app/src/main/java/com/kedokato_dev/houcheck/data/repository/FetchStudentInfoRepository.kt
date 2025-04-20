package com.kedokato_dev.houcheck.data.repository

import com.kedokato_dev.houcheck.data.api.FetchInfoStudentService
import com.kedokato_dev.houcheck.data.model.Student
import com.kedokato_dev.houcheck.data.model.toStudent
import com.kedokato_dev.houcheck.database.dao.StudentDAO
import com.kedokato_dev.houcheck.database.entity.toEntity

class FetchStudentInfoRepository(
    private val api: FetchInfoStudentService,
    private val dao: StudentDAO
) {

    suspend fun getLocalStudentById(): Student? {
        return dao.getStudentById()?.toStudent()
    }

    // Gọi API và lưu Room
    suspend fun fetchAndSaveStudent(sessionId: String): Result<Student> {
        return try {
            val response = api.fetchInfoStudent(sessionId)
            if (response.isSuccessful) {
                val studentResponse = response.body()
                studentResponse?.data?.let { student ->
                    val entity = student.toEntity()
                    dao.insertStudent(entity)
                    Result.success(student)
                } ?: Result.failure(Exception("Student data is null"))
            } else {
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}

