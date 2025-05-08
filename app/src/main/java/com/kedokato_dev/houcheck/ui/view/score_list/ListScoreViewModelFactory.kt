package com.kedokato_dev.houcheck.ui.view.score_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kedokato_dev.houcheck.repository.ListScoreRepository

class ListScoreViewModelFactory(
    private val repository: ListScoreRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListScoreViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ListScoreViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}