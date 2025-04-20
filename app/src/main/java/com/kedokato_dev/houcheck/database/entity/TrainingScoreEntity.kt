package com.kedokato_dev.houcheck.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "training_score")
data class TrainingScoreEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val semester: String,
    val academicYear: String,
    val totalScore: String,
    val rank: String
)
