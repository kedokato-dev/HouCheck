package com.kedokato_dev.houcheck.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kedokato_dev.houcheck.data.model.Student
import com.kedokato_dev.houcheck.data.model.StudentResponse
import com.kedokato_dev.houcheck.data.model.toStudent
import com.kedokato_dev.houcheck.data.repository.AuthRepository
import com.kedokato_dev.houcheck.data.repository.FetchStudentInfoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


sealed class FetchState {
    object Idle : FetchState()
    object Loading : FetchState()
    data class Success(val student: Student) : FetchState()
    data class Error(val message: String) : FetchState()
}

class FetchInfoStudentViewModel(
    private val repository: FetchStudentInfoRepository
) : ViewModel() {
    private val _fetchState = MutableStateFlow<FetchState>(FetchState.Idle)
    val fetchState: StateFlow<FetchState> get() = _fetchState


    fun fetchInfoStudent(sessionId: String) {
        if (sessionId.isBlank()) {
            _fetchState.value = FetchState.Error("Session ID cannot be empty")
            return
        }

        _fetchState.value = FetchState.Loading

        viewModelScope.launch {
            val result = repository.fetchInfoStudent(sessionId)
            _fetchState.value = result.fold(
                onSuccess = { studentResponse ->
                    FetchState.Success(studentResponse.toStudent()) // Chuyển đổi từ StudentResponse sang Student
                },
                onFailure = { error -> FetchState.Error(error.message ?: "Unknown error") }
            )
        }
    }
}
