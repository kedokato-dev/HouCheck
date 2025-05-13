package com.kedokato_dev.houcheck.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kedokato_dev.houcheck.network.model.DaySchedule


@Entity(tableName = "week_schedule")
data class WeekScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val weekValue: String = "",
    val weekDays: List<String> = emptyList(),
    val byDays: Map<String, DaySchedule> = emptyMap(),
)


