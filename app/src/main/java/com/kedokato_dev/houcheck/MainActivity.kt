package com.kedokato_dev.houcheck

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.kedokato_dev.houcheck.navhost.AppNavigation
import com.kedokato_dev.houcheck.ui.theme.HouCheckTheme
import com.kedokato_dev.houcheck.ui.theme.appTheme.ThemeMode
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
            val theme = viewModel.currentTheme
            val themeColor = viewModel.currentThemeColor
            Log.i(packageName,"ThemeUsed: ${theme.name}, ThemeColorUsed: $themeColor")
            HouCheckTheme(themeSelected = theme,themeColor = themeColor) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(viewModel)
                }
            }
        }
    }
}