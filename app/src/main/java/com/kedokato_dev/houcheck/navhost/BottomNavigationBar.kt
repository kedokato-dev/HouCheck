package com.kedokato_dev.houcheck.navhost

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.kedokato_dev.houcheck.ui.theme.HNOUDarkBlue
import com.kedokato_dev.houcheck.ui.theme.HNOULightBlue

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    bottomBarState: MutableState<Boolean>,
    items: List<BottomNavItem>,
    primaryColor: Color
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Base height for the navigation bar content
    val baseNavigationHeight = 64.dp

    // Determine if the navigation bar should be visible
    bottomBarState.value = when (currentRoute) {
        "training_score", "login", "studentInfo", "score", "list_score" -> false
        else -> true
    }

    // Get the system navigation bar insets
    val navigationBarsInsets = WindowInsets.navigationBars.asPaddingValues()
    val hasVisibleNavigationBar = navigationBarsInsets.calculateBottomPadding() > 0.dp

    // Adjust height based on navigation mode
    // For gesture navigation (smaller or no visible navigation bar) we use the baseHeight
    // For 3-button navigation (larger visible navigation bar) we add some extra space
    val adjustedHeight = if (hasVisibleNavigationBar) {
        baseNavigationHeight + 50.dp // Add some extra space for 3-button navigation
    } else {
        baseNavigationHeight // Use base height for gesture navigation
    }

    AnimatedVisibility(
        visible = bottomBarState.value,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it })
    ) {
        NavigationBar(
            modifier = Modifier
                .fillMaxWidth()
                .height(adjustedHeight)
                .navigationBarsPadding(),
            containerColor = HNOUDarkBlue.copy(alpha = 0.95f),
            tonalElevation = 16.dp,
        ) {
            items.forEach { item ->
                val selected = currentRoute == item.route
                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo("home") { saveState = true; inclusive = false }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.name
                        )
                    },
                    label = {
                        Text(
                            text = item.name,
                            style = TextStyle(
                                color = if (selected) Color.White else Color.White.copy(alpha = 0.6f),
                                fontSize = 14.sp, fontWeight = FontWeight.Bold),
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        indicatorColor = HNOULightBlue.copy(alpha = 0.95f),
                        unselectedIconColor = Color.White.copy(alpha = 0.6f),
                        unselectedTextColor = Color.White.copy(alpha = 0.6f)
                    )
                )
            }
        }
    }
}