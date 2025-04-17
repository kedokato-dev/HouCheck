package com.kedokato_dev.houcheck.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kedokato_dev.houcheck.data.api.ApiClient
import com.kedokato_dev.houcheck.data.api.ApiService
import com.kedokato_dev.houcheck.data.api.LoginRequest
import com.kedokato_dev.houcheck.data.model.Session
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import androidx.core.content.edit

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> get() = _username

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> get() = _password

    private val _loginStatus = MutableStateFlow<LoginStatus>(LoginStatus.Idle)
    val loginStatus: StateFlow<LoginStatus> get() = _loginStatus


    private val apiService: ApiService by lazy {
        ApiClient.instance.create(ApiService::class.java)
    }


    fun onUsernameChanged(newUsername: String) {
        _username.value = newUsername
    }


    fun onPasswordChanged(newPassword: String) {
        _password.value = newPassword
    }


    fun login() {
        if (_username.value.isBlank() || _password.value.isBlank()) {
            _loginStatus.value = LoginStatus.Error("Username hoặc Password không được để trống!")
            return
        }

        viewModelScope.launch {
            _loginStatus.value = LoginStatus.Loading
            try {
                val loginRequest = LoginRequest(username = _username.value, password = _password.value)
                val session = apiService.login(loginRequest)

                if (session.success) {
                    saveSessionId(session.sessionId)
                    _loginStatus.value = LoginStatus.Success
                } else {
                    _loginStatus.value = LoginStatus.Error("Đăng nhập thất bại.")
                }
            } catch (e: IOException) {
                _loginStatus.value = LoginStatus.Error("Lỗi kết nối mạng! Vui lòng kiểm tra Internet.")
            } catch (e: HttpException) {
                _loginStatus.value = LoginStatus.Error("Lỗi HTTP: ${e.code()} - ${e.message()}")
            } catch (e: Exception) {
                _loginStatus.value = LoginStatus.Error("Đã xảy ra lỗi không mong muốn: ${e.message}")
            }
        }
    }


    private fun saveSessionId(sessionId: String) {
        val sharedPreferences = getApplication<Application>()
            .getSharedPreferences("AppPreferences", Application.MODE_PRIVATE)

        sharedPreferences.edit {
            putString("session_id", sessionId)
        }
    }
}


sealed class LoginStatus {
    object Idle : LoginStatus()
    object Loading : LoginStatus()
    object Success : LoginStatus()
    data class Error(val message: String) : LoginStatus()
}