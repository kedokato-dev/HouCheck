package com.kedokato_dev.houcheck.data.model

data class Session(
    val success: Boolean,
    val sessionId: String
)

data class SessionResponse(
    val success: Boolean,
    val sessionId: String
)