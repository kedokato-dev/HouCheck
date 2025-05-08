package com.kedokato_dev.houcheck.network.model

data class ScheduleResponse(
    val weekValue: String,
    val weekDays: List<String>,
    val byDays: Map<String, DaySchedule>
)

data class DaySchedule(
    val fullDate: String,
    val classes: List<ClassInfo>
)

data class ClassInfo(
    val subject: String,
    val session: String,
    val classId: String,
    val teacher: String,
    val room: String,
    val type: String,
    val isSubstitute: Boolean,
    val dayOfWeek: String,
    val fullDateInfo: String,
    val timeSlot: String
)