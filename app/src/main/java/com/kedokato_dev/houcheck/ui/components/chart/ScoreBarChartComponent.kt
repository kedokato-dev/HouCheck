package com.kedokato_dev.houcheck.ui.components.chart

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kedokato_dev.houcheck.network.model.TrainingScore
import kotlinx.coroutines.launch

@Composable
fun ScoreBarChart(
    scores: List<TrainingScore>,
    modifier: Modifier = Modifier
) {
    if (scores.isEmpty()) return

    val textMeasurer = rememberTextMeasurer()
    
    // Animation progress
    val animationProgress = remember { Animatable(0f) }
    
    // Tạo animation khi component được khởi tạo
    LaunchedEffect(key1 = scores) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 1000,
                easing = FastOutSlowInEasing
            )
        )
    }

    // Tìm điểm cao nhất để làm mốc
    val maxScore = scores.maxOfOrNull { it.totalScore.toFloatOrNull() ?: 0f } ?: 100f
    // Áp dụng padding cho biểu đồ
    val paddingSpace = 40f
    // Tăng thêm padding bên dưới để hiển thị nhãn học kỳ rõ hơn
    val bottomExtraPadding = 40f
    // Thêm padding phía trên để có không gian hiển thị điểm số trên đầu cột
    val topExtraPadding = 25f

    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val usableHeight = canvasHeight - paddingSpace * 2 - bottomExtraPadding - topExtraPadding
        val usableWidth = canvasWidth - paddingSpace * 2

        val barWidth = (usableWidth / scores.size) * 0.6f
        val spaceWidth = (usableWidth / scores.size) * 0.4f

        // Vẽ trục tung (trục Y) - điều chỉnh vị trí cho phù hợp với padding phía trên
        drawLine(
            color = Color.Gray,
            start = Offset(paddingSpace, paddingSpace + topExtraPadding),
            end = Offset(paddingSpace, canvasHeight - paddingSpace - bottomExtraPadding),
            strokeWidth = 2f,
            cap = StrokeCap.Round
        )

        // Vẽ trục hoành (trục X)
        drawLine(
            color = Color.Gray,
            start = Offset(paddingSpace, canvasHeight - paddingSpace - bottomExtraPadding),
            end = Offset(
                canvasWidth - paddingSpace / 2,
                canvasHeight - paddingSpace - bottomExtraPadding
            ),
            strokeWidth = 2f,
            cap = StrokeCap.Round
        )

        // Vẽ các mốc giá trị trục Y
        val yStep = 20f
        for (score in 0..100 step 20) {
            val y = canvasHeight - paddingSpace - bottomExtraPadding - (score / 100f) * usableHeight

            // Vẽ đường ngang mốc với hiệu ứng chấm chấm
            drawDashedLine(
                color = Color.LightGray,
                start = Offset(paddingSpace, y),
                end = Offset(canvasWidth - paddingSpace / 2, y),
                strokeWidth = 1f,
                dashLength = 6f,
                gapLength = 4f
            )

            // Vẽ số mốc
            drawText(
                textMeasurer = textMeasurer,
                text = score.toString(),
                topLeft = Offset(paddingSpace - 25f, y - 10),
                style = TextStyle(
                    color = Color.Gray,
                    fontSize = 10.sp
                )
            )
        }

        // Vẽ các cột điểm
        scores.forEachIndexed { index, score ->
            val scoreValue = score.totalScore.toFloatOrNull() ?: 0f
            val normalizedScore = scoreValue / maxScore
            val currentBarHeight = normalizedScore * usableHeight * animationProgress.value

            val x = paddingSpace + index * (barWidth + spaceWidth) + spaceWidth / 2
            val y = canvasHeight - paddingSpace - bottomExtraPadding - currentBarHeight

            // Xác định màu cột dựa trên xếp loại
            val barColorTop = when (score.rank) {
                "Xuất sắc" -> Color(0xFF4CAF50)
                "Tốt" -> Color(0xFF8BC34A)
                "Khá" -> Color(0xFFFFC107)
                "Trung bình" -> Color(0xFFFF9800)
                else -> Color(0xFFF44336)
            }
            
            // Tạo màu đậm hơn cho gradient
            val barColorBottom = barColorTop.copy(alpha = 0.7f)
            
            // Tạo gradient cho cột
            val barBrush = Brush.verticalGradient(
                colors = listOf(barColorTop, barColorBottom),
                startY = y,
                endY = y + currentBarHeight
            )

            // Vẽ cột với góc bo tròn và gradient
            drawRoundRect(
                brush = barBrush,
                topLeft = Offset(x, y),
                size = Size(barWidth, currentBarHeight),
                cornerRadius = CornerRadius(5f, 5f)
            )
            
            // Vẽ đường viền nhẹ
            drawRoundRect(
                color = Color.White.copy(alpha = 0.3f),
                topLeft = Offset(x, y),
                size = Size(barWidth, currentBarHeight),
                cornerRadius = CornerRadius(5f, 5f),
                style = Stroke(width = 1.5f)
            )
            
            // Vẽ hiệu ứng bóng đổ trên cột (chỉ hiển thị nếu đã load xong)
//            if (animationProgress.value > 0.9f) {
//                drawCircle(
//                    color = barColorTop.copy(alpha = 0.2f),
//                    radius = barWidth * 0.4f,
//                    center = Offset(x + barWidth / 2, canvasHeight - paddingSpace - bottomExtraPadding + 3f)
//                )
//            }

            // Chỉ hiển thị nhãn điểm khi animation đã hoàn thành hơn 70%
            if (animationProgress.value >= 0.7f) {
                // Vẽ nhãn điểm trên đầu cột với hiệu ứng fade in
                val scoreText = scoreValue.toInt().toString()
                val scoreTextWidth = textMeasurer.measure(scoreText).size.width
                
                drawText(
                    textMeasurer = textMeasurer,
                    text = scoreText,
                    topLeft = Offset(x + (barWidth - scoreTextWidth) / 2, y - 22),
                    style = TextStyle(
                        color = barColorTop,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        background = Color.White.copy(alpha = 0.7f * animationProgress.value)
                    )
                )
            }

            // Vẽ nhãn học kỳ dưới cột với hiệu ứng opacity theo animation
            val label = "HK${score.semester}"
            drawText(
                textMeasurer = textMeasurer,
                text = label,
                topLeft = Offset(
                    x + barWidth / 2 - 15f,
                    canvasHeight - paddingSpace - bottomExtraPadding + 16
                ),
                style = TextStyle(
                    color = Color(0xFF333333).copy(alpha = animationProgress.value),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            )

            // Vẽ năm học nhỏ hơn bên dưới học kỳ với hiệu ứng opacity theo animation
            drawText(
                textMeasurer = textMeasurer,
                text = score.academicYear.takeLast(5),
                topLeft = Offset(
                    x + barWidth / 2 - 18f,
                    canvasHeight - paddingSpace - bottomExtraPadding + 35
                ),
                style = TextStyle(
                    color = Color(0xFF555555).copy(alpha = animationProgress.value),
                    fontSize = 10.sp
                )
            )
        }
    }
}

// Hàm hỗ trợ vẽ đường nét đứt
fun DrawScope.drawDashedLine(
    color: Color,
    start: Offset,
    end: Offset,
    strokeWidth: Float,
    dashLength: Float,
    gapLength: Float
) {
    val dx = end.x - start.x
    val dy = end.y - start.y
    val length = kotlin.math.sqrt(dx * dx + dy * dy)
    val unitX = dx / length
    val unitY = dy / length
    
    var startX = start.x
    var startY = start.y
    var drawn = 0f
    
    while (drawn < length) {
        val dashEnd = drawn + dashLength
        val endX = if (dashEnd > length) end.x else start.x + unitX * dashEnd
        val endY = if (dashEnd > length) end.y else start.y + unitY * dashEnd
        
        drawLine(
            color = color,
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = strokeWidth
        )
        
        drawn += dashLength + gapLength
        startX = start.x + unitX * drawn
        startY = start.y + unitY * drawn
    }
}