package com.kedokato_dev.houcheck.ui.view.home

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
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.kedokato_dev.houcheck.R
import com.kedokato_dev.houcheck.network.model.ScheduleResponse
import com.kedokato_dev.houcheck.ui.state.UiState
import com.kedokato_dev.houcheck.ui.theme.HNOULightBlue
import com.kedokato_dev.houcheck.ui.theme.HouCheckTheme
import com.kedokato_dev.houcheck.ui.theme.backgroundColor
import com.kedokato_dev.houcheck.ui.theme.primaryColor
import com.kedokato_dev.houcheck.ui.theme.secondaryColor
import com.kedokato_dev.houcheck.ui.view.login.AuthViewModel
import com.kedokato_dev.houcheck.ui.view.profile.FetchState
import com.kedokato_dev.houcheck.ui.view.profile.InfoStudentViewModel
import com.kedokato_dev.houcheck.ui.view.score.FetchScoreState
import com.kedokato_dev.houcheck.ui.view.score.FetchScoreViewModel
import com.kedokato_dev.houcheck.ui.view.week_schedule.WeekScheduleViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// HomeScreenContainer để quản lý ViewModel và truyền vào HomeScreen
@Composable
fun HomeScreenContainer(navController: NavHostController) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val fetchInfoViewModel: InfoStudentViewModel = hiltViewModel()
    val fetchScoreViewModel: FetchScoreViewModel = hiltViewModel()
    val fetchWeekScheduleViewModel: WeekScheduleViewModel = hiltViewModel()

    HomeScreen(
        navController = navController,
        authViewModel = authViewModel,
        fetchInfoViewModel = fetchInfoViewModel,
        fetchScoreViewModel = fetchScoreViewModel,
        fetchWeekScheduleViewModel = fetchWeekScheduleViewModel
    )
}

