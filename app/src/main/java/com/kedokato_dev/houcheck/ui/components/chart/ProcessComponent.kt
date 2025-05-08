package com.kedokato_dev.houcheck.ui.view.score.components.chart

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kedokato_dev.houcheck.network.model.Score

@Composable
fun ProgressSection(score: Score) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Thống kê",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // Progress bar cho tiến độ học tập
            val progress = remember(score) {
                (score.accumulatedCredits.toString().toFloatOrNull() ?: 0f) / 150f
            }
            
            LinearProgressItem(
                label = "Tiến độ học tập",
                progress = progress,
                progressText = "${score.accumulatedCredits}/150 tín chỉ"
            )

            // Có thể thêm các thanh tiến độ khác dựa trên dữ liệu có sẵn
        }
    }
}

@Composable
fun LinearProgressItem(label: String, progress: Float, progressText: String) {
    // Chỉ khởi tạo animation state một lần
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1500),
        label = "progressAnimation"
    )

    // Chọn màu dựa trên tiến độ và lưu cache để tránh tính toán lại
    val progressColor = remember(progress) {
        when {
            progress >= 0.75f -> Color(0xFF4CAF50) // Green for excellent progress
            progress >= 0.5f -> Color(0xFF8BC34A) // Light green for good progress
            progress >= 0.25f -> Color(0xFFFFC107) // Yellow for average progress
            else -> Color(0xFFFF9800) // Orange for initial progress
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = progressText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress.coerceIn(0f, 1f))
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(progressColor)
            )
        }
    }
}