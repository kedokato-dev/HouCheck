package com.kedokato_dev.houcheck.ui.view.profile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kedokato_dev.houcheck.network.model.Student
import com.kedokato_dev.houcheck.repository.StudentInfoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


sealed class FetchState {
    object Idle : FetchState()
    object Loading : FetchState()
    data class Success(val student: Student) : FetchState()
    data class Error(val message: String) : FetchState()
}


@HiltViewModel
class InfoStudentViewModel @Inject constructor  (
    private val repository: StudentInfoRepository
) : ViewModel() {



    private val _fetchState = MutableStateFlow<FetchState>(FetchState.Idle)
    val fetchState: StateFlow<FetchState> get() = _fetchState

    fun fetchStudentIfNeeded(sessionId: String) {
        viewModelScope.launch {
            _fetchState.value = FetchState.Loading

            val localStudent = repository.getLocalStudentById()
            if (localStudent != null) {
                _fetchState.value = FetchState.Success(localStudent)
                return@launch
            }

            val result = repository.fetchAndSaveStudent(sessionId)
            _fetchState.value = result.fold(
                onSuccess = { student -> FetchState.Success(student) },
                onFailure = { e -> FetchState.Error(e.message ?: "Lỗi không xác định") }
            )
        }
    }
}

