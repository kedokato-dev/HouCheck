package com.kedokato_dev.houcheck.navhost

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kedokato_dev.houcheck.MainViewModel
import com.kedokato_dev.houcheck.ui.LoginScreen
import com.kedokato_dev.houcheck.ui.theme.HNOUDarkBlue
import com.kedokato_dev.houcheck.ui.theme.appTheme.ThemeMode
import com.kedokato_dev.houcheck.ui.view.exam_schedule.ExamScheduleScreen
import com.kedokato_dev.houcheck.ui.view.feedback.FeedbackScreen
import com.kedokato_dev.houcheck.ui.view.home.HomeScreen
import com.kedokato_dev.houcheck.ui.view.home.HomeScreenContainer
import com.kedokato_dev.houcheck.ui.view.profile.StudentInfoScreen
import com.kedokato_dev.houcheck.ui.view.score.ScoreScreen
import com.kedokato_dev.houcheck.ui.view.score_list.ListScoreScreen
import com.kedokato_dev.houcheck.ui.view.settings.SettingScreen
import com.kedokato_dev.houcheck.ui.view.splash.SplashScreen
import com.kedokato_dev.houcheck.ui.view.training_score.TrainingScoreScreen
import com.kedokato_dev.houcheck.ui.view.week_schedule.ScheduleScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(viewModel: MainViewModel) {
    val navController = rememberNavController()
    AppNavigation(navController,viewModel)
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(navController: NavHostController,viewModel: MainViewModel) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "calendar"
    val appFullyStarted = remember { mutableStateOf(false) }

    val bottomBarVisible = currentRoute !in listOf(
        "training_score", "login", "studentInfo", "score", "list_score", "splash",
        "exam_schedule", "week_schedule", "feedback", "calender"
    ) && appFullyStarted.value

    Scaffold(
        contentWindowInsets = WindowInsets.systemBars,
        bottomBar = {
            AnimatedVisibility(
                visible = bottomBarVisible,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                BottomNavigationBar(
                    navController = navController,
                    items = listOf(
                        BottomNavItem("Trang chủ", "home", Icons.Filled.Home, Icons.Outlined.Home),
                        BottomNavItem("Hồ sơ", "studentInfo", Icons.Filled.Person, Icons.Outlined.Person),
                        BottomNavItem("Cài đặt", "settings", Icons.Filled.Settings, Icons.Outlined.Settings)
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
            NavHost(
                navController = navController,
                startDestination = "splash",
                enterTransition = { getEnterTransition(initialState, targetState) },
                exitTransition = { getExitTransition(initialState, targetState) },
                popEnterTransition = { getPopEnterTransition(initialState, targetState) },
                popExitTransition = { getPopExitTransition(initialState, targetState) }
            ) {
                composable("splash") {
                    SplashScreen {
                        navController.navigate("login") {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                }
                composable("login") {
                    LoginScreen(navController)
                }
                composable("home") {
                    appFullyStarted.value = true
                    HomeScreenContainer(navController)
                }
                composable("studentInfo") {
                    StudentInfoScreen(navController)
                }
                composable("settings") {
                    appFullyStarted.value = true
                    SettingScreen(navController,viewModel)
                }
                composable("training_score") {
                    TrainingScoreScreen(navController)
                }
                composable("score") {
                    ScoreScreen(navController)
                }
                composable("list_score") {
                    ListScoreScreen(navController)
                }
                composable("exam_schedule") {
                    ExamScheduleScreen(navController)
                }
                composable("week_schedule") {
                    ScheduleScreen(navController)
                }
                composable("feedback") {
                    appFullyStarted.value = true
                    FeedbackScreen(navController)
                }
            }
        }
    }
}

private fun getEnterTransition(
    initialState: NavBackStackEntry,
    targetState: NavBackStackEntry
): EnterTransition {
    val initialRoute = initialState.destination.route ?: ""
    val targetRoute = targetState.destination.route ?: ""
    
    return when {
        targetRoute == "splash" -> fadeIn(animationSpec = tween(700))
        targetRoute == "login" -> fadeIn(animationSpec = tween(700))
        initialRoute == "splash" -> fadeIn(animationSpec = tween(700))
        else -> slideInHorizontally(
            initialOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(300)
        ) + fadeIn(animationSpec = tween(300))
    }
}

private fun getExitTransition(
    initialState: NavBackStackEntry,
    targetState: NavBackStackEntry
): ExitTransition {
    val initialRoute = initialState.destination.route ?: ""
    val targetRoute = targetState.destination.route ?: ""
    
    return when {
        initialRoute == "splash" -> fadeOut(animationSpec = tween(700))
        else -> slideOutHorizontally(
            targetOffsetX = { fullWidth -> -fullWidth / 3 },
            animationSpec = tween(300)
        ) + fadeOut(animationSpec = tween(300))
    }
}

private fun getPopEnterTransition(
    initialState: NavBackStackEntry,
    targetState: NavBackStackEntry
): EnterTransition {
    return slideInHorizontally(
        initialOffsetX = { fullWidth -> -fullWidth / 3 },
        animationSpec = tween(300)
    ) + fadeIn(animationSpec = tween(300))
}

private fun getPopExitTransition(
    initialState: NavBackStackEntry,
    targetState: NavBackStackEntry
): ExitTransition {
    return slideOutHorizontally(
        targetOffsetX = { fullWidth -> fullWidth },
        animationSpec = tween(300)
    ) + fadeOut(animationSpec = tween(300))
}