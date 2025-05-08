package com.kedokato_dev.houcheck.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kedokato_dev.houcheck.local.entity.TrainingScoreEntity

@Dao
interface TrainingScoreDAO {
    @Insert
    suspend fun insertTrainingScore(trainingScore: TrainingScoreEntity)

    @Query("select * from training_score")
    suspend fun getTrainingScores(): List<TrainingScoreEntity>

    @Query("delete from training_score")
    suspend fun deleteAllTrainingScores()
}