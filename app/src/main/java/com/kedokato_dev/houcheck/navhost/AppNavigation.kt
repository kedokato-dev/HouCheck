package com.kedokato_dev.houcheck.navhost

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.kedokato_dev.houcheck.ui.LoginScreen
import com.kedokato_dev.houcheck.ui.view.ExamScheduleScreen
import com.kedokato_dev.houcheck.ui.view.HomeScreen
import com.kedokato_dev.houcheck.ui.view.ListScoreScreen
import com.kedokato_dev.houcheck.ui.view.ScoreScreen
import com.kedokato_dev.houcheck.ui.view.SettingScreen
import com.kedokato_dev.houcheck.ui.view.StudentInfoScreen
import com.kedokato_dev.houcheck.ui.view.TrainingScoreScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    val bottomBarState = rememberSaveable { mutableStateOf(false) }
    val primaryColor = Color(0xFF03A9F4)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    bottomBarState.value = currentRoute != "login"

    Scaffold(
        contentWindowInsets = WindowInsets.systemBars,
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                bottomBarState = bottomBarState,
                items = listOf(
                    BottomNavItem("Trang chủ", "home", Icons.Filled.Home, Icons.Outlined.Home),
                    BottomNavItem("Hồ sơ", "studentInfo", Icons.Filled.Person, Icons.Outlined.Person),
                    BottomNavItem("Cài đặt", "settings", Icons.Filled.Settings, Icons.Outlined.Settings)
                ),
                primaryColor = primaryColor
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            NavHost(navController = navController, startDestination = "login") {
                composable("home") { HomeScreen(navController) }
                composable("login") { LoginScreen(navController) }
                composable("studentInfo") { StudentInfoScreen(navController) }
                composable("settings") { SettingScreen(navController) }
                composable("training_score") { TrainingScoreScreen(navController) }
                composable ("score") { ScoreScreen(navController) }
                composable("list_score") { ListScoreScreen(navController) }
                composable("exam_schedule") { ExamScheduleScreen(navController) }
                }
            }
        }
    }

