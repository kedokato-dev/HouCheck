package com.kedokato_dev.houcheck.network.model

import com.google.gson.annotations.SerializedName


data class Feedback(
    @SerializedName("_id")
    val id: String = "",
    val name: String,
    val email: String,
    val message: String,
    val createdAt: String
)

data class GetFeedBackByEmail(
    val success: Boolean,
    val data: List<Feedback>
)

data class FeedbackResponse(
    val success: Boolean,
    val message: String,
    @SerializedName("_id")
    val id: String= ""
)

data class FeedbackRequest(
    val name: String,
    val email: String,
    val message: String,
    val createdAt: String
)

data class UpdateFeedbackRequest(
    val id: String,
    val newMessage: String
)

data class DeleteFeedbackResponse(
    val success: Boolean,
    val message: String
)


