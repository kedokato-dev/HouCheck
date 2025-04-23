package com.kedokato_dev.houcheck.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kedokato_dev.houcheck.database.entity.StudentEntity

@Dao
interface StudentDAO {

    @Query("SELECT * FROM profile")
    suspend fun getStudentById(): StudentEntity?

    @Insert
    suspend fun insertStudent(student: StudentEntity)

    @Query("DELETE FROM profile")
    suspend fun deleteAllStudents()
}