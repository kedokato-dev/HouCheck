package com.kedokato_dev.houcheck.ui.view.training_score

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kedokato_dev.houcheck.network.model.TrainingScore
import com.kedokato_dev.houcheck.repository.TrainingScoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class FetchTrainingScoreState {
    object Idle : FetchTrainingScoreState()
    object Loading : FetchTrainingScoreState()
    data class Success(val scores: List<TrainingScore>) : FetchTrainingScoreState()
    data class Error(val message: String) : FetchTrainingScoreState()
}

class FetchTrainingScoreViewModel(
    private val repository: TrainingScoreRepository
) : ViewModel() {
    private val _fetchState = MutableStateFlow<FetchTrainingScoreState>(FetchTrainingScoreState.Idle)
    val fetchState: StateFlow<FetchTrainingScoreState> get() = _fetchState

    fun fetchTrainingScore(sessionId: String) {
        if (sessionId.isBlank()) {
            _fetchState.value = FetchTrainingScoreState.Error("Session ID cannot be empty")
            return
        }

        _fetchState.value = FetchTrainingScoreState.Loading

        viewModelScope.launch {
            val result = repository.fetchTrainingScore(sessionId)
            _fetchState.value = result.fold(
                onSuccess = { response ->
                    FetchTrainingScoreState.Success(response.data)
                },
                onFailure = { error ->
                    FetchTrainingScoreState.Error(error.message ?: "Unknown error")
                }
            )
        }
    }

    fun refreshData(sessionId: String){
        if (sessionId.isBlank()) {
            _fetchState.value = FetchTrainingScoreState.Error("Session ID cannot be empty")
            return
        }

        _fetchState.value = FetchTrainingScoreState.Loading

        viewModelScope.launch {
            val result = repository.refreshData(sessionId)
            _fetchState.value = result!!.fold(
                onSuccess = { response ->
                    FetchTrainingScoreState.Success(response.data)
                },
                onFailure = { error ->
                    FetchTrainingScoreState.Error(error.message ?: "Unknown error")
                }
            )
        }
    }
}