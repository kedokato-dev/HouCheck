package com.kedokato_dev.houcheck.ui.view.score_list

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.kedokato_dev.houcheck.R
import com.kedokato_dev.houcheck.network.api.ApiClient
import com.kedokato_dev.houcheck.network.api.ListScoreService
import com.kedokato_dev.houcheck.network.model.CourseResult
import com.kedokato_dev.houcheck.repository.AuthRepository
import com.kedokato_dev.houcheck.repository.ListScoreRepository
import com.kedokato_dev.houcheck.local.dao.AppDatabase
import com.kedokato_dev.houcheck.ui.components.LoadingComponent
import com.kedokato_dev.houcheck.ui.state.UiState
import com.kedokato_dev.houcheck.ui.theme.HNOUDarkBlue
import com.kedokato_dev.houcheck.ui.theme.HNOULightBlue
import com.kedokato_dev.houcheck.ui.theme.primaryColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScoreScreen(
    navHostController: NavHostController
) {
    val context = LocalContext.current
    val api = remember { ApiClient.instance.create(ListScoreService::class.java) }
    val dao = AppDatabase.buildDatabase(context).courseResultDAO()
    val repository = remember {
        ListScoreRepository(
            api, dao
        )
    }
    val sharedPreferences = remember {
        context.getSharedPreferences("sessionId", Context.MODE_PRIVATE)
    }
    val authRepository = remember { AuthRepository(sharedPreferences) }

    val viewModel: ListScoreViewModel = viewModel(
        factory = ListScoreViewModelFactory(repository)
    )
    val fetchState = viewModel.state.collectAsState()

    // Search state
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.fetchListScore(authRepository.getSessionId().toString())
    }

    Scaffold(
        topBar = {
            if (isSearchActive) {
                // Search TopBar
                TopAppBar(
                    title = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .background(Color.White.copy(alpha = 0.1f), shape = RoundedCornerShape(16.dp))
                        ) {
                            TextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Nhập tên môn học...") },

                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp),
                                singleLine = true,
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    disabledContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedPlaceholderColor = Color.White.copy(alpha = 0.7f),
                                    unfocusedPlaceholderColor = Color.White.copy(alpha = 0.7f)
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            isSearchActive = false
                            searchQuery = ""
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Đóng tìm kiếm",
                                tint = Color.White
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            if (searchQuery.isNotBlank()) {
                                viewModel.searchListScore(searchQuery)
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Thực hiện tìm kiếm",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = primaryColor,
                        titleContentColor = Color.White
                    )
                )
            } else {
                // Regular TopBar
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
                    actions = {
                        IconButton(onClick = {
                            viewModel.refreshListScore(authRepository.getSessionId().toString())
                            Toast.makeText(context, "Đang tải lại...", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = "Tải lại",
                                tint = Color.White
                            )
                        }

                        IconButton(onClick = {
                            isSearchActive = true
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Tìm kiếm",
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
        },
        containerColor = Color.White
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
                LoadingComponent(
                    HNOUDarkBlue,
                    "Đang tải dữ liệu điểm",
                    "Vui lòng chờ trong giây lát",

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

    // Perform search when user presses enter
    LaunchedEffect(searchQuery) {
        if (isSearchActive && searchQuery.isNotBlank()) {
          viewModel.searchListScore(searchQuery)
        }
    }
}

@Composable
fun CourseResultItem(course: CourseResult) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        // Thêm border với màu và độ dày tùy chọn
        border = BorderStroke(width = 2.dp, color = HNOULightBlue)
    ) {
        Column {
            // Basic info row (always visible)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Course name and code
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = course.courseName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = course.courseCode,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Grade display
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(
                            color = getGradeColor(course.letterGrade),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (course.score4 != null) course.score4.toString() else "N/A",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Expand/collapse icon
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded)
                            Icons.Filled.KeyboardArrowUp else
                            Icons.Filled.KeyboardArrowDown,
                        contentDescription = if (expanded) "Thu gọn" else "Xem chi tiết",
                    )
                }
            }

            if (expanded) {
                Divider()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Academic period info
                    DetailItem(
                        iconResId = R.drawable.calendar,
                        label = "Học kỳ:",
                        value = "${course.semester} - ${course.academicYear}"
                    )

                    DetailItem(
                        iconResId = R.drawable.star,
                        label = "Tín chỉ:",
                        value = "${course.credits}"
                    )

                    DetailItem(
                        iconResId = R.drawable.accept,
                        label = "Điểm 10:",
                        value = (course.score10 ?: "Chưa có").toString()
                    )

                    DetailItem(
                        iconResId = R.drawable.accept,
                        label = "Điểm chữ:",
                        value = course.letterGrade.ifBlank { "Chưa có" }
                    )

                    if (course.note.isNotBlank()) {
                        DetailItem(
                            iconResId = R.drawable.notebook,
                            label = "Ghi chú:",
                            value = course.note
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DetailItem(iconResId: Int, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = iconResId),
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(72.dp)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}


@Composable
fun getGradeColor(grade: String): Color {
    return when {
        grade.isBlank() -> MaterialTheme.colorScheme.outline
        grade == "A+" || grade == "A" -> Color(0xFF388E3C) // Dark Green
        grade == "B+" || grade == "B" -> Color(0xFF1976D2) // Blue
        grade == "C+" || grade == "C" -> Color(0xFFFFA000) // Amber
        grade == "D+" || grade == "D" -> Color(0xFFE64A19) // Deep Orange
        grade == "P" -> Color(0xFFCDDC39) // Deep Orange
        grade == "F" -> Color(0xFFD32F2F) // Red
        else -> MaterialTheme.colorScheme.tertiary
    }
}

