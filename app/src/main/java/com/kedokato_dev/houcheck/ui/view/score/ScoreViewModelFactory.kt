package com.kedokato_dev.houcheck.ui.view.score

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kedokato_dev.houcheck.repository.ScoreRepository

class ScoreViewModelFactory(
    private val repository: ScoreRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FetchScoreViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FetchScoreViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}