package com.kedokato_dev.houcheck.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.kedokato_dev.houcheck.database.entity.ScoreEntity

@Dao
interface ScoreDAO {

    @Insert
    suspend fun insertScore(score: ScoreEntity)

    @Delete
    suspend fun deleteScore(score: ScoreEntity)

    @Query("SELECT * FROM score")
    suspend fun getScore(): ScoreEntity?
}