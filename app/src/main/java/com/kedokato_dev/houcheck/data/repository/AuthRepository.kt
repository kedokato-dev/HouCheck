package com.kedokato_dev.houcheck.data.repository

import com.kedokato_dev.houcheck.data.api.ApiClient
import com.kedokato_dev.houcheck.data.api.AuthService
import com.kedokato_dev.houcheck.data.api.LoginRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository {
    private val authService: AuthService by lazy {
        ApiClient.instance.create(AuthService::class.java)
    }

    private var cachedSessionId: String? = null

    suspend fun login(username: String, password: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                // Check cache first
                if (cachedSessionId != null) {
                    Result.success(cachedSessionId!!)
                } else {
                    // Call API
                    val response = authService.login(LoginRequest(username, password))
                    if (response.isSuccessful && response.body()?.success == true) {
                        val sessionId = response.body()?.sessionId ?: return@withContext Result.failure(Exception("Invalid sessionId"))
                        // Cache the sessionId
                        cachedSessionId = sessionId
                        Result.success(sessionId)
                    } else {
                        Result.failure(Exception("Login failed: ${response.message()}"))
                    }
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}