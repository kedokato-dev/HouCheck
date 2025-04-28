package com.kedokato_dev.houcheck.navhost

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.kedokato_dev.houcheck.ui.LoginScreen
import com.kedokato_dev.houcheck.ui.theme.HNOUDarkBlue
import com.kedokato_dev.houcheck.ui.view.ExamScheduleScreen
import com.kedokato_dev.houcheck.ui.view.HomeScreen
import com.kedokato_dev.houcheck.ui.view.ListScoreScreen
import com.kedokato_dev.houcheck.ui.view.ScheduleScreen
import com.kedokato_dev.houcheck.ui.view.ScoreScreen
import com.kedokato_dev.houcheck.ui.view.SettingScreen
import com.kedokato_dev.houcheck.ui.view.SplashScreen
import com.kedokato_dev.houcheck.ui.view.StudentInfoScreen
import com.kedokato_dev.houcheck.ui.view.TrainingScoreScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    // Start with the initial state explicitly set to false
    val initialRoute = remember { mutableStateOf("splash") }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: initialRoute.value

    // Initialize a state to indicate if app has fully started - initially false
    val appFullyStarted = remember { mutableStateOf(false) }

    // Determine bottom bar visibility directly
    val bottomBarVisible = currentRoute !in listOf(
        "training_score", "login", "studentInfo", "score", "list_score", "splash",
        "exam_schedule", "week_schedule"
    ) && appFullyStarted.value

    Scaffold(
        contentWindowInsets = WindowInsets.systemBars,
        bottomBar = {
            // Only include bottom bar in composition if it should be visible
            if (bottomBarVisible) {
                BottomNavigationBar(
                    navController = navController,
                    items = listOf(
                        BottomNavItem("Trang chủ", "home", Icons.Filled.Home, Icons.Outlined.Home),
                        BottomNavItem(
                            "Hồ sơ",
                            "studentInfo",
                            Icons.Filled.Person,
                            Icons.Outlined.Person
                        ),
                        BottomNavItem(
                            "Cài đặt",
                            "settings",
                            Icons.Filled.Settings,
                            Icons.Outlined.Settings
                        )
                    ),
                    primaryColor = HNOUDarkBlue
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            NavHost(navController = navController, startDestination = "splash") {
                composable("home") {
                    // When reaching home screen, app is considered fully started
                    appFullyStarted.value = true
                    HomeScreen(navController)
                }
                composable("login") { LoginScreen(navController) }
                composable("studentInfo") { StudentInfoScreen(navController) }
                composable("settings") {
                    // When reaching settings screen, app is considered fully started
                    appFullyStarted.value = true
                    SettingScreen(navController)
                }
                composable("training_score") { TrainingScoreScreen(navController) }
                composable("score") { ScoreScreen(navController) }
                composable("list_score") { ListScoreScreen(navController) }
                composable("exam_schedule") { ExamScheduleScreen(navController) }
                composable("week_schedule") { ScheduleScreen(navController) }
                composable("splash") {
                    SplashScreen {
                        navController.navigate("login") {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                }
            }
        }
    }
}