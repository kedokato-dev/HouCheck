package com.kedokato_dev.houcheck.ui.viewmodel

import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kedokato_dev.houcheck.data.model.Student
import com.kedokato_dev.houcheck.data.model.StudentName
import com.kedokato_dev.houcheck.data.model.toStudent
import com.kedokato_dev.houcheck.data.model.toStudentName
import com.kedokato_dev.houcheck.data.repository.FetchStudentNameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


sealed class FetchStudentNameState() {
    object Idle : FetchStudentNameState()
    object Loading : FetchStudentNameState()
    data class Success(val studentName: StudentName) : FetchStudentNameState()
    data class Error(val message: String) : FetchStudentNameState()
}

class FetchNameStudentViewModel() : ViewModel() {

    private val _fetchNameState = MutableStateFlow<FetchStudentNameState>(FetchStudentNameState.Idle)
    val fetchNameState: StateFlow<FetchStudentNameState> get() = _fetchNameState

    private val repository: FetchStudentNameRepository by lazy {
        FetchStudentNameRepository()
    }

    fun fetchNameStudent(sessionId: String) {
        if (sessionId.isBlank()) {
            _fetchNameState.value = FetchStudentNameState.Error("Session ID cannot be empty")
            return
        }

        _fetchNameState.value = FetchStudentNameState.Loading

        viewModelScope.launch {
            val result = repository.fetchStudentName(sessionId)
            _fetchNameState.value = result.fold(
                onSuccess = { studentResponse ->
                    FetchStudentNameState.Success(studentResponse.toStudentName())
                },
                onFailure = { error -> FetchStudentNameState.Error(error.message ?: "Unknown error") }
            )
        }
    }



}