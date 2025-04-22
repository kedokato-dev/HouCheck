package com.kedokato_dev.houcheck.ui.view

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.kedokato_dev.houcheck.data.model.ExamSchedule
import com.kedokato_dev.houcheck.data.repository.AuthRepository
import com.kedokato_dev.houcheck.data.repository.FetchExamScheduleRepository
import com.kedokato_dev.houcheck.ui.viewmodel.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamScheduleScreen(navController: NavHostController) {
    val context = LocalContext.current
    val repository = remember { FetchExamScheduleRepository() }
    val sharedPreferences = remember {
        context.getSharedPreferences("sessionId", Context.MODE_PRIVATE)
    }
    val authRepository = remember { AuthRepository(sharedPreferences) }

    val viewModel: FetchExamScheduleViewModel = viewModel(
        factory = FetchExamScheduleViewModelFactory(repository)
    )
    val fetchState by viewModel.fetchState.collectAsState()
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    // State để theo dõi kỳ được chọn từ dropdown
    var selectedSemester by remember { mutableStateOf<String?>(null) }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchExamSchedules(authRepository.getSessionId().toString())
    }

    val primaryColor = Color(0xFF03A9F4)
    val secondaryColor = Color(0xFF0277BD)

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
                                coroutineScope.launch {
                                    viewModel.fetchExamSchedules(authRepository.getSessionId().toString())
                                }
                            },
                            primaryColor = primaryColor
                        )
                    }
                    is FetchExamScheduleState.Loading -> {
                        LoadingStateSection(primaryColor = primaryColor)
                    }
                    is FetchExamScheduleState.Success -> {
                        val schedules = (fetchState as FetchExamScheduleState.Success).schedules
                        if (schedules.isEmpty()) {
                            Text(
                                text = "Không có lịch thi nào để hiển thị",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Gray,
                                modifier = Modifier.padding(16.dp)
                            )
                        } else {
                            // Nhóm lịch thi theo kỳ học
                            val groupedSchedules = schedules.groupBy { it.semester ?: "Không xác định" }

                            // Lấy danh sách các kỳ học cho dropdown
                            val semesters = groupedSchedules.keys.toList()

                            // Nếu chưa chọn kỳ nào, mặc định chọn kỳ đầu tiên
                            if (selectedSemester == null && semesters.isNotEmpty()) {
                                selectedSemester = semesters.first()
                            }

                            // Dropdown để chọn kỳ học
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Chọn kỳ học:",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF333333),
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Box {
                                    OutlinedButton(
                                        onClick = { expanded = true },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier
                                            .width(200.dp)
                                            .height(40.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = selectedSemester ?: "Chọn kỳ học",
                                                fontSize = 14.sp,
                                                color = if (selectedSemester == null) Color.Gray else Color.Black
                                            )
                                            Spacer(modifier = Modifier.weight(1f))
                                            Icon(
                                                imageVector = Icons.Default.ArrowDropDown,
                                                contentDescription = "Dropdown",
                                                tint = Color.Gray
                                            )
                                        }
                                    }
                                    DropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false },
                                        modifier = Modifier
                                            .width(200.dp)
                                            .background(Color.White)
                                    ) {
                                        semesters.forEach { semester ->
                                            DropdownMenuItem(
                                                text = {
                                                    Text(
                                                        text = semester,
                                                        fontSize = 14.sp
                                                    )
                                                },
                                                onClick = {
                                                    selectedSemester = semester
                                                    expanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            // Hiển thị lịch thi của kỳ được chọn
                            selectedSemester?.let { semester ->
                                val semesterSchedules = groupedSchedules[semester] ?: emptyList()
                                if (semesterSchedules.isEmpty()) {
                                    Text(
                                        text = "Không có lịch thi cho kỳ $semester",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                } else {
                                    semesterSchedules.forEach { schedule ->
                                        ExamScheduleCard(schedule = schedule)
                                        Spacer(modifier = Modifier.height(12.dp))
                                    }
                                }
                            }
                        }
                    }
                    is FetchExamScheduleState.Error -> {
                        ErrorStateSection(
                            message = (fetchState as FetchExamScheduleState.Error).message,
                            onRetryClick = {
                                coroutineScope.launch {
                                    viewModel.fetchExamSchedules(authRepository.getSessionId().toString())
                                }
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
                .padding(16.dp)
        ) {
            // Tên môn thi (đậm)
            Text(
                text = schedule.subject ?: "N/A",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Ngày thi
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Ngày thi: ")
                    }
                    append(schedule.date ?: "N/A")
                },
                fontSize = 14.sp,
                color = Color(0xFF333333)
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Buổi thi và Giờ thi
            Row {
                Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Buổi: ")
                        }
                        append(schedule.session ?: "N/A")
                    },
                    fontSize = 14.sp,
                    color = Color(0xFF333333)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "—", fontSize = 14.sp, color = Color(0xFF333333))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Giờ: ")
                        }
                        append(schedule.time ?: "N/A")
                    },
                    fontSize = 14.sp,
                    color = Color(0xFF333333)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Phòng và SBD
            Row {
                Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Phòng: ")
                        }
                        append(schedule.room ?: "N/A")
                    },
                    fontSize = 14.sp,
                    color = Color(0xFF333333)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "—", fontSize = 14.sp, color = Color(0xFF333333))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("SBD: ")
                        }
                        append(schedule.studentNumber ?: "N/A")
                    },
                    fontSize = 14.sp,
                    color = Color(0xFF333333)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Đợt thi
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Đợt thi: ")
                    }
                    append(schedule.testPhase ?: "N/A")
                },
                fontSize = 14.sp,
                color = Color(0xFF333333)
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Hình thức thi (nhãn đậm + nội dung thường)
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Hình thức: ")
                    }
                    append(schedule.examType ?: "N/A")
                },
                fontSize = 14.sp,
                color = Color(0xFF333333)
            )

            // Ghi chú (nếu có)
            schedule.note?.let { note ->
                if (note.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("Ghi chú: ")
                            }
                            append(note)
                        },
                        fontSize = 14.sp,
                        color = Color(0xFF333333)
                    )
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