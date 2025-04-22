package com.kedokato_dev.houcheck.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.kedokato_dev.houcheck.database.entity.CourseResultEntity

@Dao
interface CourseResultDAO {

    @Insert
    suspend fun insertCourseResult(courseResult: CourseResultEntity)

    @Query("SELECT * FROM course_result")
    suspend fun getAllCourseResults(): List<CourseResultEntity>

    @Query("SELECT * FROM course_result WHERE courseName = :courseName")
    suspend fun getCourseResultsByCourseName(courseName: String): List<CourseResultEntity>

    @Query("DELETE  FROM course_result")
    suspend fun deleteCourseResult()
}