package com.kedokato_dev.houcheck.ui.view.week_schedule

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.kedokato_dev.houcheck.R
import com.kedokato_dev.houcheck.network.api.ApiClient
import com.kedokato_dev.houcheck.network.api.WeekScheduleService
import com.kedokato_dev.houcheck.network.model.ClassInfo
import com.kedokato_dev.houcheck.network.model.DaySchedule
import com.kedokato_dev.houcheck.repository.AuthRepository
import com.kedokato_dev.houcheck.repository.WeekScheduleRepository
import com.kedokato_dev.houcheck.ui.components.LoadingComponent
import com.kedokato_dev.houcheck.ui.state.UiState
import com.kedokato_dev.houcheck.ui.theme.HNOUDarkBlue
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    navHostController: NavHostController
) {
    val context = LocalContext.current
    val api = remember { ApiClient.instance.create(WeekScheduleService::class.java) }
    val repository = remember { WeekScheduleRepository(api) }
    val sharedPreferences = remember {
        context.getSharedPreferences("sessionId", Context.MODE_PRIVATE)
    }
    val authRepository = remember { AuthRepository(sharedPreferences) }

    val viewModel: WeekScheduleViewModel = viewModel(
        factory = WeekScheduleViewModelFactory(repository)
    )

    // Current date for initial display
    val today = remember { Calendar.getInstance() }
    var selectedDate by remember { mutableStateOf(today.time) }
    var selectedWeekStart by remember { mutableStateOf(getWeekStart(today).time) }

    // State for calendar collapse
    var isCalendarExpanded by remember { mutableStateOf(true) }

    val dateFormat = remember { SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()) }
    val weekRange = remember(selectedWeekStart) {
        val start = Calendar.getInstance().apply { time = selectedWeekStart }
        val end = Calendar.getInstance().apply {
            time = selectedWeekStart
            add(Calendar.DAY_OF_YEAR, 6)
        }
        "${dateFormat.format(start.time)}-${dateFormat.format(end.time)}"
    }

    LaunchedEffect(weekRange) {
        viewModel.fetchWeekSchedule(
            authRepository.getSessionId().toString(),
            weekRange
        )
    }

    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thời khoá biểu") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = HNOUDarkBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = { navHostController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    // Today button
                    IconButton(onClick = {
                        val currentDay = Calendar.getInstance()
                        selectedDate = currentDay.time
                        selectedWeekStart = getWeekStart(currentDay).time
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.today),
                            modifier = Modifier.size(24.dp),
                            contentDescription = "Today",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when (state) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingComponent(
                        primaryColor = HNOUDarkBlue,
                        "Đang tải thông tin thời khoá biểu"
                    )
                }
            }

            is UiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (state as UiState.Error).message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            is UiState.Success -> {
                val schedule = (state as UiState.Success).data
                val weekDays = schedule.weekDays
                val byDays = schedule.byDays

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // Calendar header with collapse button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(HNOUDarkBlue)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Lịch",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium
                        )

                        IconButton(
                            onClick = { isCalendarExpanded = !isCalendarExpanded }
                        ) {
                            Icon(
                                imageVector = if (isCalendarExpanded)
                                    Icons.Default.KeyboardArrowUp else
                                    Icons.Default.KeyboardArrowDown,
                                contentDescription = if (isCalendarExpanded)
                                    "Thu nhỏ lịch" else "Mở rộng lịch",
                                tint = Color.White
                            )
                        }
                    }

                    // Hiện lịch theo tuần - chỉ hiện khi không bị thu nhỏ
                    if (isCalendarExpanded) {
                        WeekCalendarView(
                            weekStart = selectedWeekStart,
                            onDateSelected = { selectedDate = it },
                            onWeekChanged = { newWeekStart ->
                                selectedWeekStart = newWeekStart
                            },
                            selectedDate = selectedDate,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(HNOUDarkBlue)
                                .padding(bottom = 16.dp)
                        )
                    }

                    // Nội dung lịch học
                    ModernScheduleContent(
                        weekDays = weekDays,
                        byDays = byDays,
                        selectedDate = selectedDate,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Không có dữ liệu lịch học")
                }
            }
        }
    }
}

