package com.kedokato_dev.houcheck.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kedokato_dev.houcheck.data.repository.FetchWeekScheduleRepository

class FetchWeekScheduleViewModelFactory(
    private val repository: FetchWeekScheduleRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FetchWeekScheduleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FetchWeekScheduleViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}