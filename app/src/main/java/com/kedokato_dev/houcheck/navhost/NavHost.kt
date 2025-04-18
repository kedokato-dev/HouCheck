package com.kedokato_dev.houcheck.navhost

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.kedokato_dev.houcheck.ui.LoginScreen
import com.kedokato_dev.houcheck.ui.view.HomeScreen
import com.kedokato_dev.houcheck.ui.view.StudentInfoScreen
import com.kedokato_dev.houcheck.ui.viewmodel.FetchState

@Composable
fun AppNavigation(
    navController: NavHostController
) {
    NavHost(navController = navController, startDestination = "login") {
        composable("home") {
            HomeScreen(navController)
        }
        composable("login") {
            LoginScreen(navController)
        }
        composable("studentInfo") {
            StudentInfoScreen(navController)
        }
    }
}