package com.kedokato_dev.houcheck.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kedokato_dev.houcheck.database.entity.ExamScheduleEntity
import com.kedokato_dev.houcheck.database.entity.TrainingScoreEntity

@Dao
interface ExamScheduleDAO {
    @Insert
    suspend fun insertExamSchedule(examSchedule: ExamScheduleEntity)

    @Query("SELECT * FROM exam_schedule")
    suspend fun getExamSchedules(): List<ExamScheduleEntity>

    @Query("DELETE FROM exam_schedule")
    suspend fun deleteAllExamSchedules()
}