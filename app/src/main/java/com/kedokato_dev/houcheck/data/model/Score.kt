package com.kedokato_dev.houcheck.data.model

data class Score (
    val gpa4: String,
    val academicRank4: String,
    val gpa4Current: String,
    val academicRank4Current: String,
    val accumulatedCredits: String,
    val gpa10Current: String,
    val academicRank10Current: String,
    val retakeSubjects: String,
    val repeatSubjects: String,
    val pendingSubjects: String
)

data class ScoreResponse(
    val success: Boolean,
    val data: Score
)