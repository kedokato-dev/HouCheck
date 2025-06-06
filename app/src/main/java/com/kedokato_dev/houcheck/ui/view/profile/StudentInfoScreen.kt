package com.kedokato_dev.houcheck.ui.view.profile

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Vertices
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.kedokato_dev.houcheck.R
import com.kedokato_dev.houcheck.network.model.Student
import com.kedokato_dev.houcheck.repository.AuthRepository
import com.kedokato_dev.houcheck.ui.theme.accentColor
import com.kedokato_dev.houcheck.ui.theme.backgroundColor
import com.kedokato_dev.houcheck.ui.theme.primaryColor
import com.kedokato_dev.houcheck.ui.theme.secondaryColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentInfoScreen(navHostController: NavHostController) {
    val context = LocalContext.current
    val sharedPreferences = remember {
        context.getSharedPreferences("sessionId", Context.MODE_PRIVATE)
    }

    val viewModel: InfoStudentViewModel = hiltViewModel()
    val authRepository = remember { AuthRepository(sharedPreferences) }
    val fetchState by viewModel.fetchState.collectAsState()
    val scrollState = rememberScrollState()
    LaunchedEffect(Unit) {
        viewModel.fetchStudentIfNeeded(authRepository.getSessionId().toString())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Thông tin sinh viên",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 24.sp
                        )
                    }
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
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header với phần thông tin cơ bản và nút xem lịch học
                StudentHeaderCard(
                    fetchState = fetchState,
                    primaryColor = primaryColor,
                    secondaryColor = secondaryColor
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Xử lý các trạng thái
                when (fetchState) {
                    is FetchState.Idle -> {
                        EmptyStateSection(
                            onFetchClick = {
                                viewModel.fetchStudentIfNeeded(sharedPreferences.toString())
                            },
                            primaryColor = primaryColor
                        )
                    }

                    is FetchState.Loading -> {
                        LoadingStateSection(primaryColor = primaryColor)
                    }

                    is FetchState.Success -> {
                        val student = (fetchState as FetchState.Success).student
                        StudentDetailsSection(student = student)
                    }

                    is FetchState.Error -> {
                        ErrorStateSection(
                            message = (fetchState as FetchState.Error).message,
                            onRetryClick = {
                                viewModel.fetchStudentIfNeeded(sharedPreferences.toString())
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
private fun StudentHeaderCard(
    fetchState: FetchState,
    primaryColor: Color,
    secondaryColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Phần header với gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(listOf(primaryColor, secondaryColor))
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Hình ảnh profile
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                            .padding(3.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.no_avatar),
                            contentDescription = "Hình ảnh sinh viên",
                            modifier = Modifier
                                .size(94.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    when (fetchState) {
                        is FetchState.Success -> {
                            val student = (fetchState as FetchState.Success).student
                            Text(
                                student.studentName.toString(),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                "Mã SV: ${student.studentId}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.9f),
                                textAlign = TextAlign.Center
                            )
                        }

                        else -> {
                            Text(
                                "Thông tin sinh viên",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                        }

                        // Thông tin khóa học và học kỳ khi có dữ liệu

                    }
                    if (fetchState is FetchState.Success) {
                        val student = (fetchState as FetchState.Success).student
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround,

                        ) {
                            CourseInfoItem(
                                label = stringResource(R.string.academic_year),
                                value = "K${student.studentId.take(2)}",
                                icon = Icons.Filled.AccountBox
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CourseInfoItem(
    label: String,
    value: String,
    icon: ImageVector
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFFFFFFFF),
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White,
        )
    }
}

@Composable
private fun StudentDetailsSection(student: Student) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Thông tin cá nhân
            InfoCard(
                title = stringResource(R.string.student_info),
                content = {
                    InfoItem(
                        icon = Icons.Filled.Person,
                        label = stringResource(R.string.full_name),
                        value = student.studentName?.toString()
                            ?: (stringResource(R.string.no_info)),
                        accentColor = MaterialTheme.colorScheme.onBackground
                    )
                    InfoItem(
                        icon = Icons.Filled.DateRange,
                        label = stringResource(R.string.date_of_birth),
                        value = student.birthDate?.toString() ?: (stringResource(R.string.no_info)),
                        accentColor = MaterialTheme.colorScheme.onBackground

                    )
                    InfoItem(
                        icon = Icons.Filled.Person,
                        label = stringResource(R.string.sex),
                        value = student.sex?.toString() ?: (stringResource(R.string.no_info)),
                        accentColor = MaterialTheme.colorScheme.onBackground

                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Thông tin liên hệ
            InfoCard(
                title = stringResource(R.string.contact_information),
                content = {
                    InfoItem(
                        icon = Icons.Filled.Phone,
                        label = stringResource(R.string.phone_number),
                        value = student.phone?.toString() ?: stringResource(R.string.no_info),
                        accentColor = MaterialTheme.colorScheme.onBackground
                    )
                    InfoItem(
                        icon = Icons.Filled.Phone,
                        label = stringResource(R.string.phone_number),
                        value = student.userPhone?.toString() ?: stringResource(R.string.no_info),
                        accentColor = MaterialTheme.colorScheme.onBackground
                    )
                    InfoItem(
                        icon = Icons.Filled.Email,
                        label = stringResource(R.string.email),
                        value = student.email?.toString() ?: stringResource(R.string.no_info),
                        accentColor = MaterialTheme.colorScheme.onBackground
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Thông tin địa chỉ
            InfoCard(
                title = stringResource(R.string.address_info),
                content = {
                    InfoItem(
                        icon = Icons.Filled.Place,
                        label = stringResource(R.string.address),
                        value = student.address?.toString() ?: stringResource(R.string.no_info),
                        accentColor = MaterialTheme.colorScheme.onBackground
                    )
                    InfoItem(
                        icon = Icons.Filled.Place,
                        label = stringResource(R.string.address_parent),
                        value = student.detailAddress?.toString()
                            ?: stringResource(R.string.no_info),
                        accentColor = MaterialTheme.colorScheme.onBackground
                    )
                }
            )
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
        Image(
            painter = painterResource(id = R.drawable.no_avatar),
            contentDescription = "Empty state",
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Chưa có thông tin sinh viên",
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
            shape = RoundedCornerShape(8.dp),
            elevation = ButtonDefaults.elevatedButtonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 6.dp
            ),
            modifier = Modifier.height(48.dp)
        ) {
            Text(
                "Tải thông tin sinh viên",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun LoadingStateSection(primaryColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
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
            "Đang tải thông tin...",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray
        )
    }
}

@Composable
private fun ErrorStateSection(message: String, onRetryClick: () -> Unit, primaryColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.no_avatar),
            contentDescription = "Error state",
            modifier = Modifier.size(100.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Đã xảy ra lỗi",
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
                width = 1.5.dp,
                brush = Brush.horizontalGradient(
                    listOf(
                        primaryColor,
                        primaryColor.copy(alpha = 0.7f)
                    )
                )
            ),
            shape = RoundedCornerShape(8.dp),
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

@Composable
private fun InfoCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1976D2)
            )

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color(0xFFEEEEEE))
            Spacer(modifier = Modifier.height(8.dp))

            content()
        }
    }
}

@Composable
private fun InfoItem(icon: ImageVector, label: String, value: String, accentColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = accentColor
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun StudentInfoScreenPreview() {
    val navHostController = NavHostController(LocalContext.current)
    StudentInfoScreen(navHostController)
}