package com.kedokato_dev.houcheck.ui.view

import android.content.Context
import android.util.Log
import android.widget.Button
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
        Log.d("ScoreScreen", "Session ID: ${authRepository.getSessionId()}")
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Kết quả học tập",
                        fontSize = 20.sp, // fontSize nhỏ lại cho phù hợp với SmallTopAppBar
                        fontWeight = FontWeight.Bold,
                        lineHeight = 24.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navHostController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Quay lại",
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            Button(
                onClick = {
                    navHostController.navigate("list_score")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Default.List,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Xem danh sách điểm")
            }

            Column {
                when (fetchState) {
                    is FetchScoreState.Idle -> {
                        Text("Vui lòng cung cấp Session ID để lấy điểm.")
                    }

                    is FetchScoreState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    }

                    is FetchScoreState.Success -> {
                        val score = (fetchState as FetchScoreState.Success).scores
                        ScoreContent(score)
                    }

                    is FetchScoreState.Error -> {
                        val errorMessage = (fetchState as FetchScoreState.Error).message
                        Text("Lỗi: $errorMessage", color = MaterialTheme.colorScheme.error)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))


            }
        }


    }
}

@Composable
fun ScoreContent(score: Score) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        ScoreCard(
            title = "GPA (Thang 4)",
            value = score.gpa4,
            icon = Icons.Default.Info
        )
        ScoreCard(
            title = "Học lực",
            value = score.academicRank4,
            icon = Icons.Default.Info
        )
        ScoreCard(
            title = "GPA hiện tại (Thang 10)",
            value = score.gpa10Current,
            icon = Icons.Default.Info
        )
        ScoreCard(
            title = "Số tín chỉ tích lũy",
            value = score.accumulatedCredits,
            icon = Icons.Default.List
        )
        ScoreCard(
            title = "Số môn chờ điểm",
            value = score.pendingSubjects,
            icon = Icons.Default.List
        )
    }
}

@Composable
fun ScoreCard(title: String, value: Any, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary
            )
            Column {
                Text(text = title, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = value.toString(),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ScoreScreenPreview() {
    val navController = NavHostController(LocalContext.current)
    ScoreScreen(navHostController = navController)
}
