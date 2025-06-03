package com.kedokato_dev.houcheck.ui.theme.appTheme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kedokato_dev.houcheck.App

object ThemeMode {
    data class ThemeState(
        val theme: ThemeMode.ThemeData,
        val version: Int = 0
    )

    enum class ThemeData {
        System, Dark, Light
    }

    fun setThemeMode(theme: ThemeData) {
        App.mmkv.encode("ThemeMode", theme.ordinal)
    }

    fun getThemeMode(): ThemeData {
        val currentTheme = App.mmkv.decodeInt("ThemeMode", ThemeData.System.ordinal)
        for (theme in ThemeData.entries) {
            if (currentTheme == theme.ordinal) return theme
        }
        return ThemeData.System
    }

    @Composable
    fun ThemePickerDialog(
        currentTheme: ThemeData,
        onDismiss: () -> Unit,
        onThemeSelected: (ThemeData) -> Unit
    ) {
        val themeOptions = listOf(
            ThemeData.System to "Giao diện hệ thống",
            ThemeData.Dark to "Chế độ tối",
            ThemeData.Light to "Chế độ sáng"
        )

        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = "Chọn giao diện",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            },
            text = {
                Surface(
                    tonalElevation = 2.dp,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        themeOptions.forEachIndexed { index, (theme, label) ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onThemeSelected(theme)
                                        onDismiss()
                                    }
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                RadioButton(
                                    selected = theme == currentTheme,
                                    onClick = {
                                        onThemeSelected(theme)
                                        onDismiss()
                                    },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = MaterialTheme.colorScheme.primary
                                    )
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (index < themeOptions.lastIndex) {
                                Divider(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    color = MaterialTheme.colorScheme.outlineVariant
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(
                        text = "Hủy",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            shape = MaterialTheme.shapes.large,
            tonalElevation = 8.dp
        )
    }
}