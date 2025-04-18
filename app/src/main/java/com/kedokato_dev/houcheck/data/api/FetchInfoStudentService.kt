package com.kedokato_dev.houcheck.data.api

import com.kedokato_dev.houcheck.data.model.StudentResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface FetchInfoStudentService {
    @GET("/api/info-student")
    suspend fun fetchInfoStudent(@Query("sessionId") sessionId: String): Response<StudentResponse>
}



