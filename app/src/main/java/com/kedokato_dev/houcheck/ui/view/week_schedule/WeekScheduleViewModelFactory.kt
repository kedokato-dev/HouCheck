package com.kedokato_dev.houcheck.ui.view.week_schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kedokato_dev.houcheck.repository.WeekScheduleRepository

class WeekScheduleViewModelFactory(
    private val repository: WeekScheduleRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeekScheduleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeekScheduleViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}