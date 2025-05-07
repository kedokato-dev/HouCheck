package com.kedokato_dev.houcheck.ui.view.week_schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kedokato_dev.houcheck.network.model.ScheduleResponse
import com.kedokato_dev.houcheck.repository.WeekScheduleRepository
import com.kedokato_dev.houcheck.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeekScheduleViewModel(
    private  val repository: WeekScheduleRepository,
): ViewModel() {
    private val _state = MutableStateFlow<UiState<ScheduleResponse>>(UiState.Idle)
    val state: StateFlow<UiState<ScheduleResponse>> get() = _state

     fun fetchWeekSchedule(sessionId: String, weekValue: String) {

         if (sessionId.isBlank()){
                _state.value = UiState.Error("Session ID cannot be empty")
                return
         }

            _state.value = UiState.Loading

         viewModelScope.launch {
             val result = repository.fetchWeekSchedule(sessionId, weekValue)
             _state.value = result.fold(
                 onSuccess = { response -> UiState.Success(response) },
                 onFailure = { e -> UiState.Error(e.message ?: "Unknown error") }
             )
         }
    }

}