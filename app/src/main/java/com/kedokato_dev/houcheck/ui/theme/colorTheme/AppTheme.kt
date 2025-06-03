package com.kedokato_dev.houcheck.ui.theme.colorTheme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import colorOptionLists
import com.kedokato_dev.houcheck.App

object AppColors {
    fun setThemeColor(ind: Int) {
        App.mmkv.encode("ThemeColor", ind)
    }

    fun getThemeColor(): ThemeColors? {
        val currentThemeColor = App.mmkv.decodeInt("ThemeColor", -1)
        if(currentThemeColor == -1) return null
        return colorOptionLists[currentThemeColor]
    }
    val PinkSSR = ThemeColors(
        primary = Color(0xFFE91E63),
        primaryDark = Color(0xFFE91E63),
        accent = Color(0xFFFF4081),
        material100 = Color(0xFFFF80AB),
        material300 = Color(0xFFE91E63)
    )

    val Pink = ThemeColors(
        primary = Color(0xFFE91E63),       // material_pink_500
        primaryDark = Color(0xFFC2185B),   // material_pink_700
        accent = Color(0xFFFF4081),        // material_pink_accent_200
        material100 = Color(0xFFF8BBD0),   // material_pink_100
        material300 = Color(0xFFF06292)    // material_pink_300
    )

    val Blue = ThemeColors(
        primary = Color(0xFF2196F3),       // material_blue_500
        primaryDark = Color(0xFF1976D2),   // material_blue_700
        accent = Color(0xFF448AFF),        // material_blue_accent_200
        material100 = Color(0xFFBBDEFB),   // material_blue_100
        material300 = Color(0xFF64B5F6)    // material_blue_300
    )

    val Amber = ThemeColors(
        primary = Color(0xFFFFC107),       // material_amber_500
        primaryDark = Color(0xFFFFA000),   // material_amber_700
        accent = Color(0xFFFFD740),        // material_amber_accent_200
        material100 = Color(0xFFFFECB3),   // material_amber_100
        material300 = Color(0xFFFFD54F)    // material_amber_300
    )

    val Red = ThemeColors(
        primary = Color(0xFFF44336),       // material_red_500
        primaryDark = Color(0xFFD32F2F),   // material_red_700
        accent = Color(0xFFFF5252),        // material_red_accent_200
        material100 = Color(0xFFFFCDD2),   // material_red_100
        material300 = Color(0xFFE57373)    // material_red_300
    )

    val Green = ThemeColors(
        primary = Color(0xFF4CAF50),       // material_green_500
        primaryDark = Color(0xFF388E3C),   // material_green_700
        accent = Color(0xFF69F0AE),        // material_green_accent_200
        material100 = Color(0xFFC8E6C9),   // material_green_100
        material300 = Color(0xFF81C784)    // material_green_300
    )

    val Black = ThemeColors(
        primary = Color(0xFF000000),
        primaryDark = Color(0xFF000000),
        accent = Color(0xFF000000),
        material100 = Color(0xFF000000),
        material300 = Color(0xFF000000),
        onPrimarySurface = Color.White
    )

    val Teal = ThemeColors(
        primary = Color(0xFF009688),       // material_teal_500
        primaryDark = Color(0xFF00796B),   // material_teal_700
        accent = Color(0xFF64FFDA),        // material_teal_accent_200
        material100 = Color(0xFFB2DFDB),   // material_teal_100
        material300 = Color(0xFF4DB6AC)    // material_teal_300
    )
}

fun ThemeColors.toColorScheme(isDark: Boolean): ColorScheme {
    return if (isDark) {
        darkColorScheme(
            primary = this.primary,
            onPrimary = this.onPrimarySurface,
            primaryContainer = this.primaryDark,
            secondary = this.accent,
            surface = this.material100,
            background = this.material300,
        )
    } else {
        lightColorScheme(
            primary = this.primary,
            onPrimary = this.onPrimarySurface,
            primaryContainer = this.primaryDark,
            secondary = this.accent,
            surface = this.material100,
            background = this.material300,
        )
    }
}

data class ThemeColors(
    val primary: Color,
    val primaryDark: Color,
    val accent: Color,
    val material100: Color,
    val material300: Color,
    val onPrimarySurface: Color = Color.White,
    val itemShapeFillColor: Color? = null
)
