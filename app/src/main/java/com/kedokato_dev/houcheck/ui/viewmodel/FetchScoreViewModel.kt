package com.kedokato_dev.houcheck.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kedokato_dev.houcheck.data.model.Score
import com.kedokato_dev.houcheck.data.repository.FetchScoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class FetchScoreState {
    object Idle : FetchScoreState()
    object Loading : FetchScoreState()
    data class Success(val scores: Score) : FetchScoreState()
    data class Error(val message: String) : FetchScoreState()
}

class FetchScoreViewModel(
    private val repository: FetchScoreRepository
) : ViewModel() {
    private val _fetchState = MutableStateFlow<FetchScoreState>(FetchScoreState.Idle)
    val fetchState: StateFlow<FetchScoreState> get() = _fetchState

    fun fetchScore(sessionId: String) {
        if (sessionId.isBlank()) {
            _fetchState.value = FetchScoreState.Error("Session ID cannot be empty")
            return
        }

        _fetchState.value = FetchScoreState.Loading


        viewModelScope.launch {
            val result = repository.fetchScore(sessionId)
            _fetchState.value = result.fold(
                onSuccess = { response ->
                    FetchScoreState.Success(response.data)
                },
                onFailure = { error ->
                    FetchScoreState.Error(error.message ?: "Unknown error")
                }
            )
        }
    }
}