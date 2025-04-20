package com.kedokato_dev.houcheck.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.kedokato_dev.houcheck.database.entity.StudentEntity

@Dao
interface StudentDAO {
    @Query("SELECT * FROM profile WHERE id = :id")
    suspend fun getStudentById(id: Int): StudentEntity?
}