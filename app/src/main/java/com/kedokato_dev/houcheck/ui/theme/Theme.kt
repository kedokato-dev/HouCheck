package com.kedokato_dev.houcheck.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.kedokato_dev.houcheck.ui.theme.appTheme.ThemeMode
import com.kedokato_dev.houcheck.ui.theme.colorTheme.AppColors
import com.kedokato_dev.houcheck.ui.theme.colorTheme.ThemeColors
import com.kedokato_dev.houcheck.ui.theme.colorTheme.toColorScheme

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

fun createCustomColorScheme(colors: ThemeColors, isDark: Boolean) =
    (if (isDark) darkColorScheme() else lightColorScheme()).copy(
        primary = colors.primary,
        secondary = colors.accent,
        tertiary = colors.material300,
        onPrimary = colors.onPrimarySurface
    )

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun HouCheckTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    themeSelected: ThemeMode.ThemeData = ThemeMode.ThemeData.System,
    themeColor: ThemeColors? = null,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        themeColor != null -> {
            themeColor.toColorScheme(if (themeSelected == ThemeMode.ThemeData.System) darkTheme else if (themeSelected == ThemeMode.ThemeData.Dark) true else false)
        }

        themeSelected != ThemeMode.ThemeData.System -> {
            when (themeSelected) {
                ThemeMode.ThemeData.Dark -> DarkColorScheme
                else -> LightColorScheme
            }
        }

        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    MaterialTheme(
        colorScheme = colorScheme.copy(),
        typography = Typography,
        content = content
    )
}