package com.kedokato_dev.houcheck

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.kedokato_dev.houcheck.ui.theme.appTheme.ThemeMode
import com.kedokato_dev.houcheck.ui.theme.colorTheme.AppColors
import com.kedokato_dev.houcheck.ui.theme.colorTheme.ThemeColors
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel  @Inject constructor(): ViewModel() {
    var currentTheme by mutableStateOf(ThemeMode.getThemeMode())
        private set
    var currentThemeColor by mutableStateOf(AppColors.getThemeColor())
        private set
    fun setTheme(theme: ThemeMode.ThemeData) {
        currentTheme = theme
    }
    fun setThemeColor(themeColors: ThemeColors?) {
        currentThemeColor = themeColors
    }
}