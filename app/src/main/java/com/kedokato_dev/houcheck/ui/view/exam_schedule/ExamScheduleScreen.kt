package com.kedokato_dev.houcheck.ui.view.exam_schedule

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.kedokato_dev.houcheck.network.api.ApiClient
import com.kedokato_dev.houcheck.network.api.ExamScheduleService
import com.kedokato_dev.houcheck.network.model.ExamSchedule
import com.kedokato_dev.houcheck.repository.AuthRepository
import com.kedokato_dev.houcheck.repository.ExamScheduleRepository
import com.kedokato_dev.houcheck.local.dao.AppDatabase
import com.kedokato_dev.houcheck.ui.components.EmptyStateComponent
import com.kedokato_dev.houcheck.ui.components.LoadingComponent
import com.kedokato_dev.houcheck.ui.theme.HNOUDarkBlue
import com.kedokato_dev.houcheck.ui.view.login.AuthViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamScheduleScreen(navController: NavHostController) {
    val context = LocalContext.current

    val authViewModel: AuthViewModel = hiltViewModel()
    val sessionId = authViewModel.getSessionId().toString()

    val viewModel: FetchExamScheduleViewModel = hiltViewModel()
    val fetchState by viewModel.fetchState.collectAsState()
    val scrollState = rememberScrollState()

    // Load data when the screen is first displayed
    LaunchedEffect(Unit) {
        viewModel.fetchExamSchedules(sessionId)
    }

    // Theme colors
    val primaryColor = Color(0xFF03A9F4)
    val secondaryColor = Color(0xFF0277BD)
    val backgroundColor = Color(0xFFF8F7FC)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Lịch Thi",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 24.sp
                    )
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
                    // Add refresh button here
                    IconButton(
                        onClick = {
                            viewModel.refreshExamSchedules(sessionId)
                        }
                    ) {
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
        containerColor = backgroundColor
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (fetchState) {
                    is FetchExamScheduleState.Idle -> {
                        EmptyStateComponent(
                            title = "Chưa có lịch thi",
                            subtitle = "Bấm nút bên dưới để tải lịch thi",
                            buttonText = "Tải lịch thi",
                            onButtonClick = {
                                viewModel.fetchExamSchedules(
                                   sessionId
                                )
                            },
                            primaryColor = HNOUDarkBlue,
                        )
                    }
                    is FetchExamScheduleState.Loading -> {
                        LoadingComponent(primaryColor = primaryColor,
                            title = "Đang tải lịch thi",)
                    }
                    is FetchExamScheduleState.Success -> {
                        val schedules = (fetchState as FetchExamScheduleState.Success).schedules

                        val currentDate = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, 0)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }.time // Lấy ngày hiện tại và reset giờ về 00:00

                        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

                        val filteredSchedules = schedules.filter { schedule ->
                            try {
                                val examDate = schedule.date?.let { dateFormat.parse(it) }
                                examDate != null && (examDate.after(currentDate) || examDate == currentDate)
                            } catch (e: Exception) {
                                true // Giữ lại nếu không thể phân tích ngày
                            }
                        }

                        if (filteredSchedules.isEmpty()) {
                            NoExamsSection(primaryColor)
                        } else {
                            val groupedSchedules = filteredSchedules.groupBy { it.date }

                            groupedSchedules.forEach { (date, schedulesForDate) ->
                                DateHeader(
                                    date = date ?: "Không có ngày",
                                    primaryColor = secondaryColor
                                )

                                schedulesForDate.forEach { schedule ->
                                    EnhancedExamScheduleCard(
                                        schedule = schedule,
                                        primaryColor = primaryColor,
                                        secondaryColor = secondaryColor
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }

                    is FetchExamScheduleState.Error -> {
                        ErrorStateSection(
                            message = (fetchState as FetchExamScheduleState.Error).message,
                            onRetryClick = {
                                viewModel.fetchExamSchedules(sessionId)
                            },
                            primaryColor = primaryColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DateHeader(date: String, primaryColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.DateRange,
            contentDescription = "Ngày thi",
            tint = primaryColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Ngày thi: $date",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = primaryColor
        )
    }
    Divider(
        color = primaryColor.copy(alpha = 0.3f),
        thickness = 1.dp,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun EnhancedExamScheduleCard(
    schedule: ExamSchedule,
    primaryColor: Color,
    secondaryColor: Color
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Time section with gradient background
            Column(
                modifier = Modifier
                    .weight(0.3f)
                    .fillMaxHeight()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                primaryColor,
                                secondaryColor
                            )
                        )
                    )
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = schedule.session ?: "N/A",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = schedule.time ?: "N/A",
                    fontSize = 18.sp,
                    color = Color.White
                )
            }

            // Subject information
            Column(
                modifier = Modifier
                    .weight(0.7f)
                    .padding(16.dp)
            ) {
                // Subject name
                Text(
                    text = schedule.subject ?: "N/A",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Room with icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Phòng thi",
                        tint = secondaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Phòng: ${schedule.room ?: "N/A"}",
                        fontSize = 15.sp,
                        color = Color(0xFF555555)
                    )
                }

                // Student number with icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Số báo danh",
                        tint = secondaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SBD: ${schedule.studentNumber ?: "N/A"}",
                        fontSize = 15.sp,
                        color = Color(0xFF555555)
                    )
                }
            }
        }
    }
}




@Composable
private fun NoExamsSection(primaryColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon for no exams
        Icon(
            imageVector = Icons.Default.DateRange,
            contentDescription = "Không có lịch thi",
            tint = primaryColor.copy(alpha = 0.7f),
            modifier = Modifier
                .size(80.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            "Chưa có lịch thi sắp tới",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            color = Color(0xFF555555)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Bạn sẽ thấy lịch thi khi có lịch mới",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

@Composable
private fun ErrorStateSection(message: String, onRetryClick: () -> Unit, primaryColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Error icon
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(40.dp))
                .background(Color.Red.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "!",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Red.copy(alpha = 0.8f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Đã có lỗi xảy ra",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Red.copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            message,
            fontSize = 15.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRetryClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = primaryColor,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.height(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Thử lại",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Thử lại",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ExamScheduleScreenPreview() {
    val navController = NavHostController(LocalContext.current)
    ExamScheduleScreen(navController)
}