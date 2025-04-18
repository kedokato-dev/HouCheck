package com.kedokato_dev.houcheck.ui.view

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.kedokato_dev.houcheck.data.repository.AuthRepository
import com.kedokato_dev.houcheck.data.repository.FetchStudentInfoRepository
import com.kedokato_dev.houcheck.data.repository.FetchStudentNameRepository
import com.kedokato_dev.houcheck.ui.viewmodel.FetchInfoStudentViewModel
import com.kedokato_dev.houcheck.ui.viewmodel.FetchInfoStudentViewModelFactory
import com.kedokato_dev.houcheck.ui.viewmodel.FetchNameStudentViewModel
import com.kedokato_dev.houcheck.ui.viewmodel.FetchStudentNameState

@Composable
fun HomeScreen(navController: NavHostController) {
    var context = LocalContext.current

    val viewModel: FetchNameStudentViewModel = viewModel ()

    val fetchNameState = viewModel.fetchNameState.collectAsState()

    val sharedPreferences = remember {
        context.getSharedPreferences("sessionId", Context.MODE_PRIVATE)
    }

    val authRepository = remember { AuthRepository(sharedPreferences) }

    LaunchedEffect(Unit) {
        viewModel.fetchNameStudent(authRepository.getSessionId().toString())
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFF5F5F5))) {
        // Header Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF03A9F4), Color(0xFF0277BD))
                    )
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.no_avatar),
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                when (val state = fetchNameState.value) {
                    is FetchStudentNameState.Success -> {
                        Text(
                            text = state.studentName.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "MSV: ${state.studentName.studentId}",
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                    is FetchStudentNameState.Loading -> {
                        Text(
                            text = "Loading...",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    is FetchStudentNameState.Error -> {
                        Text(
                            text = "Error: ${state.message}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Red
                        )
                    }
                    else -> {
                        Text(
                            text = "No data",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            // GPA Section
            Box(
                modifier = Modifier
                    .background(Color.White, CircleShape)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "3.25 / 4 \uD83C\uDF96\uFE0F",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0277BD)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                "Chức năng chính",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            val features = listOf(
                FeatureItem("Xem lịch học", R.drawable.schedule_date_svgrepo_com),
                FeatureItem("Xem điểm học tập", R.drawable.a_plus_result_svgrepo_com),
                FeatureItem("Xem học phí", R.drawable.pig_piggy_bank_svgrepo_com),
                FeatureItem("Xem lịch thi", R.drawable.schedule_date_svgrepo_com),
                FeatureItem("Tin tức", R.drawable.newspaper_news_svgrepo_com),
                FeatureItem("Donate", R.drawable.coffe_svgrepo_com),
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxHeight()
            ) {
                items(features.size) { index ->
                    FeatureGridItem(item = features[index]) {
                        when (index) {
                            0 -> navController.navigate("studentInfo")
                            1 -> navController.navigate("result")
                            2 -> navController.navigate("tuition")
                            3 -> navController.navigate("exam_schedule")

                            4 -> Toast.makeText(
                                context,
                                "Chức năng đang phát triển",
                                Toast.LENGTH_SHORT
                            ).show()

                            5 -> Toast.makeText(
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
}

data class FeatureItem(
    val title: String,
    val iconRes: Int
)

@Composable
fun FeatureGridItem(item: FeatureItem, onClick: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable { onClick() }
            .padding(8.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(4.dp),
            modifier = Modifier.size(64.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF81C6FD)),
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = item.iconRes),
                    contentDescription = item.title,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = item.title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            color = Color(0xFF333333),
            modifier = Modifier.fillMaxWidth()
        )
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun StudentProfileScreenPreview() {
    val navController = NavHostController(context = LocalContext.current)
    HomeScreen(navController)
}