@Composable
fun HomeScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    fetchInfoViewModel: InfoStudentViewModel,
    fetchScoreViewModel: FetchScoreViewModel,
    fetchWeekScheduleViewModel: WeekScheduleViewModel
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    val sessionId = authViewModel.getSessionId() ?: ""
    val fetchState by fetchInfoViewModel.fetchState.collectAsState()
    val fetchScoreState by fetchScoreViewModel.fetchState.collectAsState()
    val weekScheduleState by fetchWeekScheduleViewModel.state.collectAsState()

    var isRefreshing by remember { mutableStateOf(false) }

    // LaunchedEffect với key phù hợp để tránh fetch dữ liệu không cần thiết
    LaunchedEffect(key1 = sessionId) {
        if (sessionId.isNotEmpty()) {
            fetchInfoViewModel.fetchStudentIfNeeded(sessionId)
            fetchScoreViewModel.fetchScore(sessionId)
            fetchWeekScheduleViewModel.fetchWeekSchedule(sessionId, getWeekRange())
        }
    }

    // Hàm làm mới dữ liệu với coroutines scope
    fun refreshData() {
        coroutineScope.launch {
            isRefreshing = true
            try {
                fetchInfoViewModel.fetchStudentIfNeeded(sessionId)
                fetchScoreViewModel.fetchScore(sessionId)
                fetchWeekScheduleViewModel.fetchWeekSchedule(sessionId, getWeekRange())
            } finally {
                isRefreshing = false
            }
        }
    }

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = { refreshData() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp)
            ) {
                ProfileHeaderSection(fetchState, fetchScoreState, primaryColor, secondaryColor, navController)
                Spacer(modifier = Modifier.height(16.dp))
                ImportantNoticesSection(navController)
                Spacer(modifier = Modifier.height(16.dp))
                FeaturesSection(navController, context)
                Spacer(modifier = Modifier.height(16.dp))
                TodayScheduleSection(weekScheduleState, getTodayDateString(), navController)
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// Hàm hỗ trợ tính toán ngày
private fun getWeekRange(): String {
    val today = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    val weekStart = Calendar.getInstance().apply {
        time = today.time
        while (get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            add(Calendar.DATE, -1)
        }
    }
    val weekEnd = Calendar.getInstance().apply {
        time = weekStart.time
        add(Calendar.DAY_OF_YEAR, 6)
    }
    return "${dateFormat.format(weekStart.time)}-${dateFormat.format(weekEnd.time)}"
}

private fun getTodayDateString(): String {
    return SimpleDateFormat("dd/MM/yyyy", Locale("vi")).format(Calendar.getInstance().time)
}

@Composable
fun ProfileHeaderSection(
    fetchState: FetchState,
    fetchScoreState: FetchScoreState,
    primaryColor: Color,
    secondaryColor: Color,
    navController: NavHostController
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
                text = stringResource(R.string.app_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Row {
                IconButton(onClick = { /* TODO: Navigate to notifications */ }) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = stringResource(R.string.notifications),
                        tint = Color.White
                    )
                }

                IconButton(onClick = {
                    navController.navigate("settings")
                }) {
                    Icon(
                        imageVector = Icons.Rounded.Settings,
                        contentDescription = stringResource(R.string.settings),
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
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background
            ),
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
                    contentDescription = stringResource(R.string.avatar),
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
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            Text(
                                text = stringResource(R.string.student_id_format, student.studentId ?: ""),
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.bodyLarge,
                            )

                            Text(
                                text = stringResource(R.string.major),
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.bodyLarge,
                                overflow = TextOverflow.Ellipsis
                            )

                            ProfileScoreInfo(fetchScoreState, primaryColor)
                        }

                        is FetchState.Loading -> {
                            LoadingState(
                                size = 24.dp,
                                color = primaryColor,
                                strokeWidth = 2.dp
                            )
                        }

                        is FetchState.Error -> {
                            ErrorState(
                                message = stringResource(R.string.error_loading_info)
                            )
                        }

                        else -> {
                            Text(
                                text = stringResource(R.string.no_info),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileScoreInfo(
    fetchScoreState: FetchScoreState,
    primaryColor: Color
) {
    when(fetchScoreState) {
        is FetchScoreState.Success -> {
            val score = fetchScoreState.scores
            Text(
                text = stringResource(R.string.gpa_format, score.gpa4),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        is FetchScoreState.Loading -> {
            LoadingState(
                size = 16.dp,
                strokeWidth = 2.dp,
                color = primaryColor
            )
        }

        else -> {
            Text(
                text = stringResource(R.string.no_info),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ImportantNoticesSection(navController: NavHostController) {
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
                text = stringResource(R.string.important_notices),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            TextButton(onClick = {
                navController.navigate("notices")
            }) {
                Text(
                    text = stringResource(R.string.view_all),
                    color = MaterialTheme.colorScheme.primary
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
                    contentDescription = stringResource(R.string.notice),
                    tint = Color(0xFFE65100),
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.exam_schedule_title),
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100)
                    )

                    Text(
                        text = stringResource(R.string.exam_schedule_description),
                        style = MaterialTheme.typography.bodyMedium,
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
            text = stringResource(R.string.utilities),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        val features = listOf(
            FeatureItem(stringResource(R.string.schedule), R.drawable.calendar_date_schedule, Color(0xFF2196F3)),
            FeatureItem(stringResource(R.string.scores), R.drawable.score_repo, Color(0xFF4CAF50)),
            FeatureItem(stringResource(R.string.personal_info), R.drawable.info, Color(0xFF9C27B0)),
            FeatureItem(stringResource(R.string.tuition), R.drawable.pay, Color(0xFFFF9800)),
            FeatureItem(stringResource(R.string.exam_schedule), R.drawable.calendar_date_exem, Color(0xFFF44336)),
            FeatureItem(stringResource(R.string.training_score), R.drawable.score_repo, Color(0xFF795548)),
            FeatureItem(stringResource(R.string.news), R.drawable.news, Color(0xFF607D8B)),
            FeatureItem(stringResource(R.string.support), R.drawable.coffe_svgrepo_com, Color(0xFF009688)),
        )

        // Sử dụng size-based layout thay vì chiều cao cố định
        FeatureGrid(features, navController, context)
    }
}

@Composable
fun FeatureGrid(
    features: List<FeatureItem>,
    navController: NavHostController,
    context: Context
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        contentPadding = PaddingValues(0.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        userScrollEnabled = false, // Disable scrolling since we're inside a scrollable Column
        modifier = Modifier.height((features.size / 4 * 110).dp) // Dynamic height based on items
    ) {
        items(features) { feature ->
            EnhancedFeatureGridItem(feature) {
                when (feature.title) {
                    context.getString(R.string.schedule) -> navController.navigate("week_schedule")
                    context.getString(R.string.scores) -> navController.navigate("score")
                    context.getString(R.string.personal_info) -> navController.navigate("studentInfo")
                    context.getString(R.string.exam_schedule) -> navController.navigate("exam_schedule")
                    context.getString(R.string.training_score) -> navController.navigate("training_score")
                    else -> {
                        Toast.makeText(
                            context,
                            context.getString(R.string.feature_in_development),
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
                colorFilter = ColorFilter.tint(item.backgroundColor)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = item.title,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(64.dp)
        )
    }
}

@Composable
fun TodayScheduleSection(
    weekScheduleState: UiState<ScheduleResponse>,
    todayDateString: String,
    navHostController: NavHostController
) {
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
                text = stringResource(R.string.today_schedule),
                style = MaterialTheme.typography.titleLarge
            )

            TextButton(onClick = {
                navHostController.navigate("week_schedule")
            }) {
                Text(
                    text = stringResource(R.string.view_all),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Extract different states to separate composables
        when (weekScheduleState) {
            is UiState.Loading -> TodayScheduleLoadingState()
            is UiState.Error -> TodayScheduleErrorState(weekScheduleState.message)
            is UiState.Success -> TodayScheduleContent(weekScheduleState.data, todayDateString)
            else -> TodayScheduleEmptyState()
        }
    }
}

@Composable
fun TodayScheduleLoadingState() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .height(60.dp),
            contentAlignment = Alignment.Center
        ) {
            LoadingState(color = HNOULightBlue)
        }
    }
}

@Composable
fun TodayScheduleErrorState(errorMessage: String?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .height(60.dp),
            contentAlignment = Alignment.Center
        ) {
            ErrorState(message = errorMessage ?: stringResource(R.string.error_loading_schedule))
        }
    }
}

@Composable
fun TodayScheduleEmptyState() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .height(60.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.no_data),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun TodayScheduleContent(schedule: ScheduleResponse, todayDateString: String) {
    //Find the correct day
    val todaySchedule =
        schedule.weekDays.find { it.contains(todayDateString) }?.let { day ->
            val dayKey = day.dropLast(12)
            schedule.byDays[dayKey]
        }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            if (todaySchedule?.classes?.isNotEmpty() == true) {
                todaySchedule.classes.forEachIndexed { index, classInfo ->
                    ScheduleItem(
                        status = classInfo.session,
                        session = classInfo.timeSlot,
                        subject = classInfo.subject,
                        room = classInfo.room,
                        backgroundColor = getClassStatusColor(classInfo.session).copy(alpha = 0.1f)
                    )

                    if (index < todaySchedule.classes.size - 1) {
                        Divider(modifier = Modifier.padding(horizontal = 16.dp))
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .height(60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_schedule_today),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}

@Composable
fun ScheduleItem(
    status: String,
    session: String,
    subject: String,
    room: String,
    backgroundColor: Color
) {
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
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = subject,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onBackground

            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.status),
                    contentDescription = stringResource(R.string.status),
                    modifier = Modifier.size(16.dp),
                    tint = HNOULightBlue
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = status,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.width(16.dp))

                Icon(
                    painter = painterResource(id = R.drawable.room_key),
                    contentDescription = stringResource(R.string.room),
                    modifier = Modifier.size(16.dp),
                    tint = HNOULightBlue
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = room,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
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

data class FeatureItem(
    val title: String,
    val iconRes: Int,
    val backgroundColor: Color
)

// Thêm các component tái sử dụng
@Composable
fun LoadingState(
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 24.dp,
    color: Color = MaterialTheme.colorScheme.primary,
    strokeWidth: androidx.compose.ui.unit.Dp = 2.dp
) {
    CircularProgressIndicator(
        modifier = modifier.then(Modifier.size(size)),
        color = color,
        strokeWidth = strokeWidth
    )
}

@Composable
fun ErrorState(message: String) {
    Text(
        text = message,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.error
    )
}

// Thêm Preview cho các thành phần chính
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HouCheckTheme {
        val navController = rememberNavController()
        val authViewModel: AuthViewModel = hiltViewModel()
        val fetchInfoViewModel: InfoStudentViewModel = hiltViewModel()
        val fetchScoreViewModel: FetchScoreViewModel = hiltViewModel()
        val fetchWeekScheduleViewModel: WeekScheduleViewModel = hiltViewModel()

        HomeScreen(
            navController = navController,
            authViewModel = authViewModel,
            fetchInfoViewModel = fetchInfoViewModel,
            fetchScoreViewModel = fetchScoreViewModel,
            fetchWeekScheduleViewModel = fetchWeekScheduleViewModel
        )
    }
}