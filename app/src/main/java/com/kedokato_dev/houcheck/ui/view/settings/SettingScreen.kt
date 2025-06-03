package com.kedokato_dev.houcheck.ui.view.settings

import ColorPickerDialog
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import colorOptionLists
import com.kedokato_dev.houcheck.MainViewModel
import com.kedokato_dev.houcheck.R
import com.kedokato_dev.houcheck.ui.theme.appTheme.ThemeMode
import com.kedokato_dev.houcheck.ui.theme.colorTheme.AppColors
import com.kedokato_dev.houcheck.ui.theme.primaryColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(navHostController: NavHostController,viewModel: MainViewModel? = null) {

    val context = LocalContext.current
    var showThemeDialog by remember { mutableStateOf(false) }
    var showThemeColorDialog by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Cài đặt",
                        fontSize = 20.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)

        ) {
            val settings = listOf(
                SettingItem(1,"Đăng xuất", icon = painterResource(id = R.drawable.logout)  ),
                SettingItem(2,"Theme Mode ( Dark, Light )", icon = painterResource(id = R.drawable.dark_mode)  ),
                SettingItem(3,"Theme Color", icon = painterResource(id = R.drawable.ic_custom_color)  ),
                SettingItem(4,"Gửi ý kiến cải thiện app", icon = painterResource(id = R.drawable.feedback)  )
            )

            settings.forEach { item ->
                SettingItemRow(item = item, onClick = {
                    when (item.id) {
                        1 -> {
                            navHostController.navigate("login") {
                               // khong cho phep quay lai
                                popUpTo("login") {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }
                        2 -> {
                            showThemeDialog = true
                        }
                        3 -> {
                            showThemeColorDialog = true
                        }
                       4 -> {
                            navHostController.navigate("feedback")
                        }
                    }

                })
                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
    if(showThemeDialog) {
        ThemeMode.ThemePickerDialog(
            viewModel?.currentTheme ?: ThemeMode.ThemeData.System,
            onDismiss = {
                showThemeDialog = false
            }, onThemeSelected = {
                ThemeMode.setThemeMode(it)
                viewModel?.setTheme(it)
            }
        )
    }
    if (showThemeColorDialog) {
        ColorPickerDialog(
            currentColor = viewModel?.currentThemeColor ?: AppColors.Blue,
            onDismiss = { showThemeColorDialog = false },
            onReset = {
                AppColors.setThemeColor(-1)
                viewModel?.setThemeColor(null)
            },
            onColorSelected = {
                AppColors.setThemeColor(colorOptionLists.indexOf(it))
                viewModel?.setThemeColor(it)
            }
        )
    }
}


@Composable
fun SettingItemRow(item: SettingItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            painter = item.icon,
            contentDescription = item.title,
            modifier = Modifier.size(24.dp),
            tint = primaryColor
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = item.title,
            fontSize = 16.sp
        )
    }
}

data class  SettingItem(
    val id: Int,
    val title: String,
    val icon: Painter
)




@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SettingScreenPreview() {
    val navHostController = NavHostController(LocalContext.current)
    SettingScreen(navHostController)
}