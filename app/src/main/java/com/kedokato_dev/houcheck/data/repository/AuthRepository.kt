package com.kedokato_dev.houcheck.data.repository

import android.content.SharedPreferences
import com.kedokato_dev.houcheck.data.api.ApiClient
import com.kedokato_dev.houcheck.data.api.AuthService
import com.kedokato_dev.houcheck.data.api.LoginRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(private val sharedPreferences: SharedPreferences) {
    private val authService: AuthService by lazy {
        ApiClient.instance.create(AuthService::class.java)
    }

    // Cache sessionId in memory for faster access
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
                        val sessionId = response.body()?.sessionId
                            ?: return@withContext Result.failure(Exception("Invalid sessionId"))

                        // Cache the sessionId in memory and save it in SharedPreferences
                        cachedSessionId = sessionId
                        saveSessionIdToPreferences(sessionId)

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

    fun getSessionId(): String? {
        // Return cached sessionId if available, otherwise load from SharedPreferences
        return cachedSessionId ?: sharedPreferences.getString("SESSION_ID", null)
    }

    private fun saveSessionIdToPreferences(sessionId: String) {
        sharedPreferences.edit()
            .putString("SESSION_ID", sessionId)
            .apply()
    }

    fun clearSession() {
        // Clear both cached sessionId and the value in SharedPreferences
        cachedSessionId = null
        sharedPreferences.edit()
            .remove("SESSION_ID")
            .apply()
    }
}