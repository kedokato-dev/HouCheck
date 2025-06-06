package com.kedokato_dev.houcheck.ui.view.score.components.chart

import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.kedokato_dev.houcheck.network.model.Score

@Composable
fun GPASummarySection(score: Score) {
    // Memoize values to prevent recalculations during recomposition
    val (gpa4Value, gpa10Value, chartColor) = remember(score) {
        val gpa4 = score.gpa4.toString().toFloatOrNull() ?: 0f
        val gpa10 = score.gpa10Current.toString().toFloatOrNull() ?: 0f
        
        val color = when {
            gpa4 >= 3.6f -> Color(0xFF4CAF50) // Green for excellent
            gpa4 >= 3.0f -> Color(0xFF8BC34A) // Light green for good
            gpa4 >= 2.0f -> Color(0xFFFFC107) // Yellow for average
            else -> Color(0xFFF44336) // Red for poor
        }
        
        Triple(gpa4, gpa10, color)
    }
    
    // Mark whether animation has played
    var isAnimationPlayed by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        isAnimationPlayed = true
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
                text = "Điểm trung bình",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // MPAndroidChart PieChart cho GPA
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    GPAPieChart(
                        gpaValue = gpa4Value,
                        maxValue = 4f,
                        chartColor = chartColor.toArgb(),
                        animationDuration = 1500
                    )
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = score.gpa4.toString(),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Thang 4",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    GPAInfoItem(
                        label = "Học lực",
                        value = score.academicRank4,
                        color = when (score.academicRank4.toString().lowercase()) {
                            "xuất sắc" -> Color(0xFF4CAF50)
                            "giỏi" -> Color(0xFF8BC34A)
                            "khá" -> Color(0xFFFFC107)
                            else -> Color(0xFFFF9800)
                        }
                    )

                    GPAInfoItem(
                        label = "GPA (Thang 10)",
                        value = score.gpa10Current,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}

@Composable
fun GPAPieChart(
    gpaValue: Float,
    maxValue: Float = 4.0f,
    chartColor: Int,
    animationDuration: Int = 1000
) {
    // Using remember to store the chart instance to avoid creating a new one on each recomposition
    val chart = remember { mutableStateOf<PieChart?>(null) }
    
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            PieChart(context).apply {
                description.isEnabled = false
                isDrawHoleEnabled = true
                setHoleColor(android.graphics.Color.TRANSPARENT)
                holeRadius = 75f
                transparentCircleRadius = 0f
                setDrawCenterText(false)
                setDrawEntryLabels(false)
                rotationAngle = 270f
                isRotationEnabled = false
                isHighlightPerTapEnabled = false
                legend.isEnabled = false
                setTouchEnabled(false)
                setExtraOffsets(0f, 0f, 0f, 0f)
                
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                
                chart.value = this
            }
        },
        update = { view ->
            // Create entries for the chart
            val entries = ArrayList<PieEntry>().apply {
                add(PieEntry(gpaValue, "GPA"))
                add(PieEntry(maxValue - gpaValue, ""))
            }
            
            // Configure dataset
            val dataSet = PieDataSet(entries, "").apply {
                colors = listOf(chartColor, android.graphics.Color.LTGRAY)
                setDrawIcons(false)
                sliceSpace = 3f
                selectionShift = 0f
                valueTextSize = 0f
            }
            
            // Create data from dataset
            val data = PieData(dataSet).apply {
                setDrawValues(false)
            }
            
            // Update chart
            view.data = data
            view.invalidate() 
            view.animateY(animationDuration)
        }
    )
}

@Composable
fun GPAInfoItem(label: String, value: Any, color: Color) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}