package com.kedokato_dev.houcheck.data.api

import com.kedokato_dev.houcheck.data.model.SessionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequest(val username: String, val password: String)


interface AuthService {
    @POST("/api/login")
    suspend fun login(@Body request: LoginRequest): Response<SessionResponse>
}