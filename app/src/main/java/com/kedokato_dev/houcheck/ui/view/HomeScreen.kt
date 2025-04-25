package com.kedokato_dev.houcheck.ui.view

// In HomeScreen.kt

import FetchInfoStudentViewModel
import FetchState
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.kedokato_dev.houcheck.R
import com.kedokato_dev.houcheck.data.api.ApiClient
import com.kedokato_dev.houcheck.data.api.FetchInfoStudentService
import com.kedokato_dev.houcheck.data.api.FetchScoreService
import com.kedokato_dev.houcheck.data.api.FetchWeekScheduleService
import com.kedokato_dev.houcheck.data.model.ScheduleResponse
import com.kedokato_dev.houcheck.data.repository.AuthRepository
import com.kedokato_dev.houcheck.data.repository.FetchScoreRepository
import com.kedokato_dev.houcheck.data.repository.FetchStudentInfoRepository
import com.kedokato_dev.houcheck.data.repository.FetchWeekScheduleRepository
import com.kedokato_dev.houcheck.database.dao.AppDatabase
import com.kedokato_dev.houcheck.ui.state.UiState
import com.kedokato_dev.houcheck.ui.theme.HNOULightBlue
import com.kedokato_dev.houcheck.ui.theme.backgroundColor
import com.kedokato_dev.houcheck.ui.theme.primaryColor
import com.kedokato_dev.houcheck.ui.theme.secondaryColor
import com.kedokato_dev.houcheck.ui.viewmodel.FetchInfoStudentViewModelFactory
import com.kedokato_dev.houcheck.ui.viewmodel.FetchScoreState
import com.kedokato_dev.houcheck.ui.viewmodel.FetchScoreViewModel
import com.kedokato_dev.houcheck.ui.viewmodel.FetchScoreViewModelFactory
import com.kedokato_dev.houcheck.ui.viewmodel.FetchWeekScheduleViewModel
import com.kedokato_dev.houcheck.ui.viewmodel.FetchWeekScheduleViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun HomeScreen(navController: NavHostController) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val database: AppDatabase = AppDatabase.buildDatabase(context)

    val sharedPreferences = remember {
        context.getSharedPreferences("sessionId", Context.MODE_PRIVATE)
    }

    val fetchInfoStudentApi =
        remember { ApiClient.instance.create(FetchInfoStudentService::class.java) }
    val fetchScoreApi = remember { ApiClient.instance.create(FetchScoreService::class.java) }
    val studentDao = database.studentDAO()
    val scoreDao = database.scoreDAO()
    val repository = remember { FetchStudentInfoRepository(fetchInfoStudentApi, studentDao) }
    val fetchScoreRepo = remember { FetchScoreRepository(fetchScoreApi, scoreDao) }
    val authRepository = remember { AuthRepository(sharedPreferences) }

    val viewModel: FetchInfoStudentViewModel = viewModel(
        factory = FetchInfoStudentViewModelFactory(repository)
    )

    val fetchScoreViewModel: FetchScoreViewModel = viewModel(
        factory = FetchScoreViewModelFactory(fetchScoreRepo)
    )

    // Add Week Schedule ViewModel
    val fetchWeekScheduleApi = remember { ApiClient.instance.create(FetchWeekScheduleService::class.java) }
    val fetchWeekScheduleRepository = remember { FetchWeekScheduleRepository(fetchWeekScheduleApi) }
    val fetchWeekScheduleViewModel: FetchWeekScheduleViewModel = viewModel(
        factory = FetchWeekScheduleViewModelFactory(fetchWeekScheduleRepository)
    )

    val fetchState by viewModel.fetchState.collectAsState()
    val fetchScoreState by fetchScoreViewModel.fetchState.collectAsState()

    // Week Schedule State
    val weekScheduleState by fetchWeekScheduleViewModel.state.collectAsState()

    // Get today's date in the correct format
    val today = remember { Calendar.getInstance() }
    val dateFormat = remember { SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()) }
    val weekStart = remember {
        val cal = Calendar.getInstance()
        cal.time = today.time
        while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            cal.add(Calendar.DATE, -1)
        }
        cal.time
    }
    val weekRange = remember(weekStart) {
        val start = Calendar.getInstance().apply { time = weekStart }
        val end = Calendar.getInstance().apply {
            time = weekStart
            add(Calendar.DAY_OF_YEAR, 6)
        }
        "${dateFormat.format(start.time)}-${dateFormat.format(end.time)}"
    }
    val todayDateString = remember { SimpleDateFormat("dd/MM/yyyy", Locale("vi")).format(today.time) }


    LaunchedEffect(Unit) {
        viewModel.fetchStudentIfNeeded(authRepository.getSessionId().toString())
        fetchScoreViewModel.fetchScore(authRepository.getSessionId().toString())
        // Fetch the week schedule when the HomeScreen is launched
        fetchWeekScheduleViewModel.fetchWeekSchedule(authRepository.getSessionId().toString(), weekRange)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Header section với thông tin sinh viên và GPA
            ProfileHeaderSection(fetchState, fetchScoreState, primaryColor, secondaryColor)

            Spacer(modifier = Modifier.height(16.dp))

            // Thông báo quan trọng
            ImportantNoticesSection()

            Spacer(modifier = Modifier.height(16.dp))

            // Các tính năng chính
            FeaturesSection(navController, context)

            Spacer(modifier = Modifier.height(16.dp))

            // Thời khóa biểu hôm nay
            TodayScheduleSection(weekScheduleState = weekScheduleState, todayDateString = todayDateString, navController)

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ProfileHeaderSection(
    fetchState: FetchState,
    fetchScoreState: FetchScoreState,
    primaryColor: Color,
    secondaryColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        // Background gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(primaryColor, secondaryColor)
                    )
                )
        )

        // Top app bar with settings and notifications
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "My HOU",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Row {
                IconButton(onClick = { /* TODO: Navigate to notifications */ }) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Thông báo",
                        tint = Color.White
                    )
                }

                IconButton(onClick = { /* TODO: Navigate to settings */ }) {
                    Icon(
                        imageVector = Icons.Rounded.Settings,
                        contentDescription = "Cài đặt",
                        tint = Color.White
                    )
                }
            }
        }

        // Profile card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 60.dp)
                .align(Alignment.BottomCenter)
                .shadow(8.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.no_avatar),
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .border(2.dp, primaryColor, CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    when (fetchState) {
                        is FetchState.Success -> {
                            val student = fetchState.student
                            Text(
                                text = student.studentName.toString(),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )

                            Text(
                                text = "MSV: ${student.studentId}",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
//                                text = "Ngành: ${student.studentMajor ?: "Chưa có thông tin"}",
                                text = "Ngành: CNTT",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        is FetchState.Loading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = primaryColor,
                                strokeWidth = 2.dp
                            )
                        }
                        is FetchState.Error -> {
                            Text(
                                text = "Lỗi tải thông tin",
                                fontSize = 16.sp,
                                color = Color.Red
                            )
                        }
                        else -> {
                            Text(
                                text = "Chưa có thông tin",
                                fontSize = 16.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

                // GPA Section
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = primaryColor.copy(alpha = 0.1f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "GPA",
                            fontSize = 12.sp,
                            color = primaryColor
                        )

                        when (fetchScoreState) {
                            is FetchScoreState.Success -> {
                                val score = fetchScoreState.scores
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${score.gpa4}",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = primaryColor
                                    )
                                    Text(
                                        text = "/4",
                                        fontSize = 14.sp,
                                        color = primaryColor
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "🔥",
                                        fontSize = 16.sp
                                    )
                                }
                            }
                            is FetchScoreState.Loading -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                            }
                            else -> {
                                Text(
                                    text = "N/A",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = primaryColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ImportantNoticesSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Thông báo quan trọng",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            TextButton(onClick = { /* TODO: Navigate to all notices */ }) {
                Text(
                    text = "Xem tất cả",
                    color = Color(0xFF1565C0)
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFE0B2) // Màu cam nhạt cho thông báo
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.newspaper_news_svgrepo_com),
                    contentDescription = "Thông báo",
                    tint = Color(0xFFE65100),
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Lịch thi học kỳ 2 (2024-2025)",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100)
                    )

                    Text(
                        text = "Lịch thi đã được cập nhật. Vui lòng kiểm tra thông tin!",
                        fontSize = 14.sp,
                        color = Color(0xFFE65100)
                    )
                }
            }
        }
    }
}

