package com.kedokato_dev.houcheck.ui.viewmodel

import FetchInfoStudentViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kedokato_dev.houcheck.data.repository.FetchStudentInfoRepository

class FetchInfoStudentViewModelFactory(
    private val repository: FetchStudentInfoRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FetchInfoStudentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FetchInfoStudentViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}