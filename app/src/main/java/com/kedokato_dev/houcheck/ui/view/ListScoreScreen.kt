package com.kedokato_dev.houcheck.ui.view

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.kedokato_dev.houcheck.data.api.ApiClient
import com.kedokato_dev.houcheck.data.api.FetchListScoreService
import com.kedokato_dev.houcheck.data.model.CourseResult
import com.kedokato_dev.houcheck.data.repository.AuthRepository
import com.kedokato_dev.houcheck.data.repository.FetchListScoreRepository
import com.kedokato_dev.houcheck.database.dao.AppDatabase
import com.kedokato_dev.houcheck.ui.state.UiState
import com.kedokato_dev.houcheck.ui.viewmodel.FetchListScoreViewModel
import com.kedokato_dev.houcheck.ui.viewmodel.FetchListScoreViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScoreScreen(
    navHostController: NavHostController
) {
    val context = LocalContext.current
    val api = remember { ApiClient.instance.create(FetchListScoreService::class.java) }
    val dao = AppDatabase.buildDatabase(context).courseResultDAO()
    val repository = remember {
        FetchListScoreRepository(
            api, dao
        )
    }
    val sharedPreferences = remember {
        context.getSharedPreferences("sessionId", Context.MODE_PRIVATE)
    }
    val authRepository = remember { AuthRepository(sharedPreferences) }

    val viewModel: FetchListScoreViewModel = viewModel(
        factory = FetchListScoreViewModelFactory(repository)
    )
    val fetchState = viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchListScore(authRepository.getSessionId().toString())
    }

    val primaryColor = Color(0xFF03A9F4)
    val secondaryColor = Color(0xFF0277BD)
    val gradientColors = listOf(primaryColor, secondaryColor)

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
        },
        containerColor = Color(0xFFF8F7FC)
    ) { paddingValues ->
        when (val state = fetchState.value) {
            is UiState.Idle -> {
                Text(
                    text = "Chưa có dữ liệu.",
                    modifier = Modifier.padding(paddingValues),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            is UiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxWidth()
                        .wrapContentSize()
                )
            }
            is UiState.Success -> {
                LazyColumn(
                    modifier = Modifier.padding(paddingValues)
                ) {
                    items(state.data.size) { index ->
                        val course = state.data[index]
                        CourseResultItem(course)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
            is UiState.Error -> {
                Text(
                    text = "Lỗi: ${state.message}",
                    modifier = Modifier.padding(paddingValues),
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
fun CourseResultItem(course: CourseResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(course.courseName, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Mã môn: ${course.courseCode} | Tín chỉ: ${course.credits}")
            Text("Học kỳ: ${course.semester} - Năm học: ${course.academicYear}")
            Text("Điểm 10: ${course.score10 ?: "Chưa có"} | Điểm 4: ${course.score4 ?: "Chưa có"}")
            Text("Chữ: ${course.letterGrade.ifBlank { "Chưa có" }}")
            if (course.note.isNotBlank()) {
                Text("Ghi chú: ${course.note}")
            }
        }
    }
}

