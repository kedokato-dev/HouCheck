package com.kedokato_dev.houcheck.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.kedokato_dev.houcheck.database.entity.WeekScheduleEntity

@Dao
interface WeekScheduleDAO {

    @Query("SELECT * FROM week_schedule")
    suspend fun getAllWeekSchedules(): WeekScheduleEntity

    @Insert
    suspend fun insertWeekSchedule(weekSchedule: WeekScheduleEntity)

    @Query("Delete FROM week_schedule")
    suspend fun deleteWeekSchedule()
}