package com.kedokato_dev.houcheck.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kedokato_dev.houcheck.local.entity.ExamScheduleEntity

@Dao
interface ExamScheduleDAO {
    @Insert
    suspend fun insertExamSchedule(examSchedule: ExamScheduleEntity)

    @Query("SELECT * FROM exam_schedule")
    suspend fun getExamSchedules(): List<ExamScheduleEntity>

    @Query("DELETE FROM exam_schedule")
    suspend fun deleteAllExamSchedules()
}