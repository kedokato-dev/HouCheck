package com.kedokato_dev.houcheck.data.repository

import com.kedokato_dev.houcheck.data.api.ApiClient
import com.kedokato_dev.houcheck.data.api.FetchExamScheduleService
import com.kedokato_dev.houcheck.data.model.ExamSchedule
import com.kedokato_dev.houcheck.database.dao.ExamScheduleDAO
import com.kedokato_dev.houcheck.database.entity.ExamScheduleEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FetchExamScheduleRepository(
    private val api: FetchExamScheduleService = ApiClient.instance.create(FetchExamScheduleService::class.java),
    private val dao: ExamScheduleDAO? = null
) {

    suspend fun fetchExamSchedule(sessionId: String): List<ExamSchedule> = withContext(Dispatchers.IO) {
        // First check if we have cached data
        if (dao != null) {
            val localData = dao.getExamSchedules()
            if (localData.isNotEmpty()) {
                return@withContext localData.map { entity ->
                    ExamSchedule(
                        semester = entity.semester,
                        subject = entity.subject,
                        testTime = entity.testTime,
                        testPhase = entity.testPhase,
                        date = entity.date,
                        session = entity.session,
                        time = entity.time,
                        room = entity.room,
                        studentNumber = entity.studentNumber,
                        examType = entity.examType,
                        note = entity.note
                    )
                }
            }
        }

        // If no cached data or no DAO provided, fetch from API
        val response = api.fetchExamSchedule(sessionId)
        if (response.isSuccessful) {
            val schedules = response.body()?.data?.examSchedules
                ?: throw Exception("No exam schedule data available")

            // Cache the data if DAO is available
            if (dao != null) {
                schedules.forEach { schedule ->
                    dao.insertExamSchedule(
                        ExamScheduleEntity(
                            semester = schedule.semester,
                            subject = schedule.subject,
                            testTime = schedule.testTime,
                            testPhase = schedule.testPhase,
                            date = schedule.date,
                            session = schedule.session,
                            time = schedule.time,
                            room = schedule.room,
                            studentNumber = schedule.studentNumber,
                            examType = schedule.examType,
                            note = schedule.note
                        )
                    )
                }
            }

            return@withContext schedules
        } else {
            throw Exception("Error fetching exam schedule: ${response.message()}")
        }
    }

    suspend fun refreshExamSchedule(sessionId: String): List<ExamSchedule> = withContext(Dispatchers.IO) {
        // Clear cache if DAO is available
        dao?.deleteAllExamSchedules()

        // Fetch fresh data from API
        val response = api.fetchExamSchedule(sessionId)
        if (response.isSuccessful) {
            val schedules = response.body()?.data?.examSchedules
                ?: throw Exception("No exam schedule data available")

            // Cache the refreshed data if DAO is available
            if (dao != null) {
                schedules.forEach { schedule ->
                    dao.insertExamSchedule(
                        ExamScheduleEntity(
                            semester = schedule.semester,
                            subject = schedule.subject,
                            testTime = schedule.testTime,
                            testPhase = schedule.testPhase,
                            date = schedule.date,
                            session = schedule.session,
                            time = schedule.time,
                            room = schedule.room,
                            studentNumber = schedule.studentNumber,
                            examType = schedule.examType,
                            note = schedule.note
                        )
                    )
                }
            }

            return@withContext schedules
        } else {
            throw Exception("Error refreshing exam schedule: ${response.message()}")
        }
    }
}