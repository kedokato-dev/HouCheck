package com.kedokato_dev.houcheck.ui.view.feedback

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val repository: FeedBackRepository
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
            repository.getFeedbackByEmailLocal(email).collect { feedbackEntities ->
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
                val (success, responseMessage, id) = repository.postFeedback(
                    name,
                    email,
                    message,
                    currentTime
                )
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
                        repository.insertFeedbackLocal(feedbackEntities)
                    }
                } else {
                    _operationState.value = UiState.Error(responseMessage)
                }
            } catch (e: Exception) {
                _operationState.value = UiState.Error(e.message ?: "Failed to send feedback")
            }
        }
    }

    // update feedback có sử dụng flow để theo dõi sự thay đổi
    @SuppressLint("SuspiciousIndentation")
    fun updateFeedback(feedBack: FeedbackEntity) {
        if (feedBack.message.isBlank()) {
            _operationState.value = UiState.Error("Nội dung phản hồi không được để trống")
            return
        }
        _operationState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val success = repository.updateFeedback(feedBack.id, feedBack.message)
                if (success) {
                    _operationState.value = UiState.Success("Chỉnh sửa phản hồi thành công")
                    withContext(Dispatchers.IO) {
                        repository.updateFeedbackLocal(feedBack)
                    }
                } else {
                    _operationState.value = UiState.Error("Chỉnh sửa phản hồi thất bại")
                }
            } catch (e: Exception) {
                _operationState.value = UiState.Error(e.message ?: "Chỉnh sửa phản hồi thất bại")
            }
        }
    }

    fun deleteFeedback(feedbackId: String) {
        if (feedbackId.isBlank()) {
            _operationState.value = UiState.Error("Feedback ID không hợp lệ")
            return
        }

        _operationState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val success = repository.deleteFeedback(feedbackId)
                if (success) {
                    _operationState.value = UiState.Success("Xóa phản hồi thành công")
                    withContext(Dispatchers.IO) {
                        repository.deleteFeedbackByIdLocal(feedbackId)
                    }
                } else {
                    _operationState.value = UiState.Error("Xóa phản hồi thất bại")
                }
            } catch (e: Exception) {
                _operationState.value = UiState.Error(e.message ?: "Xóa phản hồi thất bại")
            }
        }
    }
}