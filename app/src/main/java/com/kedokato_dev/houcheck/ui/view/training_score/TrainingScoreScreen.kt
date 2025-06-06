package com.kedokato_dev.houcheck.ui.view.training_score

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.kedokato_dev.houcheck.R
import com.kedokato_dev.houcheck.network.model.TrainingScore
import com.kedokato_dev.houcheck.ui.components.EmptyStateComponent
import com.kedokato_dev.houcheck.ui.components.LoadingComponent
import com.kedokato_dev.houcheck.ui.components.chart.ScoreBarChart
import com.kedokato_dev.houcheck.ui.theme.HNOULightBlue
import com.kedokato_dev.houcheck.ui.theme.primaryColor
import com.kedokato_dev.houcheck.ui.view.login.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingScoreScreen(navController: NavHostController) {

    val authViewModel: AuthViewModel = hiltViewModel()
    val sessionId = authViewModel.getSessionId().toString()
    val viewModel: FetchTrainingScoreViewModel = hiltViewModel()
    val fetchState by viewModel.fetchState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchTrainingScore(
            sessionId
        )
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
                        Text(
                            stringResource(R.string.training_score),
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
                        viewModel.refreshData(sessionId)
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
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            when (fetchState) {
                is FetchTrainingScoreState.Idle -> {

                    EmptyStateComponent(
                        title = stringResource(R.string.training_score_no_data),
                        buttonText = stringResource(R.string.load),
                        onButtonClick = {
                            viewModel.fetchTrainingScore(
                                sessionId
                            )
                        }
                    )
                }

                is FetchTrainingScoreState.Loading -> {
                    LoadingComponent(HNOULightBlue, stringResource(R.string.loading_data))
                }

                is FetchTrainingScoreState.Success -> {
                    val scores = (fetchState as FetchTrainingScoreState.Success).scores


                    // Sắp xếp scores theo thời gian (học kỳ/năm học) - Năm học mới nhất hiển thị trước
                    val sortedScores = scores.sortedByDescending {
                        "${it.academicYear}_${it.semester}"
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                            .background(MaterialTheme.colorScheme.background),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {

                        item {

                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = stringResource(R.string.training_score_title),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(all = 8.dp),
                            )
                        }

                        items(sortedScores) { score ->
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

                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }

                is FetchTrainingScoreState.Error -> {
                    ErrorStateSection(
                        message = (fetchState as FetchTrainingScoreState.Error).message,
                        onRetryClick = {
                            viewModel.refreshData(
                                sessionId
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
private fun TrainingScoreCard(score: TrainingScore, getRankColor: (String) -> Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
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
                    .background(HNOULightBlue),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = score.totalScore.toString().dropLast(3),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${stringResource(R.string.semester)} ${score.semester} (${score.academicYear})",
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground
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
                        text = "${stringResource(R.string.rating)}  ${score.rank}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
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
            stringResource(R.string.error),
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
                stringResource(R.string.retry),
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