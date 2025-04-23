package com.kedokato_dev.houcheck.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exam_schedule")
data class ExamScheduleEntity (
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val semester: String,
    val subject: String,
    val testTime: String,
    val testPhase: String,
    val date: String,
    val session: String,
    val time: String,
    val room: String,
    val studentNumber: String,
    val examType: String,
    val note: String?
)