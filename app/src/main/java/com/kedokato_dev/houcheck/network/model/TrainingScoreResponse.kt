package com.kedokato_dev.houcheck.network.model

import com.google.gson.annotations.SerializedName

data class TrainingScoreResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: List<TrainingScore>
)

data class TrainingScore(
    @SerializedName("semester") val semester: String,
    @SerializedName("academicYear") val academicYear: String,
    @SerializedName("totalScore") val totalScore: String,
    @SerializedName("rank") val rank: String
)