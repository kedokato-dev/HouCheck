package com.kedokato_dev.houcheck.ui.view.feedback

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.kedokato_dev.houcheck.local.dao.AppDatabase
import com.kedokato_dev.houcheck.local.entity.FeedbackEntity
import com.kedokato_dev.houcheck.network.model.Feedback
import com.kedokato_dev.houcheck.ui.components.LoadingComponent
import com.kedokato_dev.houcheck.ui.state.UiState
import com.kedokato_dev.houcheck.ui.theme.HNOUDarkBlue
import com.kedokato_dev.houcheck.ui.theme.HNOULightBlue
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackScreen(navController: NavHostController) {
    val context = LocalContext.current
    val db = AppDatabase(context)
    val viewModel: FetchFeedbackViewModel = hiltViewModel()
    val feedbackState by viewModel.feedbackState.collectAsState()
    val operationState by viewModel.operationState.collectAsState()

    var email by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var selectedFeedback by remember { mutableStateOf<Feedback?>(null) }
    var isEditing by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Function to reset edit state
    fun resetEditState() {
        isEditing = false
        selectedFeedback = null
        message = ""
    }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val student = db.studentDAO().getStudentById()
            if (student != null) {
                name = student.studentName ?: ""
                email = student.email ?: ""
                if (email.isNotEmpty()) {
                    viewModel.observeFeedbackByEmail(email)
                }
            }
        }
    }

    LaunchedEffect(operationState) {
        when (operationState) {
            is UiState.Success -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar((operationState as UiState.Success<String>).data)
                    if (email.isNotEmpty()) {
                        viewModel.observeFeedbackByEmail(email)
                        resetEditState()
                    }
                }
            }
            is UiState.Error -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar((operationState as UiState.Error).message)
                }
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Phản hồi") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = HNOUDarkBlue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isEditing) {
                Text(
                    text = "Chỉnh sửa phản hồi",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = HNOUDarkBlue
                )
            }
            
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                label = { Text(if (isEditing) "Nội dung chỉnh sửa" else "Thông tin phản hồi") },
                placeholder = { Text("Nhập thông tin phản hồi") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                enabled = true
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (isEditing) Arrangement.SpaceBetween else Arrangement.Center
            ) {
                if (isEditing) {
                    Button(
                        onClick = { resetEditState() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Gray,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Hủy")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = {
                            selectedFeedback?.let {
                                val feedbackEntity = FeedbackEntity(
                                    id = it.id,
                                    name = it.name,
                                    email = it.email,
                                    message = message,
                                    createdAt = it.createdAt
                                )
                                viewModel.updateFeedback(feedbackEntity)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HNOULightBlue,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text("Cập nhật")
                    }
                } else {
                    Button(
                        onClick = {
                            if (message.isNotBlank()) {
                                viewModel.sendFeedback(name, email, message)
                                message = ""
                            } else {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Vui lòng nhập thông tin phản hồi")
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HNOULightBlue,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text("Gửi phản hồi")
                    }
                }
            }

            if (feedbackState is UiState.Loading) {
                LoadingComponent(HNOULightBlue, "Đang tải phản hồi", "Vui lòng chờ trong giây lát")
            } else if (feedbackState is UiState.Success) {
                val feedbackList = (feedbackState as UiState.Success<List<Feedback>>).data
                if (feedbackList.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Chưa có phản hồi nào",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = Color.Gray
                        )
                    }
                } else {
                    Text(
                        text = "Lịch sử phản hồi",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = HNOUDarkBlue
                    )
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(feedbackList) { feedback ->
                            FeedbackItem(
                                feedback = feedback,
                                onClick = {},
                                modifier = Modifier.fillMaxWidth(),
                                onDelete = {
                                    viewModel.deleteFeedback(feedback.id)
                                },
                                onUpdate = {
                                    selectedFeedback = feedback
                                    message = feedback.message
                                    isEditing = true
                                }
                            )
                        }
                    }
                }
            } else if (feedbackState is UiState.Error) {
                Text(
                    text = (feedbackState as UiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun FeedbackItem(
    feedback: Feedback,
    onClick: () -> Unit,
    onDelete: (Feedback) -> Unit,
    onUpdate: (Feedback) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDropdownMenu by remember { mutableStateOf(false) }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = feedback.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                
                Box {
                    IconButton(onClick = { showDropdownMenu = true }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "More options",
                            tint = Color.Gray
                        )
                    }
                    
                    DropdownMenu(
                        expanded = showDropdownMenu,
                        onDismissRequest = { showDropdownMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Chỉnh sửa") },
                            leadingIcon = { 
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Edit",
                                    tint = HNOULightBlue
                                )
                            },
                            onClick = {
                                onUpdate(feedback)
                                showDropdownMenu = false
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Xóa") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color.Red
                                )
                            },
                            onClick = {
                                onDelete(feedback)
                                showDropdownMenu = false
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = "Date",
                    modifier = Modifier.size(16.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = feedback.createdAt,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = feedback.message,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}



@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FeedbackPreView(){
    val feedback = Feedback(
        id = "123",
        name = "John Doe",
        email = "",
        message = "This is a feedback message.",
        createdAt = "2023-10-01",)

    FeedbackItem(
        feedback = feedback,
        onClick = {},
        onDelete = {},
        onUpdate = {},
    )
}

