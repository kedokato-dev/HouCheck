package com.kedokato_dev.houcheck.ui.view.score.components.chart

import android.graphics.Color as AndroidColor
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.kedokato_dev.houcheck.local.entity.CourseResultEntity
import com.kedokato_dev.houcheck.network.model.CourseResult
import com.kedokato_dev.houcheck.network.model.Score
import com.kedokato_dev.houcheck.ui.view.score_list.ListScoreViewModel


@Composable
fun GradeDistributionSection(listScoreResult: List<CourseResultEntity>) {
    // We need to get the detailed course list from the Score object
    val courseList = listScoreResult?: emptyList()
    
    if (courseList.isEmpty()) {
        // If there are no courses with grades yet, show a placeholder
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
                    text = "Phân bố điểm chữ",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "Chưa có dữ liệu về điểm chữ các môn học",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
        return
    }
    
    // Count occurrences of each letter grade
    val gradeDistribution = courseList
        .filter { it.letterGrade.isNotBlank() }
        .groupBy { it.letterGrade }
        .mapValues { it.value.size }
        .toSortedMap(compareByDescending { 
            when(it) {
                "A+" -> 10
                "A" -> 9
                "B+" -> 8
                "B" -> 7
                "C+" -> 6
                "C" -> 5
                "D+" -> 4
                "D" -> 3
                "F" -> 2
                else -> 1
            }
        })
    
    val totalGradedCourses = gradeDistribution.values.sum()
    
    if (totalGradedCourses == 0) {
        // Handle case when there are no graded courses
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
                    text = "Thống kê điểm chữ môn học",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "Chưa có dữ liệu về điểm chữ của các môn học",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
        return
    }
    
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
                text = "Thống kê điểm chữ môn học",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                GradeDistributionPieChart(
                    gradeDistribution = gradeDistribution,
                    totalCourses = totalGradedCourses
                )
            }
            
            // Legend section showing total count
            Text(
                text = "Tổng số môn đã có điểm: $totalGradedCourses môn",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun GradeDistributionPieChart(
    gradeDistribution: Map<String, Int>,
    totalCourses: Int,
    animationDuration: Int = 1200
) {
    val chart = remember { mutableStateOf<PieChart?>(null) }
    
    // Map for grade colors
    val gradeColors = mapOf(
        "A+" to Color(0xFF4CAF50).toArgb(), // Dark Green
        "A" to Color(0xFF8BC34A).toArgb(), // Light Green
        "B+" to Color(0xFF03A9F4).toArgb(), // Light Blue
        "B" to Color(0xFF2196F3).toArgb(), // Blue
        "C+" to Color(0xFFFFC107).toArgb(), // Amber
        "C" to Color(0xFFFF9800).toArgb(), // Orange
        "D+" to Color(0xFFFF5722).toArgb(), // Deep Orange
        "D" to Color(0xFFE91E63).toArgb(), // Pink
        "F" to Color(0xFFF44336).toArgb(), // Red
        "P" to Color(0xFF9C27B0).toArgb(), // Purple
    )
    
    // Default color for unknown grades
    val defaultColor = Color(0xFF607D8B).toArgb() // Blue Grey
    
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            PieChart(context).apply {
                description.isEnabled = false
                isDrawHoleEnabled = true
                setHoleColor(AndroidColor.TRANSPARENT)
                holeRadius = 40f
                transparentCircleRadius = 45f
                setDrawCenterText(false)
                setDrawEntryLabels(true)
                setEntryLabelColor(AndroidColor.BLACK)
                setEntryLabelTextSize(10f)
                rotationAngle = 0f
                isRotationEnabled = true
                isHighlightPerTapEnabled = true
                
                // Configure legend
                legend.isEnabled = true
                legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
                legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                legend.orientation = Legend.LegendOrientation.VERTICAL
                legend.setDrawInside(false)
                legend.textSize = 12f
                legend.form = Legend.LegendForm.CIRCLE
                
                setExtraOffsets(5f, 10f, 40f, 5f)
                
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                
                chart.value = this
            }
        },
        update = { view ->
            // Create entries for the chart
            val entries = gradeDistribution.map { (grade, count) ->
                PieEntry(
                    count.toFloat(), 
                    grade, 
                    count
                )
            }
            
            // Configure dataset
            val dataSet = PieDataSet(entries, "").apply {
                colors = gradeDistribution.map { (grade, _) ->
                    gradeColors[grade] ?: defaultColor
                }
                setDrawIcons(false)
                sliceSpace = 2f
                selectionShift = 5f
                valueTextSize = 12f
                valueTextColor = AndroidColor.BLACK
                
                // Use custom formatter to display percentage
                valueFormatter = object : ValueFormatter() {
                    override fun getPieLabel(value: Float, pieEntry: PieEntry): String {
                        val percentage = (value / totalCourses) * 100
                        return "${percentage.toInt()}%"
                    }
                }
            }
            
            // Create data from dataset
            val data = PieData(dataSet)
            
            // Update chart
            view.data = data
            view.invalidate()
            view.animateY(animationDuration)
        }
    )
}