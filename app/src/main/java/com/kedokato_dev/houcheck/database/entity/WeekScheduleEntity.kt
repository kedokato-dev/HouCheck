package com.kedokato_dev.houcheck.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kedokato_dev.houcheck.data.model.DaySchedule


@Entity(tableName = "week_schedule")
data class WeekScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val weekValue: String,
    val weekDays: List<String>,
    val byDays: Map<String, DaySchedule>
)


