package com.kedokato_dev.houcheck.ui.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kedokato_dev.houcheck.data.model.Feedback
import com.kedokato_dev.houcheck.data.repository.FetchFeedBackRepository
import com.kedokato_dev.houcheck.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FetchFeedbackViewModel(
    private val repository: FetchFeedBackRepository = FetchFeedBackRepository()
): ViewModel() {

    private val _feedbackState = MutableStateFlow<UiState<List<Feedback>>>(UiState.Idle)
    val feedbackState: StateFlow<UiState<List<Feedback>>> get() = _feedbackState

    private val _operationState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val operationState: StateFlow<UiState<String>> get() = _operationState

    // Store feedback ID for update/delete operations
    private var currentFeedbackId: String = ""

    fun getFeedbackByEmail(email: String) {
        if (email.isBlank()) {
            _feedbackState.value = UiState.Error("Email cannot be empty")
            return
        }

        _feedbackState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val feedbacks = repository.getFeedbackByEmail(email)
                _feedbackState.value = UiState.Success(feedbacks)
            } catch (e: Exception) {
                _feedbackState.value = UiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendFeedback(name: String, email: String, message: String) {
        if (name.isBlank() || email.isBlank() || message.isBlank()) {
            _operationState.value = UiState.Error("All fields must be filled")
            return
        }

        _operationState.value = UiState.Loading

        val currentTime = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)

        viewModelScope.launch {
            try {
                val (success, responseMessage, id) = repository.postFeedback(name, email, message, currentTime)
                if (success) {
                    currentFeedbackId = id
                    _operationState.value = UiState.Success("Feedback sent successfully")
                } else {
                    _operationState.value = UiState.Error(responseMessage)
                }
            } catch (e: Exception) {
                _operationState.value = UiState.Error(e.message ?: "Failed to send feedback")
            }
        }
    }

    fun updateFeedback(newMessage: String) {
        if (newMessage.isBlank()) {
            _operationState.value = UiState.Error("Message cannot be empty")
            return
        }

        if (currentFeedbackId.isBlank()) {
            _operationState.value = UiState.Error("No feedback selected to update")
            return
        }

        _operationState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val success = repository.updateFeedback(currentFeedbackId, newMessage)
                if (success) {
                    _operationState.value = UiState.Success("Feedback updated successfully")
                } else {
                    _operationState.value = UiState.Error("Failed to update feedback")
                }
            } catch (e: Exception) {
                _operationState.value = UiState.Error(e.message ?: "Failed to update feedback")
            }
        }
    }

    fun deleteFeedback() {
        if (currentFeedbackId.isBlank()) {
            _operationState.value = UiState.Error("No feedback selected to delete")
            return
        }

        _operationState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val success = repository.deleteFeedback(currentFeedbackId)
                if (success) {
                    currentFeedbackId = ""
                    _operationState.value = UiState.Success("Feedback deleted successfully")
                } else {
                    _operationState.value = UiState.Error("Failed to delete feedback")
                }
            } catch (e: Exception) {
                _operationState.value = UiState.Error(e.message ?: "Failed to delete feedback")
            }
        }
    }

    fun setCurrentFeedback(id: String) {
        currentFeedbackId = id
    }

    fun resetOperationState() {
        _operationState.value = UiState.Idle
    }
}

class FetchFeedbackViewModelFactory(
    private val repository: FetchFeedBackRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FetchFeedbackViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FetchFeedbackViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}