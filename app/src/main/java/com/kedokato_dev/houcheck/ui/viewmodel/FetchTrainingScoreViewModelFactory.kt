package com.kedokato_dev.houcheck.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kedokato_dev.houcheck.data.repository.FetchTrainingScoreRepository

class FetchTrainingScoreViewModelFactory(
    private val repository: FetchTrainingScoreRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FetchTrainingScoreViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FetchTrainingScoreViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}