package com.kedokato_dev.houcheck.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.kedokato_dev.houcheck.data.model.CourseResult
import com.kedokato_dev.houcheck.data.repository.FetchListScoreRepository
import com.kedokato_dev.houcheck.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


class FetchListScoreViewModel(
    private val repository: FetchListScoreRepository
) : ViewModel() {
    private val _state = MutableStateFlow<UiState<List<CourseResult>>>(UiState.Idle)
    val state: StateFlow<UiState<List<CourseResult>>> get() = _state

    fun fetchListScore(sessionId: String) {
        if (sessionId.isBlank()) {
            _state.value = UiState.Error("Session ID cannot be empty")
            return
        }

        _state.value = UiState.Loading

        viewModelScope.launch {
            val result = repository.fetchAndSaveListScore(sessionId)
            _state.value = result.fold(
                onSuccess = { response -> UiState.Success(response.data.scores) },
                onFailure = { e -> UiState.Error(e.message ?: "Unknown error") }
            )
        }
    }

   fun refreshListScore(sessionId: String) {
        if (sessionId.isBlank()) {
            _state.value = UiState.Error("Session ID cannot be empty")
            return
        }

        _state.value = UiState.Loading

        viewModelScope.launch {
            val result = repository.refreshData(sessionId)
            _state.value = result.fold(
                onSuccess = { response -> UiState.Success(response) },
                onFailure = { e -> UiState.Error(e.message ?: "Unknown error") }
            )
        }
    }

    fun searchListScore(query: String) {
        validateInput(query, "Vui lòng nhập từ khóa tìm kiếm") {
            setLoadingState()
            viewModelScope.launch {
                val result = repository.searchCourseResultsByCourseName(query)
                _state.value = if (result.isNullOrEmpty()) {
                    UiState.Error("Không tìm thấy kết quả nào")
                } else {
                    UiState.Success(result.map { entity ->
                        CourseResult(
                            semester = entity.semester,
                            academicYear = entity.academicYear,
                            courseCode = entity.courseCode,
                            courseName = entity.courseName,
                            credits = entity.credits,
                            score10 = entity.score10,
                            score4 = entity.score4,
                            letterGrade = entity.letterGrade,
                            notCounted = entity.notCounted,
                            note = entity.note,
                            detailLink = entity.detailLink
                        )
                    })
                }
            }
        }
    }

    private inline fun validateInput(input: String, errorMessage: String, block: () -> Unit) {
        if (input.isBlank()) {
            setErrorState(errorMessage)
        } else {
            block()
        }
    }

    private fun setErrorState(message: String) {
        _state.value = UiState.Error(message)
    }

    private fun setLoadingState() {
        _state.value = UiState.Loading
    }
}