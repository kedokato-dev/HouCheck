package com.kedokato_dev.houcheck.ui.theme


import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// 🎨 Màu cho chế độ Sáng
private val LightColors = lightColorScheme(
    primary = Color(0xFF2196F3),        // Xanh dương
    onPrimary = Color.White,
    background = Color.White,           // Nền trắng
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black
)

// 🌙 Màu cho chế độ Tối
private val DarkColors = darkColorScheme(
    primary = Color(0xFF90CAF9),        // Xanh nhạt
    onPrimary = Color.Black,
    background = Color(0xFF121212),     // Nền tối
    onBackground = Color.White,
    surface = Color(0xFF1E1E1E),
    onSurface = Color.White
)

// ✨ Typography và Shapes (dùng mặc định nếu chưa cần chỉnh)
private val AppTypography = Typography()
private val AppShapes = Shapes()

// 🧩 Theme tổng
@Composable
fun HouCheckTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (useDarkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}