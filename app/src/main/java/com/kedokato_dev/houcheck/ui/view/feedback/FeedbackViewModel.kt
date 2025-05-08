package com.kedokato_dev.houcheck.ui.view.feedback

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kedokato_dev.houcheck.local.dao.FeedbackDAO
import com.kedokato_dev.houcheck.local.entity.FeedbackEntity
import com.kedokato_dev.houcheck.network.model.Feedback
import com.kedokato_dev.houcheck.repository.FeedBackRepository
import com.kedokato_dev.houcheck.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class FetchFeedbackViewModel @Inject constructor(
    private val repository: FeedBackRepository,
    private val feedbackDao: FeedbackDAO,
) : ViewModel() {

    private val _feedbackState = MutableStateFlow<UiState<List<Feedback>>>(UiState.Idle)
    val feedbackState: StateFlow<UiState<List<Feedback>>> get() = _feedbackState

    private val _operationState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val operationState: StateFlow<UiState<String>> get() = _operationState

    // Sử dụng Flow để lắng nghe sự thay đổi
    fun observeFeedbackByEmail(email: String) {
        if (email.isBlank()) {
            _feedbackState.value = UiState.Error("Email cannot be empty")
            return
        }

        _feedbackState.value = UiState.Loading

        viewModelScope.launch {
            feedbackDao.getFeedbackByEmail(email).collect { feedbackEntities ->
                if (feedbackEntities.isNotEmpty()) {
                    _feedbackState.value = UiState.Success(feedbackEntities.map { localFeedback ->
                        Feedback(
                            id = localFeedback.id,
                            name = localFeedback.name,
                            email = localFeedback.email,
                            message = localFeedback.message,
                            createdAt = localFeedback.createdAt
                        )
                    })
                } else {
                    _feedbackState.value = UiState.Success(emptyList())
                }
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
                    _operationState.value = UiState.Success("Feedback sent successfully")

                    // Fetch lại dữ liệu từ server và lưu vào Room
                    val feedbacks = repository.getFeedbackByEmail(email)
                    withContext(Dispatchers.IO) {
                        val feedbackEntities = feedbacks.map { networkFeedback ->
                            FeedbackEntity(
                                id = networkFeedback.id,
                                name = networkFeedback.name,
                                email = networkFeedback.email,
                                message = networkFeedback.message,
                                createdAt = networkFeedback.createdAt
                            )
                        }
                        feedbackDao.insertFeedback(feedbackEntities)
                    }
                } else {
                    _operationState.value = UiState.Error(responseMessage)
                }
            } catch (e: Exception) {
                _operationState.value = UiState.Error(e.message ?: "Failed to send feedback")
            }
        }
    }
}