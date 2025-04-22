package com.kedokato_dev.houcheck.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kedokato_dev.houcheck.data.model.ExamSchedule
import com.kedokato_dev.houcheck.data.repository.FetchExamScheduleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class FetchExamScheduleState {
    object Idle : FetchExamScheduleState()
    object Loading : FetchExamScheduleState()
    data class Success(val schedules: List<ExamSchedule>) : FetchExamScheduleState()
    data class Error(val message: String) : FetchExamScheduleState()
}

class FetchExamScheduleViewModel(
    private val repository: FetchExamScheduleRepository
) : ViewModel() {
    private val _fetchState = MutableStateFlow<FetchExamScheduleState>(FetchExamScheduleState.Idle)
    val fetchState: StateFlow<FetchExamScheduleState> = _fetchState.asStateFlow()

    suspend fun fetchExamSchedules(sessionId: String) {
        _fetchState.value = FetchExamScheduleState.Loading
        try {
            val schedules = repository.fetchExamSchedule(sessionId)
            _fetchState.value = FetchExamScheduleState.Success(schedules)
        } catch (e: Exception) {
            _fetchState.value = FetchExamScheduleState.Error(e.message ?: "Unknown error")
        }
    }
}