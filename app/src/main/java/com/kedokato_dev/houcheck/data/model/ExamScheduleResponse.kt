package com.kedokato_dev.houcheck.data.model


import com.google.gson.annotations.SerializedName

data class ExamScheduleResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: ExamScheduleData
)

data class ExamScheduleData(
    @SerializedName("examSchedules") val examSchedules: List<ExamSchedule>
)

data class ExamSchedule(
    @SerializedName("semester") val semester: String,
    @SerializedName("subject") val subject: String,
    @SerializedName("testTime") val testTime: String,
    @SerializedName("testPhase") val testPhase: String,
    @SerializedName("date") val date: String,
    @SerializedName("session") val session: String,
    @SerializedName("time") val time: String,
    @SerializedName("room") val room: String,
    @SerializedName("studentNumber") val studentNumber: String,
    @SerializedName("examType") val examType: String,
    @SerializedName("note") val note: String?
)