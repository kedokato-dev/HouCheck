package com.kedokato_dev.houcheck.network.api

import com.kedokato_dev.houcheck.network.model.StudentResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface InfoStudentService {
    @GET("/api/info-student")
    suspend fun fetchInfoStudent(@Query("sessionId") sessionId: String): Response<StudentResponse>
}



