package com.kedokato_dev.houcheck.ui.view

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.kedokato_dev.houcheck.data.api.FetchExamScheduleService
import com.kedokato_dev.houcheck.data.model.ExamSchedule
import com.kedokato_dev.houcheck.data.repository.AuthRepository
import com.kedokato_dev.houcheck.data.repository.FetchExamScheduleRepository
import com.kedokato_dev.houcheck.database.dao.AppDatabase
import com.kedokato_dev.houcheck.ui.viewmodel.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamScheduleScreen(navController: NavHostController) {
    val context = LocalContext.current
    // Get the database and DAO
    val examScheduleDAO = AppDatabase.buildDatabase(context).examScheduleDAO()
    val api = ApiClient.instance.create(FetchExamScheduleService::class.java)
    val repository = remember { FetchExamScheduleRepository(api, examScheduleDAO) }
    val sharedPreferences = remember {
        context.getSharedPreferences("sessionId", Context.MODE_PRIVATE)
    }
    val authRepository = remember { AuthRepository(sharedPreferences) }

    val viewModel: FetchExamScheduleViewModel = viewModel(
        factory = FetchExamScheduleViewModelFactory(repository)
    )
    val fetchState by viewModel.fetchState.collectAsState()
    val scrollState = rememberScrollState()

    // Load data when the screen is first displayed
    LaunchedEffect(Unit) {
        viewModel.fetchExamSchedules(authRepository.getSessionId().toString())
    }

    val primaryColor = Color(0xFF03A9F4)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Lịch thi",
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
                            viewModel.refreshExamSchedules(authRepository.getSessionId().toString())
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
        containerColor = Color(0xFFF8F7FC)
    ) { paddingValues ->
        // Rest of your UI remains the same...
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (fetchState) {
                    is FetchExamScheduleState.Idle -> {
                        EmptyStateSection(
                            onFetchClick = {
                                viewModel.fetchExamSchedules(authRepository.getSessionId().toString())
                            },
                            primaryColor = primaryColor
                        )
                    }
                    is FetchExamScheduleState.Loading -> {
                        LoadingStateSection(primaryColor = primaryColor)
                    }
                    is FetchExamScheduleState.Success -> {
                        val schedules = (fetchState as FetchExamScheduleState.Success).schedules

                        // Chỉ hiển thị lịch thi có ngày lớn hơn ngày hiện tại
                        val currentDate = Calendar.getInstance().time
                        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

                        val filteredSchedules = schedules.filter { schedule ->
                            try {
                                val examDate = schedule.date?.let { dateFormat.parse(it) }
                                examDate != null && examDate.after(currentDate)
                            } catch (e: Exception) {
                                true // Giữ lại nếu không thể phân tích ngày
                            }
                        }

                        if (filteredSchedules.isEmpty()) {
                            Text(
                                text = "Không có lịch thi sắp tới để hiển thị",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Gray,
                                modifier = Modifier.padding(16.dp)
                            )
                        } else {
                            // Nhóm lịch thi theo ngày
                            val groupedSchedules = filteredSchedules.groupBy { it.date }

                            // Hiển thị theo từng ngày
                            groupedSchedules.forEach { (date, schedulesForDate) ->
                                // Hiển thị ngày thi
                                Text(
                                    text = "Ngày thi: $date",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0277BD),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                )

                                // Hiển thị danh sách các môn thi trong ngày đó
                                schedulesForDate.forEach { schedule ->
                                    ExamScheduleCard(schedule = schedule)
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
                                viewModel.fetchExamSchedules(authRepository.getSessionId().toString())
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
private fun ExamScheduleCard(schedule: ExamSchedule) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // Layout cha chứa thông tin buổi thi và môn thi
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Phần bên trái: Buổi thi và giờ bắt đầu
                Column(
                    modifier = Modifier
                        .weight(0.3f)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = schedule.session ?: "N/A",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0277BD)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = schedule.time ?: "N/A",
                        fontSize = 20.sp,
                        color = Color(0xFF333333)
                    )
                }

                // Đường ngăn cách giữa phần thông tin và buổi thi
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(Color(0xFF03A9F4)) // Màu xanh lam
                )

                // Phần bên phải: Thông tin môn thi
                Column(
                    modifier = Modifier
                        .weight(0.7f)
                        .padding(16.dp)
                ) {
                    // Tên môn thi (bỏ icon)
                    Text(
                        text = schedule.subject ?: "N/A",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Phòng thi với icon
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Phòng thi",
                            tint = Color(0xFF0277BD),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = schedule.room ?: "N/A",
                            fontSize = 14.sp,
                            color = Color(0xFF333333)
                        )
                    }

                    // Số báo danh với icon
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Số báo danh",
                            tint = Color(0xFF0277BD),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SBD: ${schedule.studentNumber ?: "N/A"}",
                            fontSize = 14.sp,
                            color = Color(0xFF333333)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyStateSection(onFetchClick: () -> Unit, primaryColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Chưa có dữ liệu lịch thi",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        ElevatedButton(
            onClick = onFetchClick,
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = primaryColor,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.elevatedButtonElevation(
                defaultElevation = 6.dp,
                pressedElevation = 8.dp
            ),
            modifier = Modifier.height(56.dp)
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
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            color = primaryColor,
            strokeWidth = 4.dp,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Đang tải dữ liệu lịch thi...",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray
        )
    }
}

@Composable
private fun ErrorStateSection(message: String, onRetryClick: () -> Unit, primaryColor: Color) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Đã có lỗi xảy ra",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.Red.copy(alpha = 0.8f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            message,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedButton(
            onClick = onRetryClick,
            border = ButtonDefaults.outlinedButtonBorder.copy(
                width = 2.dp,
                brush = Brush.horizontalGradient(listOf(primaryColor, primaryColor.copy(alpha = 0.7f)))
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.height(48.dp)
        ) {
            Text(
                "Thử lại",
                color = primaryColor,
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