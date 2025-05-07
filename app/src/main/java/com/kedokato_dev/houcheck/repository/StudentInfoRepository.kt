package com.kedokato_dev.houcheck.repository

import com.kedokato_dev.houcheck.network.api.InfoStudentService
import com.kedokato_dev.houcheck.network.model.Student
import com.kedokato_dev.houcheck.network.model.toStudent
import com.kedokato_dev.houcheck.local.dao.StudentDAO
import com.kedokato_dev.houcheck.local.entity.toEntity

class StudentInfoRepository(
    private val api: InfoStudentService,
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

