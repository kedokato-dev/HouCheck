package com.kedokato_dev.houcheck.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kedokato_dev.houcheck.local.entity.ScoreEntity

@Dao
interface ScoreDAO {

    @Insert
    suspend fun insertScore(score: ScoreEntity)

    @Query("DELETE FROM score")
    suspend fun deleteScore()

    @Query("SELECT * FROM score")
    suspend fun getScore(): ScoreEntity?
}