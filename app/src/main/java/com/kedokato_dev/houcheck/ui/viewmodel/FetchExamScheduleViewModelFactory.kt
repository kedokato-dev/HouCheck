package com.kedokato_dev.houcheck.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kedokato_dev.houcheck.data.repository.FetchExamScheduleRepository

class FetchExamScheduleViewModelFactory(
    private val repository: FetchExamScheduleRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FetchExamScheduleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FetchExamScheduleViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}