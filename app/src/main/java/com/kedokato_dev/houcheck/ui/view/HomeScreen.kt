package com.kedokato_dev.houcheck.ui.view

import FetchInfoStudentViewModel
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
import androidx.compose.runtime.getValue
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
import com.kedokato_dev.houcheck.data.api.ApiClient
import com.kedokato_dev.houcheck.data.api.FetchInfoStudentService
import com.kedokato_dev.houcheck.data.api.FetchScoreService
import com.kedokato_dev.houcheck.data.repository.AuthRepository
import com.kedokato_dev.houcheck.data.repository.FetchScoreRepository
import com.kedokato_dev.houcheck.data.repository.FetchStudentInfoRepository
import com.kedokato_dev.houcheck.database.dao.AppDatabase
import com.kedokato_dev.houcheck.ui.viewmodel.FetchInfoStudentViewModelFactory
import com.kedokato_dev.houcheck.ui.viewmodel.FetchNameStudentViewModel
import com.kedokato_dev.houcheck.ui.viewmodel.FetchScoreState
import com.kedokato_dev.houcheck.ui.viewmodel.FetchScoreViewModel
import com.kedokato_dev.houcheck.ui.viewmodel.FetchScoreViewModelFactory
import com.kedokato_dev.houcheck.ui.viewmodel.FetchStudentNameState

@Composable
fun HomeScreen(navController: NavHostController) {
    var context = LocalContext.current


    val sharedPreferences = remember {
        context.getSharedPreferences("sessionId", Context.MODE_PRIVATE)
    }

    val fetchInfoStudentApi =
        remember { ApiClient.instance.create(FetchInfoStudentService::class.java) }
    val fetchScoreApi = remember { ApiClient.instance.create(FetchScoreService::class.java) }
    val studentDao = AppDatabase.buildDatabase(context).studentDAO()
    val scoreDao = AppDatabase.buildDatabase(context).scoreDAO()
    val repository = remember { FetchStudentInfoRepository(fetchInfoStudentApi, studentDao) }

    val fetchScoreRepo = remember { FetchScoreRepository(fetchScoreApi, scoreDao) }

    val viewModel: FetchInfoStudentViewModel = viewModel(
        factory = FetchInfoStudentViewModelFactory(repository)
    )

    val fetchScoreViewModel: FetchScoreViewModel = viewModel(
        factory = FetchScoreViewModelFactory(fetchScoreRepo)
    )


    val authRepository = remember { AuthRepository(sharedPreferences) }

    val fetchState by viewModel.fetchState.collectAsState()
    val fetchScoreState by fetchScoreViewModel.fetchState.collectAsState()

    // Màu sắc hiện đại
    val primaryColor = Color(0xFF03A9F4) // Tím đậm
    val secondaryColor = Color(0xFF6DB4EC) // Tím nhạt hơn
    val gradientColors = listOf(primaryColor, secondaryColor)


    LaunchedEffect(Unit) {
        viewModel.fetchStudentIfNeeded(authRepository.getSessionId().toString())
        fetchScoreViewModel.fetchScore(authRepository.getSessionId().toString())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
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
                when (fetchState) {
                    is FetchState.Success -> {
                        val student = (fetchState as FetchState.Success).student
                        Text(
                            text = student.studentName.toString(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "MSV: ${student.studentId}",
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }

                    is FetchState.Loading -> {
                        Text(
                            text = "Loading...",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    is FetchState.Error -> {
                        Text(
                            text = (fetchState as FetchState.Error).message,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
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
                when (fetchScoreState) {

                    is FetchScoreState.Success -> {
                        val score = (fetchScoreState as FetchScoreState.Success).scores
                        Text(
                            text = "GPA: ${score.gpa4} / 4\uD83D\uDD25",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0277BD)
                        )
                    }

                    is FetchScoreState.Loading -> {
                        Text(
                            text = "Loading...",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    is FetchScoreState.Error -> {
                        Text(
                            text = (fetchState as FetchState.Error).message,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    else -> {
                        Text(
                            text = "No data",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }


                }

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
                FeatureItem("Thông tin cá nhân", R.drawable.no_avatar),
                FeatureItem("Xem học phí", R.drawable.pig_piggy_bank_svgrepo_com),
                FeatureItem("Xem lịch thi", R.drawable.schedule_date_svgrepo_com),
                FeatureItem("Điểm rèn luyện", R.drawable.a_plus_result_svgrepo_com),
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
                            0 -> navController.navigate("home")
                            1 -> navController.navigate("score")
                            2 -> navController.navigate("studentInfo")
                            3 -> navController.navigate("home")
                            4 -> navController.navigate("home")
                            5 -> navController.navigate("training_score")

                            6 -> Toast.makeText(
                                context,
                                "Chức năng đang phát triển",
                                Toast.LENGTH_SHORT
                            ).show()

                            7 -> Toast.makeText(
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