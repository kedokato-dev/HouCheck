package com.kedokato_dev.houcheck.ui.view.score

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.kedokato_dev.houcheck.local.dao.AppDatabase
import com.kedokato_dev.houcheck.local.entity.CourseResultEntity
import com.kedokato_dev.houcheck.network.api.ApiClient
import com.kedokato_dev.houcheck.network.api.ListScoreService
import com.kedokato_dev.houcheck.network.api.ScoreService
import com.kedokato_dev.houcheck.network.model.CourseResult
import com.kedokato_dev.houcheck.network.model.Score
import com.kedokato_dev.houcheck.repository.AuthRepository
import com.kedokato_dev.houcheck.repository.ListScoreRepository
import com.kedokato_dev.houcheck.repository.ScoreRepository
import com.kedokato_dev.houcheck.ui.components.EmptyStateComponent
import com.kedokato_dev.houcheck.ui.components.ErrorComponent
import com.kedokato_dev.houcheck.ui.components.LoadingComponent
import com.kedokato_dev.houcheck.ui.state.UiState
import com.kedokato_dev.houcheck.ui.theme.HNOULightBlue
import com.kedokato_dev.houcheck.ui.theme.primaryColor
import com.kedokato_dev.houcheck.ui.view.score.components.chart.AcademicStatusSection
import com.kedokato_dev.houcheck.ui.view.score.components.chart.GPASummarySection
import com.kedokato_dev.houcheck.ui.view.score.components.chart.GradeDistributionSection
import com.kedokato_dev.houcheck.ui.view.score.components.chart.ProgressSection
import com.kedokato_dev.houcheck.ui.view.score_list.ListScoreViewModel
import com.kedokato_dev.houcheck.ui.view.score_list.ListScoreViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoreScreen(navHostController: NavHostController) {
    val context = LocalContext.current
    val sharedPreferences = remember {
        context.getSharedPreferences("sessionId", Context.MODE_PRIVATE)
    }

    val listScoreApi = remember { ApiClient.instance.create(ListScoreService::class.java) }
    val courseResultDAO = AppDatabase.buildDatabase(context).courseResultDAO()
    val listScoreRepository = remember {
        ListScoreRepository(
            listScoreApi, courseResultDAO
        )
    }

    val listScoreViewModel: ListScoreViewModel = viewModel(
        factory = ListScoreViewModelFactory(listScoreRepository)
    )

    val db = AppDatabase.buildDatabase(context)


    // Lấy dữ liệu điểm từ ViewModel
    val listScoreState by listScoreViewModel.state.collectAsState()

    val api = remember { ApiClient.instance.create(ScoreService::class.java) }
    val dao = AppDatabase.buildDatabase(context).scoreDAO()
    val repository = remember { ScoreRepository(api, dao) }
    val viewModel: FetchScoreViewModel = viewModel(factory = ScoreViewModelFactory(repository))
    val authRepository = remember { AuthRepository(sharedPreferences) }
    val fetchState by viewModel.fetchState.collectAsState()

    // Theo dõi trạng thái refresh
    val isRefreshing = remember { mutableStateOf(false) }
    
    // Hàm refresh data
    val refreshData = {
        isRefreshing.value = true
        viewModel.refreshScore(authRepository.getSessionId().toString())
        isRefreshing.value = false
    }

    LaunchedEffect(Unit) {
        viewModel.fetchScore(authRepository.getSessionId().toString())
        listScoreViewModel.fetchListScore(authRepository.getSessionId().toString())
    }

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
                    IconButton(onClick = refreshData) {
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
                    EmptyStateComponent(
                      onButtonClick = {
                          viewModel.fetchScore(authRepository.getSessionId().toString())
                      },
                    )
                }

                is FetchScoreState.Loading -> {
                    LoadingComponent(HNOULightBlue, "Đang tải dữ liệu...")
                }

                is FetchScoreState.Success -> {
                    val score = (fetchState as FetchScoreState.Success).scores
                    val courseList = when (listScoreState) {
                        is UiState.Success -> (listScoreState as UiState.Success<List<CourseResult>>).data
                            .map { courseResult ->
                                CourseResultEntity(
                                    semester = courseResult.semester,
                                    academicYear = courseResult.academicYear,
                                    courseCode = courseResult.courseCode,
                                    courseName = courseResult.courseName,
                                    credits = courseResult.credits,
                                    score10 = courseResult.score10,
                                    score4 = courseResult.score4,
                                    letterGrade = courseResult.letterGrade,
                                    notCounted = courseResult.notCounted,
                                    note = courseResult.note,
                                    detailLink = courseResult.detailLink
                                )
                            }
                        else -> emptyList()
                    }
                    ScoreContentRedesigned(score, navHostController, courseList)
                }

                is FetchScoreState.Error -> {
                    val errorMessage = (fetchState as FetchScoreState.Error).message
                    ErrorComponent(errorMessage)
                }
            }

            // Show loading overlay when refreshing
            if (isRefreshing.value) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                   LoadingComponent(HNOULightBlue, "Đang làm mới dữ liệu...")
                }
            }
        }
    }
}

@Composable
fun ScoreContentRedesigned(score: Score, navHostController: NavHostController, listScore: List<CourseResultEntity>) {
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

        GradeDistributionSection(
            listScore
        )

        // Progress Section
        ProgressSection(score)

        Spacer(modifier = Modifier.height(72.dp)) // Space for FAB
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ScoreScreenPreview() {
    val navController = rememberNavController()
    ScoreScreen(navHostController = navController)
}