@Composable
fun WeekCalendarView(
    weekStart: Date,
    onDateSelected: (Date) -> Unit,
    onWeekChanged: (Date) -> Unit,
    selectedDate: Date,
    modifier: Modifier = Modifier
) {
    val cal = remember { Calendar.getInstance() }
    val today = remember { Calendar.getInstance().time }

    // Generate dates for the current week
    val weekDates = remember(weekStart) {
        val dates = mutableListOf<Date>()
        cal.time = weekStart

        repeat(7) {
            dates.add(cal.time)
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }
        dates
    }

    val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale("vi"))
    val monthYear = remember(weekStart) { monthYearFormat.format(weekStart) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Month and Year with navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                cal.time = weekStart
                cal.add(Calendar.WEEK_OF_YEAR, -1)
                onWeekChanged(cal.time)
            }) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Previous Week",
                    tint = Color.White
                )
            }

            Text(
                text = monthYear.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )

            IconButton(onClick = {
                cal.time = weekStart
                cal.add(Calendar.WEEK_OF_YEAR, 1)
                onWeekChanged(cal.time)
            }) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Next Week",
                    tint = Color.White
                )
            }
        }

        // Weekday headers
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val dayLabels = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")
            dayLabels.forEach { day ->
                Text(
                    text = day,
                    color = Color.White.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Day numbers
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val dateFormat = SimpleDateFormat("dd", Locale.getDefault())

            weekDates.forEachIndexed { index, date ->
                val isSelected = SimpleDateFormat("yyyyMMdd").format(date) ==
                        SimpleDateFormat("yyyyMMdd").format(selectedDate)
                val isToday = SimpleDateFormat("yyyyMMdd").format(date) ==
                        SimpleDateFormat("yyyyMMdd").format(today)

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isSelected -> Color.White
                                    else -> Color.Transparent
                                }
                            )
                            .clickable { onDateSelected(date) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = dateFormat.format(date),
                            color = when {
                                isSelected -> Color(0xFF2196F3)
                                else -> Color.White
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal
                        )
                    }

                    // Today indicator dot
                    if (isToday) {
                        Box(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                    }
                }
            }
        }

        // Today's date display
        Box(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White.copy(alpha = 0.2f))
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            val formattedSelectedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate)
            Text(
                text = if (SimpleDateFormat("yyyyMMdd").format(selectedDate) ==
                    SimpleDateFormat("yyyyMMdd").format(today))
                    "Hôm nay" else formattedSelectedDate,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun ModernScheduleContent(
    weekDays: List<String>,
    byDays: Map<String, DaySchedule>,
    selectedDate: Date,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    // Trích xuất ngày/tháng/năm từ selectedDate
    val selectedDateString = SimpleDateFormat("dd/MM/yyyy", Locale("vi")).format(selectedDate)
    Log.d("SelectedDate", "Selected date string: $selectedDateString")

    // Tìm vị trí ngày được chọn để scroll
    LaunchedEffect(selectedDate) {
        val position = weekDays.indexOfFirst { it.contains(selectedDateString) }.takeIf { it >= 0 } ?: 0
        Log.d("Scrolling", "Scrolling to position: $position for date: $selectedDateString")
        listState.animateScrollToItem(position)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        state = listState
    ) {
        weekDays.forEach { day ->
            item(key = day) {
                val dayDate = formatDayDate(day)

                // Kiểm tra xem chuỗi ngày có chứa selectedDateString không
                val isSelectedDay = day.contains(selectedDateString)
                Log.d("ScheduleScreen", "Day: $day, Looking for: $selectedDateString, Selected: $isSelectedDay")

                Column(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth()
                        .let {
                            if (isSelectedDay) {
                                it.background(
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(16.dp)
                                ).padding(horizontal = 8.dp, vertical = 4.dp)
                            } else {
                                it
                            }
                        }
                ) {

                    Text(
                        text = dayDate,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    val dayKey = day.dropLast(12)
                    val daySchedule = byDays[dayKey]

                    if (daySchedule?.classes?.isEmpty() == true) {
                        EmptyDayCard()
                    } else {
                        daySchedule?.classes?.forEach { classInfo ->
                            ModernClassCard(classInfo)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun EmptyDayCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Không có lịch học cho ngày này",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ModernClassCard(classInfo: ClassInfo) {
    // Simplified color logic using only three colors
    val subjectColor = getClassStatusColor(classInfo.session)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .height(170.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Left color indicator based on class status
            Box(
                modifier = Modifier
                    .width(12.dp)
                    .fillMaxHeight()
                    .background(subjectColor)
            )

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header section
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Class ID and session type
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        SelectionContainer {
                            Text(
                                text = classInfo.classId,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    SessionTypeTag(classInfo.session)

                    Spacer(modifier = Modifier.height(6.dp))

                    // Subject name
                    Text(
                        text = classInfo.subject,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(8.dp))


                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = classInfo.teacher,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Room information
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp) // Increased padding for better touch targets
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = "Location", // Added content description for accessibility
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = classInfo.room,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.width(8.dp)) // Added proper spacing between text elements

                    // Added a small visual divider between room and session
                    Box(
                        modifier = Modifier
                            .height(12.dp)
                            .width(1.dp)
                            .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Added session icon for visual consistency
                    Icon(
                        imageVector = Icons.Outlined.Star, // Added time/schedule icon
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text =  classInfo.timeSlot,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun SessionTypeTag(session: String) {
    val (backgroundColor, textColor, label) = when {
        session.contains("bù") -> Triple(
            MaterialTheme.colorScheme.errorContainer,
            Color(0xFF9C27B0),  // Tím (Purple)
            "Học bù"
        )
        session.contains("Nghỉ") -> Triple(
            MaterialTheme.colorScheme.errorContainer,
            Color(0xFFF44336),  // Đỏ (Red)
            "Nghỉ học"
        )
        else -> Triple(
            MaterialTheme.colorScheme.primaryContainer,
            Color(0xFF4CAF50),  // Xanh (Green)
            "Học bình thường"
        )
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        contentColor = backgroundColor
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

// New function to get color based on class status
private fun getClassStatusColor(session: String): Color {
    return when {
        session.contains("bù") -> Color(0xFF9C27B0)    // Tím (Purple) for makeup classes
        session.contains("Nghỉ") -> Color(0xFFF44336)  // Đỏ (Red) for canceled classes
        else -> Color(0xFF4CAF50)                      // Xanh (Green) for normal classes
    }
}

// Rest of the helper functions remain the same...

// Helper functions for formatting and colors
private fun formatDayName(dayString: String): String {
    // Example: "Mon, Apr 21, 2025 00:00:00 GMT" -> "Thứ Hai"
    val dayMap = mapOf(
        "Mon" to "Thứ Hai",
        "Tue" to "Thứ Ba",
        "Wed" to "Thứ Tư",
        "Thu" to "Thứ Năm",
        "Fri" to "Thứ Sáu",
        "Sat" to "Thứ Bảy",
        "Sun" to "Chủ Nhật"
    )

    val dayCode = dayString.substring(0, 3)
    return dayMap[dayCode] ?: dayCode
}

private fun formatDayDate(dayString: String): String {
    // Example: "Mon, Apr 21, 2025 00:00:00 GMT" -> "21 tháng 4, 2025"
    val parts = dayString.split(" ")
    if (parts.size >= 4) {
        val day = parts[2].removeSuffix(",")
        val month = when(parts[1]) {
            "Jan" -> "1"
            "Feb" -> "2"
            "Mar" -> "3"
            "Apr" -> "4"
            "May" -> "5"
            "Jun" -> "6"
            "Jul" -> "7"
            "Aug" -> "8"
            "Sep" -> "9"
            "Oct" -> "10"
            "Nov" -> "11"
            "Dec" -> "12"
            else -> parts[1]
        }
        val year = parts[3]

        return "$day tháng $month, $year"
    }
    return dayString
}

// Helper function to get the start of the week (Monday)
private fun getWeekStart(calendar: Calendar): Calendar {
    val firstDayOfWeek = Calendar.MONDAY
    val result = Calendar.getInstance()
    result.time = calendar.time

    while (result.get(Calendar.DAY_OF_WEEK) != firstDayOfWeek) {
        result.add(Calendar.DATE, -1)
    }

    // Reset time part
    result.set(Calendar.HOUR_OF_DAY, 0)
    result.set(Calendar.MINUTE, 0)
    result.set(Calendar.SECOND, 0)
    result.set(Calendar.MILLISECOND, 0)

    return result
}
