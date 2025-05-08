package com.kedokato_dev.houcheck.ui.components.chart

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import com.kedokato_dev.houcheck.network.model.TrainingScore

@Composable
fun ScoreBarChart(
    scores: List<TrainingScore>,
    modifier: Modifier = Modifier
) {
    if (scores.isEmpty()) return

    val textMeasurer = rememberTextMeasurer()

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

            // Vẽ đường ngang mốc
            drawLine(
                color = Color.LightGray,
                start = Offset(paddingSpace, y),
                end = Offset(canvasWidth - paddingSpace / 2, y),
                strokeWidth = 1f,
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
            val barHeight = normalizedScore * usableHeight

            val x = paddingSpace + index * (barWidth + spaceWidth) + spaceWidth / 2
            val y = canvasHeight - paddingSpace - bottomExtraPadding - barHeight

            // Xác định màu cột dựa trên xếp loại
            val barColor = when (score.rank) {
                "Xuất sắc" -> Color(0xFF4CAF50)
                "Tốt" -> Color(0xFF8BC34A)
                "Khá" -> Color(0xFFFFC107)
                "Trung bình" -> Color(0xFFFF9800)
                else -> Color(0xFFF44336)
            }

            // Vẽ cột
            drawRect(
                color = barColor,
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight)
            )

            // Vẽ nhãn điểm trên đầu cột - đẩy cao hơn để không bị cột che khuất
            val scoreText = scoreValue.toInt().toString()
            val scoreTextWidth = textMeasurer.measure(scoreText).size.width

            drawText(
                textMeasurer = textMeasurer,
                text = scoreText,
                topLeft = Offset(x + (barWidth - scoreTextWidth) / 2, y - 22), // Tăng khoảng cách lên trên
                style = TextStyle(
                    color = barColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    background = Color.White.copy(alpha = 0.7f) // Thêm nền mờ để dễ đọc
                )
            )

            // Vẽ nhãn học kỳ dưới cột - giữ nguyên vì đã tốt
            val label = "HK${score.semester}"
            drawText(
                textMeasurer = textMeasurer,
                text = label,
                topLeft = Offset(
                    x + barWidth / 2 - 15f,
                    canvasHeight - paddingSpace - bottomExtraPadding + 16
                ),
                style = TextStyle(
                    color = Color(0xFF333333),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            )

            // Vẽ năm học nhỏ hơn bên dưới học kỳ - giữ nguyên vì đã tốt
            drawText(
                textMeasurer = textMeasurer,
                text = score.academicYear.takeLast(5),
                topLeft = Offset(
                    x + barWidth / 2 - 18f,
                    canvasHeight - paddingSpace - bottomExtraPadding + 35
                ),
                style = TextStyle(
                    color = Color(0xFF555555),
                    fontSize = 10.sp
                )
            )
        }
    }
}