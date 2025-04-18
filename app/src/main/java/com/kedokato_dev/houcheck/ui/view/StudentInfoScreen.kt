package com.kedokato_dev.houcheck.ui.view

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.kedokato_dev.houcheck.R
import com.kedokato_dev.houcheck.data.model.Student
import com.kedokato_dev.houcheck.data.repository.AuthRepository
import com.kedokato_dev.houcheck.data.repository.FetchStudentInfoRepository
import com.kedokato_dev.houcheck.ui.viewmodel.FetchInfoStudentViewModel
import com.kedokato_dev.houcheck.ui.viewmodel.FetchInfoStudentViewModelFactory
import com.kedokato_dev.houcheck.ui.viewmodel.FetchState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentInfoScreen(navHostController: NavHostController) {
    val context = LocalContext.current
    val repository = remember { FetchStudentInfoRepository() }
    val sharedPreferences = remember {
        context.getSharedPreferences("sessionId", Context.MODE_PRIVATE)
    }

    val authRepository = remember { AuthRepository(sharedPreferences) }
    val viewModel: FetchInfoStudentViewModel = viewModel(
        factory = FetchInfoStudentViewModelFactory(repository)
    )

    val fetchState by viewModel.fetchState.collectAsState()
    val scrollState = rememberScrollState() // Thêm trạng thái cuộn

    // Màu sắc hiện đại
    val primaryColor = Color(0xFF5B21B6) // Tím đậm
    val secondaryColor = Color(0xFF9333EA) // Tím nhạt hơn
    val gradientColors = listOf(primaryColor, secondaryColor)

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        "Hồ sơ sinh viên",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 28.sp
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
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = primaryColor,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF8F7FC) // Nền sáng cho toàn màn hình
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Bọc nội dung chính trong verticalScroll để có thể cuộn
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState) // Thêm khả năng cuộn
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Phần thông tin cá nhân với nền gradient
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            brush = Brush.verticalGradient(gradientColors)
                        )
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Hình ảnh hồ sơ tròn với viền
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.no_avatar),
                                contentDescription = "Hình ảnh sinh viên",
                                modifier = Modifier
                                    .size(112.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        when (fetchState) {
                            is FetchState.Success -> {
                                val student = (fetchState as FetchState.Success).student
                                Text(
                                    student.studentName.toString(), // Sử dụng toString() để đảm bảo an toàn
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )

                                Text(
                                    "Mã SV: ${student.studentId}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.White.copy(alpha = 0.8f),
                                    textAlign = TextAlign.Center
                                )
                            }
                            else -> {
                                Text(
                                    "Hồ sơ sinh viên",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // Xử lý các trạng thái
                when (fetchState) {
                    is FetchState.Idle -> {
                        EmptyStateSection(
                            onFetchClick = {
                                viewModel.fetchInfoStudent(authRepository.getSessionId().toString())
                            },
                            primaryColor = primaryColor
                        )
                    }
                    is FetchState.Loading -> {
                        LoadingStateSection(primaryColor = primaryColor)
                    }
                    is FetchState.Success -> {
                        // Phải xử lý đúng kiểu dữ liệu ở đây
                        val student = (fetchState as FetchState.Success).student
                        StudentDetailsSection(student = student)
                    }
                    is FetchState.Error -> {
                        ErrorStateSection(
                            message = (fetchState as FetchState.Error).message,
                            onRetryClick = {
                                viewModel.fetchInfoStudent(authRepository.getSessionId().toString())
                            },
                            primaryColor = primaryColor
                        )
                    }
                }
            }
        }
    }
}

// Các hàm khác giữ nguyên, nhưng điều chỉnh StudentDetailsSection để xử lý kiểu dữ liệu đúng
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
                title = "Thông tin cá nhân",
                content = {
                    InfoItem(
                        icon = Icons.Outlined.Person,
                        label = "Họ và tên",
                        value = student.studentName?.toString() ?: "Không có thông tin"
                    )
                    InfoItem(
                        icon = Icons.Outlined.Person,
                        label = "Ngày sinh",
                        value = student.birthDate?.toString() ?: "Không có thông tin"
                    )
                    InfoItem(
                        icon = Icons.Outlined.Person,
                        label = "Giới tính",
                        value = student.sex?.toString() ?: "Không có thông tin"
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Thông tin liên hệ
            InfoCard(
                title = "Thông tin liên hệ",
                content = {
                    InfoItem(
                        icon = Icons.Outlined.Phone,
                        label = "Điện thoại nhà",
                        value = student.phone?.toString() ?: "Không có thông tin"
                    )
                    InfoItem(
                        icon = Icons.Outlined.Phone,
                        label = "Điện thoại cá nhân",
                        value = student.userPhone?.toString() ?: "Không có thông tin"
                    )
                    InfoItem(
                        icon = Icons.Outlined.Email,
                        label = "Email",
                        value = student.email?.toString() ?: "Không có thông tin"
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Thông tin địa chỉ
            InfoCard(
                title = "Thông tin địa chỉ",
                content = {
                    InfoItem(
                        icon = Icons.Outlined.Place,
                        label = "Nơi sinh",
                        value = student.address?.toString() ?: "Không có thông tin"
                    )
                    InfoItem(
                        icon = Icons.Outlined.Place,
                        label = "Địa chỉ hiện tại",
                        value = student.detailAddress?.toString() ?: "Không có thông tin"
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
            "No student information available yet",
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
                "Fetch Student Info",
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
            "Loading student information...",
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
        Image(
            painter = painterResource(id = R.drawable.no_avatar),
            contentDescription = "Error state",
            modifier = Modifier.size(100.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Something went wrong",
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
                "Retry",
                color = primaryColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}



@Composable
private fun InfoCard(title: String, content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 4.dp,
        color = Color.White
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
                color = Color(0xFF5B21B6)
            )

            Spacer(modifier = Modifier.height(16.dp))

            content()
        }
    }
}

@Composable
private fun InfoItem(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = Color(0xFF9333EA)
        )

        Spacer(modifier = Modifier.width(16.dp))

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
    // Gọi hàm StudentInfoScreen với NavHostController giả lập
    val navHostController = NavHostController(LocalContext.current)
    StudentInfoScreen(navHostController)
}



