package com.kedokato_dev.houcheck.ui.theme

import androidx.compose.ui.graphics.Color
import kotlin.math.absoluteValue

// Define a set of colors for different subjects
private val subjectColors = listOf(
    Color(0xFF6200EA),  // Deep Purple
    Color(0xFF2962FF),  // Blue
    Color(0xFF00BFA5),  // Teal
    Color(0xFFFF6D00),  // Orange
    Color(0xFFC51162),  // Pink
    Color(0xFF00C853),  // Green
    Color(0xFFAA00FF),  // Purple
    Color(0xFF6200EA),  // Deep Purple
    Color(0xFF2962FF),  // Blue
    Color(0xFF00BFA5),  // Teal
)

// Function to get a consistent color based on subject name
fun getClassCardColor(subjectName: String): Color {
    val hash = subjectName.hashCode().mod(subjectColors.size)
    return subjectColors[hash.absoluteValue]
}