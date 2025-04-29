package com.kedokato_dev.houcheck.ui.view

import android.content.Context
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.kedokato_dev.houcheck.data.api.ApiClient
import com.kedokato_dev.houcheck.data.api.FetchScoreService
import com.kedokato_dev.houcheck.data.model.Score
import com.kedokato_dev.houcheck.data.repository.AuthRepository
import com.kedokato_dev.houcheck.data.repository.FetchScoreRepository
import com.kedokato_dev.houcheck.database.dao.AppDatabase
import com.kedokato_dev.houcheck.ui.theme.primaryColor
import com.kedokato_dev.houcheck.ui.viewmodel.FetchScoreState
import com.kedokato_dev.houcheck.ui.viewmodel.FetchScoreViewModel
import com.kedokato_dev.houcheck.ui.viewmodel.FetchScoreViewModelFactory
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoreScreen(navHostController: NavHostController) {
    val context = LocalContext.current
    val sharedPreferences = remember {
        context.getSharedPreferences("sessionId", Context.MODE_PRIVATE)
    }

    val api = remember { ApiClient.instance.create(FetchScoreService::class.java) }
    val dao = AppDatabase.buildDatabase(context).scoreDAO()
    val repository = remember { FetchScoreRepository(api, dao) }
    val viewModel: FetchScoreViewModel = viewModel(factory = FetchScoreViewModelFactory(repository))
    val authRepository = remember { AuthRepository(sharedPreferences) }
    val fetchState by viewModel.fetchState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchScore(authRepository.getSessionId().toString())
    }

    val refreshingState = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Kết quả học tập",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 24.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navHostController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        refreshingState.value = true
                        viewModel.refreshScore(authRepository.getSessionId().toString())
                        refreshingState.value = false
                    }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Làm mới",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = primaryColor,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navHostController.navigate("list_score") },
                containerColor = primaryColor,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.List, contentDescription = "Xem danh sách điểm")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (fetchState) {
                is FetchScoreState.Idle -> {
                    EmptyStateMessage(message = "Vui lòng cung cấp Session ID để lấy điểm.")
                }

                is FetchScoreState.Loading -> {
                    LoadingState()
                }

                is FetchScoreState.Success -> {
                    val score = (fetchState as FetchScoreState.Success).scores
                    ScoreContentRedesigned(score, navHostController)
                }

                is FetchScoreState.Error -> {
                    val errorMessage = (fetchState as FetchScoreState.Error).message
                    ErrorState(errorMessage)
                }
            }

            // Show loading overlay when refreshing
            if (refreshingState.value) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = primaryColor)
                }
            }
        }
    }
}

@Composable
fun EmptyStateMessage(message: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = primaryColor.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

@Composable
fun LoadingState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(color = primaryColor)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Đang tải dữ liệu...",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun ErrorState(errorMessage: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Đã xảy ra lỗi:",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

@Composable
fun ScoreContentRedesigned(score: Score, navHostController: NavHostController) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // GPA Summary Section with Chart
        GPASummarySection(score)

        // Academic Status Section
        AcademicStatusSection(score)

        // Progress Section
        ProgressSection(score)

        Spacer(modifier = Modifier.height(72.dp)) // Space for FAB
    }
}

@Composable
fun GPASummarySection(score: Score) {
    var isAnimationPlayed by remember { mutableStateOf(false) }
    val gpa4Value = score.gpa4.toString().toFloatOrNull() ?: 0f
    val gpa10Value = score.gpa10Current.toString().toFloatOrNull() ?: 0f

    LaunchedEffect(Unit) {
        isAnimationPlayed = true
    }

    val gpa4Progress = animateFloatAsState(
        targetValue = if (isAnimationPlayed) gpa4Value / 4f else 0f,
        animationSpec = tween(1500),
        label = "gpa4Animation"
    )

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
                // GPA Chart (Thang 4)
                Box(
                    modifier = Modifier.size(140.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        // Background circle
                        drawArc(
                            color = Color.LightGray.copy(alpha = 0.3f),
                            startAngle = 120f,
                            sweepAngle = 300f,
                            useCenter = false,
                            style = Stroke(width = 20f, cap = StrokeCap.Round)
                        )

                        // Progress arc
                        drawArc(
                            color = when {
                                gpa4Value >= 3.6f -> Color(0xFF4CAF50) // Green for excellent
                                gpa4Value >= 3.0f -> Color(0xFF8BC34A) // Light green for good
                                gpa4Value >= 2.0f -> Color(0xFFFFC107) // Yellow for average
                                else -> Color(0xFFF44336) // Red for poor
                            },
                            startAngle = 120f,
                            sweepAngle = 300f * gpa4Progress.value,
                            useCenter = false,
                            style = Stroke(width = 20f, cap = StrokeCap.Round)
                        )
                    }

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
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun GPAInfoItem(label: String, value: Any, color: Color) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun AcademicStatusSection(score: Score) {
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
                text = "Trạng thái học tập",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatusItem(
                    icon = Icons.Default.Info,
                    value = score.accumulatedCredits.toString(),
                    label = "Tín chỉ tích lũy",
                    color = Color(0xFF3F51B5)
                )

                StatusItem(
                    icon = Icons.Default.Info,
                    value = score.pendingSubjects.toString(),
                    label = "Môn chờ điểm",
                    color = Color(0xFFFF9800)
                )
            }
        }
    }
}

@Composable
fun StatusItem(icon: androidx.compose.ui.graphics.vector.ImageVector, value: String, label: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(100.dp)
        )
    }
}

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

            // You could add more statistics here based on score data
            // This is a placeholder for potential additional statistics
            LinearProgressItem(
                label = "Tiến độ học tập",
                progress = (score.accumulatedCredits.toString().toFloatOrNull() ?: 0f) / 150f, // Assuming 150 credits for graduation
                progressText = "${score.accumulatedCredits}/150 tín chỉ"
            )

            // You could add more progress bars like this based on available data
        }
    }
}

@Composable
fun LinearProgressItem(label: String, progress: Float, progressText: String) {
    var isAnimationPlayed by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isAnimationPlayed = true
    }

    val animatedProgress = animateFloatAsState(
        targetValue = if (isAnimationPlayed) progress else 0f,
        animationSpec = tween(1500),
        label = "progressAnimation"
    )

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
                    .fillMaxWidth(animatedProgress.value.coerceIn(0f, 1f))
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(
                        when {
                            progress >= 0.75f -> Color(0xFF4CAF50) // Green for excellent progress
                            progress >= 0.5f -> Color(0xFF8BC34A) // Light green for good progress
                            progress >= 0.25f -> Color(0xFFFFC107) // Yellow for average progress
                            else -> Color(0xFFFF9800) // Orange for initial progress
                        }
                    )
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ScoreScreenPreview() {
    val navController = rememberNavController()
    ScoreScreen(navHostController = navController)
}