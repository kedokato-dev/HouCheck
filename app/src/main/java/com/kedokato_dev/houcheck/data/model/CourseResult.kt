package com.kedokato_dev.houcheck.data.model

data class CourseResult(
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


data class CourseResultResponse(
    val success: Boolean,
    val data: Data
)

data class Data(
    val scores: List<CourseResult>
)

