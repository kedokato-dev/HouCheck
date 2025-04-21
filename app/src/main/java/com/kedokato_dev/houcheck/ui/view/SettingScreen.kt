package com.kedokato_dev.houcheck.ui.view

import android.widget.Toast
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.kedokato_dev.houcheck.R
import com.kedokato_dev.houcheck.ui.theme.primaryColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(navHostController: NavHostController) {

    val context = LocalContext.current


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
                    containerColor = primaryColor,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
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
                SettingItem("Đăng xuất", icon = painterResource(id = R.drawable.logout)  ),
                SettingItem("Dark Mode", icon = painterResource(id = R.drawable.dark_mode)  )
            )

            settings.forEach { item ->
                SettingItemRow(item = item, onClick = {
                    when (item.title) {
                        "Đăng xuất" -> {
                            navHostController.navigate("login") {
                               // khong cho phep quay lai
                                popUpTo("login") {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }
                        "Dark Mode" -> {
                            Toast.makeText(context, "Chưa có chức năng này", Toast.LENGTH_SHORT).show()
                        }
                    }

                })
                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }
        }
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
    val title: String,
    val icon: Painter
)




@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SettingScreenPreview() {
    val navHostController = NavHostController(LocalContext.current)
    SettingScreen(navHostController)
}