@Composable
fun FeaturesSection(navController: NavHostController, context: Context) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Tiện ích",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        val features = listOf(
            FeatureItem("Lịch học", R.drawable.calendar_date_schedule, Color(0xFF2196F3)),
            FeatureItem("Điểm học tập", R.drawable.score_repo, Color(0xFF4CAF50)),
            FeatureItem("Thông tin cá nhân", R.drawable.info, Color(0xFF9C27B0)),
            FeatureItem("Học phí", R.drawable.pay, Color(0xFFFF9800)),
            FeatureItem("Lịch thi", R.drawable.calendar_date_exem, Color(0xFFF44336)),
            FeatureItem("Điểm rèn luyện", R.drawable.score_repo, Color(0xFF795548)),
            FeatureItem("Tin tức", R.drawable.news, Color(0xFF607D8B)),
            FeatureItem("Hỗ trợ", R.drawable.coffe_svgrepo_com, Color(0xFF009688)),
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            contentPadding = PaddingValues(0.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.height(200.dp),
            userScrollEnabled = false
        ) {
            items(features.size) { index ->
                EnhancedFeatureGridItem(features[index]) {
                    when (index) {
                        0 -> navController.navigate("week_schedule")
                        1 -> navController.navigate("score")
                        2 -> navController.navigate("studentInfo")
                        3 -> navController.navigate("home")
                        4 -> navController.navigate("exam_schedule")
                        5 -> navController.navigate("training_score")
                        6, 7 -> Toast.makeText(
                            context,
                            "Chức năng đang phát triển",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}

@Composable
fun EnhancedFeatureGridItem(item: FeatureItem, onClick: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(56.dp)
                .background(
                    color = item.backgroundColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(8.dp)
        ) {
            Image(
                painter = painterResource(id = item.iconRes),
                contentDescription = item.title,
                modifier = Modifier.size(28.dp),
                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(item.backgroundColor)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = item.title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            color = Color(0xFF333333),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(64.dp)
        )
    }
}

// In HomeScreen.kt

@Composable
fun TodayScheduleSection(weekScheduleState: UiState<ScheduleResponse>, todayDateString: String, navHostController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Lịch học hôm nay",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            TextButton(onClick = {
                navHostController.navigate("week_schedule")
            }) {
                Text(
                    text = "Xem tất cả",
                    color = Color(0xFF1565C0)
                )
            }
        }

        // Schedule content based on the state
        when (weekScheduleState) {
            is UiState.Loading -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
            is UiState.Error -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
                        Text("Error loading schedule")
                    }
                }
            }
            is UiState.Success -> {
                val schedule = weekScheduleState.data
                //Find the correct day
                val todaySchedule = schedule.weekDays.find { it.contains(todayDateString) }?.let { day ->
                    val dayKey = day.dropLast(12)
                    schedule.byDays[dayKey]
                }

                if (todaySchedule?.classes?.isNotEmpty() == true) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            todaySchedule.classes.forEach { classInfo ->
                                ScheduleItem(
                                    status = classInfo.session,
                                    session = classInfo.timeSlot,
                                    subject = classInfo.subject,
                                    room = classInfo.room,
                                    backgroundColor = getClassStatusColor(classInfo.session).copy(alpha = 0.1f) // You'll need to define this function or adapt it
                                )
                                if (todaySchedule.classes.indexOf(classInfo) < todaySchedule.classes.size - 1) {
                                    Divider(modifier = Modifier.padding(horizontal = 16.dp))
                                }
                            }
                        }
                    }
                } else {
                    // Empty state
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
                            Text("Không có lịch học hôm nay")
                        }
                    }
                }
            }
            else -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
                        Text("Không có dữ liệu")
                    }
                }
            }
        }
    }
}

// New function to get color based on class status
private fun getClassStatusColor(session: String): Color {
    return when {
        session.contains("bù") -> Color(0xFF9C27B0)
        session.contains("Nghỉ") -> Color(0xFFF44336)
        else -> Color(0xFF4CAF50)
    }
}

@Composable
fun ScheduleItem(status: String,session: String, subject: String, room: String, backgroundColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(backgroundColor, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = session,
                fontWeight = FontWeight.Bold,
                color = backgroundColor.copy(alpha = 10f)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = subject,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.status),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = HNOULightBlue
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = status,
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.width(16.dp))

                Icon(
                    painter = painterResource(id = R.drawable.room_key), // Thay thế bằng icon location
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = HNOULightBlue
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = room,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

data class FeatureItem(
    val title: String,
    val iconRes: Int,
    val backgroundColor: Color
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
//    AppTheme {
//        val navController = NavHostController(context = LocalContext.current)
//        HomeScreen(navController)
//    }
}