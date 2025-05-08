package com.kedokato_dev.houcheck.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "score")
data class ScoreEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val gpa4: String,
    val academicRank4: String,
    val gpa4Current: String,
    val academicRank4Current: String,
    val accumulatedCredits: String,
    val gpa10Current: String,
    val academicRank10Current: String?,
    val retakeSubjects: String,
    val repeatSubjects: String,
    val pendingSubjects: String
)
