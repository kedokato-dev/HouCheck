package com.kedokato_dev.houcheck.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "course_result")
data class CourseResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val semester: String,
    val academicYear: String,
    val courseCode: String,
    val courseName: String,
    val credits: Int,
    val score10: Double?,       // nullable vì có thể null
    val score4: Double?,        // nullable vì có thể null
    val letterGrade: String,
    val notCounted: Boolean,
    val note: String,
    val detailLink: String
)