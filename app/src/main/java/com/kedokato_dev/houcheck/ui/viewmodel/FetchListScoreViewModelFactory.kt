package com.kedokato_dev.houcheck.ui.viewmodel

import FetchInfoStudentViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kedokato_dev.houcheck.data.repository.FetchListScoreRepository

class FetchListScoreViewModelFactory(
    private val repository: FetchListScoreRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FetchListScoreViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FetchListScoreViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}