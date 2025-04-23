package com.kedokato_dev.houcheck.ui.view

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.kedokato_dev.houcheck.data.api.ApiClient
import com.kedokato_dev.houcheck.data.api.FetchTrainingScoreService
import com.kedokato_dev.houcheck.data.model.TrainingScore
import com.kedokato_dev.houcheck.data.repository.AuthRepository
import com.kedokato_dev.houcheck.data.repository.FetchTrainingScoreRepository
import com.kedokato_dev.houcheck.database.dao.AppDatabase
import com.kedokato_dev.houcheck.ui.theme.HNOUGradientColors
import com.kedokato_dev.houcheck.ui.theme.primaryColor
import com.kedokato_dev.houcheck.ui.viewmodel.FetchTrainingScoreState
import com.kedokato_dev.houcheck.ui.viewmodel.FetchTrainingScoreViewModel
import com.kedokato_dev.houcheck.ui.viewmodel.FetchTrainingScoreViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingScoreScreen(navController: NavHostController) {
    val context = LocalContext.current
    val api = remember { ApiClient.instance.create(FetchTrainingScoreService::class.java) }
    val dao = AppDatabase.buildDatabase(context).trainingScoreDAO()
    val repository = remember {
        FetchTrainingScoreRepository(
            api, dao
        )
    }
    val sharedPreferences = remember {
        context.getSharedPreferences("sessionId", Context.MODE_PRIVATE)
    }
    val authRepository = remember { AuthRepository(sharedPreferences) }

    val viewModel: FetchTrainingScoreViewModel = viewModel(
        factory = FetchTrainingScoreViewModelFactory(repository)
    )
    val fetchState by viewModel.fetchState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchTrainingScore(authRepository.getSessionId().toString())
    }


    // Màu sắc xếp loại
    val excellentColor = Color(0xFF4CAF50)
    val goodColor = Color(0xFF8BC34A)
    val averageColor = Color(0xFFFFC107)
    val belowAverageColor = Color(0xFFFF9800)
    val poorColor = Color(0xFFF44336)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Outlined.Info,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Điểm Rèn Luyện",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 24.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    // Nút làm mới điểm
                    IconButton(onClick = {
                        viewModel.fetchTrainingScore(authRepository.getSessionId().toString())
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
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
        containerColor = Color(0xFFF8F7FC)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (fetchState) {
                is FetchTrainingScoreState.Idle -> {
                    EmptyStateSection(
                        onFetchClick = {
                            viewModel.fetchTrainingScore(
                                authRepository.getSessionId().toString()
                            )
                        },
                        gradientColors = HNOUGradientColors
                    )
                }

                is FetchTrainingScoreState.Loading -> {
                    LoadingStateSection(primaryColor = primaryColor)
                }

                is FetchTrainingScoreState.Success -> {
                    val scores = (fetchState as FetchTrainingScoreState.Success).scores

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    ) {
                        // Hiển thị tổng quan điểm rèn luyện
                        val latestScore = scores.firstOrNull()
                        latestScore?.let { score ->
                            ScoreSummary(score = score)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Danh sách lịch sử điểm rèn luyện
                        Text(
                            text = "Lịch sử điểm rèn luyện",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF333333),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(scores) { score ->
                                TrainingScoreCard(
                                    score = score,
                                    getRankColor = { rank ->
                                        when (rank) {
                                            "Xuất sắc" -> excellentColor
                                            "Tốt" -> goodColor
                                            "Khá" -> averageColor
                                            "Trung bình" -> belowAverageColor
                                            else -> poorColor
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                is FetchTrainingScoreState.Error -> {
                    ErrorStateSection(
                        message = (fetchState as FetchTrainingScoreState.Error).message,
                        onRetryClick = {
                            viewModel.fetchTrainingScore(
                                authRepository.getSessionId().toString()
                            )
                        },
                        primaryColor = primaryColor
                    )
                }
            }
        }
    }
}

@Composable
private fun ScoreSummary(score: TrainingScore) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Học kỳ hiện tại",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "HK${score.semester} (${score.academicYear})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                // Hiển thị điểm rèn luyện dạng biểu tượng
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF03A9F4),
                                    Color(0xFF0288D1)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = score.totalScore.toString().dropLast(3),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                        Text(
                            text = "điểm",
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Thêm Divider để phân tách
            Divider(color = Color(0xFFEEEEEE))

            Spacer(modifier = Modifier.height(16.dp))

            // Hiển thị xếp loại
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.Info,
                        contentDescription = null,
                        tint = Color(0xFF03A9F4),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Xếp loại",
                        fontSize = 16.sp,
                        color = Color(0xFF555555)
                    )
                }

                // Hiển thị xếp loại với màu tương ứng
                val (backgroundColor, textColor) = when (score.rank) {
                    "Xuất sắc" -> Pair(Color(0xFFE8F5E9), Color(0xFF4CAF50))
                    "Tốt" -> Pair(Color(0xFFF1F8E9), Color(0xFF8BC34A))
                    "Khá" -> Pair(Color(0xFFFFF8E1), Color(0xFFFFC107))
                    "Trung bình" -> Pair(Color(0xFFFFF3E0), Color(0xFFFF9800))
                    else -> Pair(Color(0xFFFFEBEE), Color(0xFFF44336))
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(backgroundColor)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = score.rank,
                        color = textColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun TrainingScoreCard(score: TrainingScore, getRankColor: (String) -> Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Điểm số hiển thị dạng hình tròn
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE3F2FD)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = score.totalScore.toString().dropLast(3),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF0288D1)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "HK${score.semester} (${score.academicYear})",
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = Color(0xFF333333)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Hiển thị xếp loại với nhãn màu
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(getRankColor(score.rank))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Xếp loại: ${score.rank}",
                        fontSize = 14.sp,
                        color = Color(0xFF666666)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyStateSection(onFetchClick: () -> Unit, gradientColors: List<Color>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = null,
            tint = Color(0xFF03A9F4),
            modifier = Modifier.size(72.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Chưa có dữ liệu điểm rèn luyện",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Hãy tải dữ liệu để xem điểm rèn luyện của bạn",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onFetchClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = gradientColors[0]
            )
        ) {
            Text(
                "Tải dữ liệu",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun LoadingStateSection(primaryColor: Color) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            color = primaryColor,
            strokeWidth = 4.dp,
            modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Đang tải dữ liệu điểm rèn luyện...",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Gray
        )
    }
}

@Composable
private fun ErrorStateSection(message: String, onRetryClick: () -> Unit, primaryColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = null,
            tint = Color.Red.copy(alpha = 0.8f),
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Đã có lỗi xảy ra",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.Red.copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            message,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onRetryClick,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = primaryColor
            ),
            modifier = Modifier.height(52.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Refresh,
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                "Thử lại",
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TrainingScoreScreenPreview() {
    val navController = NavHostController(LocalContext.current)
    TrainingScoreScreen(navController)
}