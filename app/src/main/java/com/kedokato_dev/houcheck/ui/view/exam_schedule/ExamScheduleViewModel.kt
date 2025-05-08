package com.kedokato_dev.houcheck.ui.view.exam_schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kedokato_dev.houcheck.network.model.ExamSchedule
import com.kedokato_dev.houcheck.repository.ExamScheduleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class FetchExamScheduleState {
    object Idle : FetchExamScheduleState()
    object Loading : FetchExamScheduleState()
    data class Success(val schedules: List<ExamSchedule>) : FetchExamScheduleState()
    data class Error(val message: String) : FetchExamScheduleState()
}

class FetchExamScheduleViewModel(
    private val repository: ExamScheduleRepository
) : ViewModel() {
    private val _fetchState = MutableStateFlow<FetchExamScheduleState>(FetchExamScheduleState.Idle)
    val fetchState: StateFlow<FetchExamScheduleState> = _fetchState.asStateFlow()

    fun fetchExamSchedules(sessionId: String) {
        viewModelScope.launch {
            _fetchState.value = FetchExamScheduleState.Loading
            try {
                val schedules = repository.fetchExamSchedule(sessionId)
                _fetchState.value = FetchExamScheduleState.Success(schedules)
            } catch (e: Exception) {
                _fetchState.value = FetchExamScheduleState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun refreshExamSchedules(sessionId: String) {
        viewModelScope.launch {
            _fetchState.value = FetchExamScheduleState.Loading
            try {
                val schedules = repository.refreshExamSchedule(sessionId)
                _fetchState.value = FetchExamScheduleState.Success(schedules)
            } catch (e: Exception) {
                _fetchState.value = FetchExamScheduleState.Error(e.message ?: "Unknown error")
            }
        }
    }
}