package com.kedokato_dev.houcheck.ui.view.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kedokato_dev.houcheck.network.model.StudentName
import com.kedokato_dev.houcheck.network.model.toStudentName
import com.kedokato_dev.houcheck.repository.StudentNameRepository
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

    private val repository: StudentNameRepository by lazy {
        StudentNameRepository()
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