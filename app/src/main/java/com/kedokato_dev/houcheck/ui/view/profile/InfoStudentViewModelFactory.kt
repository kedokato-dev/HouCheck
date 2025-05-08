package com.kedokato_dev.houcheck.ui.view.profile

import FetchInfoStudentViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kedokato_dev.houcheck.repository.StudentInfoRepository

class InfoStudentViewModelFactory(
    private val repository: StudentInfoRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FetchInfoStudentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FetchInfoStudentViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}