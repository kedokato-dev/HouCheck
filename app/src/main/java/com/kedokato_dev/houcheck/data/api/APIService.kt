package com.kedokato_dev.houcheck.data.api

import com.kedokato_dev.houcheck.data.model.Session
import com.kedokato_dev.houcheck.data.model.Student
import retrofit2.Call
import retrofit2.http.*

import retrofit2.http.GET

interface ApiService {

    @POST("/api/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): Session
    @GET("/")
    suspend fun getStatusServer(): String
}

data class LoginRequest(
    val username: String,
    val password: String
)