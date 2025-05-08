package com.kedokato_dev.houcheck.network.model

data class Session(
    val success: Boolean,
    val sessionId: String
)

data class SessionResponse(
    val success: Boolean,
    val sessionId: String
)