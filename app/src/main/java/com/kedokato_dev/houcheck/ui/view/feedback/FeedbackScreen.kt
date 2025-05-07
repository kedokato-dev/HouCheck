package com.kedokato_dev.houcheck.ui.view.feedback

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.kedokato_dev.houcheck.network.model.Feedback
import com.kedokato_dev.houcheck.repository.FeedBackRepository
import com.kedokato_dev.houcheck.local.dao.AppDatabase
import com.kedokato_dev.houcheck.ui.components.LoadingComponent
import com.kedokato_dev.houcheck.ui.state.UiState
import com.kedokato_dev.houcheck.ui.theme.HNOULightBlue
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackScreen(navController: NavHostController) {
    val context = LocalContext.current
    val repository = FeedBackRepository()
    val viewModelFactory = FetchFeedbackViewModelFactory(repository)
    val viewModel: FetchFeedbackViewModel = viewModel(factory = viewModelFactory)
    val db = AppDatabase(context)

    val feedbackState by viewModel.feedbackState.collectAsState()
    val operationState by viewModel.operationState.collectAsState()


    var email by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(false) }
    var selectedFeedback by remember { mutableStateOf<Feedback?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()


    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val student = db.studentDAO().getStudentById()
            if (student != null) {
                name = student.studentName ?: ""
                email = student.email ?: ""
                // Fetch feedback history once we have the email
                if (email.isNotEmpty()) {
                    viewModel.getFeedbackByEmail(email)
                }
            }
        }
    }





    LaunchedEffect(operationState) {
        when (operationState) {
            is UiState.Success -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar((operationState as UiState.Success<String>).data)
                    viewModel.resetOperationState()
                    if (email.isNotEmpty()) {
                        viewModel.getFeedbackByEmail(email)
                    }
                }
            }
            is UiState.Error -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar((operationState as UiState.Error).message)
                    viewModel.resetOperationState()
                }
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Feedback") },
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
            OutlinedTextField(
                value = name,
                onValueChange = { },
                enabled = false,
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = email,
                onValueChange = { },
                label = { Text("Email") },
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                label = { Text(if (isEditing) "Update Message" else "Message") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        if (isEditing) {
                            viewModel.updateFeedback(message)
                            isEditing = false
                            message = ""
                        } else {
                            viewModel.sendFeedback(name, email, message)
                            message = ""
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        if (isEditing) Icons.Default.Edit else Icons.Default.Send,
                        contentDescription = if (isEditing) "Cập nhật" else "Gửi"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isEditing) "Cập nhật" else "Gửi ý kiến")
                }

                if (isEditing) {
                    Button(
                        onClick = {
                            viewModel.deleteFeedback()
                            isEditing = false
                            message = ""
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Xóa")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Xóa")
                    }
                } else {
                    OutlinedButton(
                        onClick = {
                            if (email.isNotEmpty()) {
                                viewModel.getFeedbackByEmail(email)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Xen lịch sử")
                    }
                }
            }

            // Cancel editing button
            if (isEditing) {
                TextButton(
                    onClick = {
                        isEditing = false
                        message = ""
                        selectedFeedback = null
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Cancel Editing")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (feedbackState) {
                is UiState.Loading -> {
                  Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                      LoadingComponent(HNOULightBlue, "Đang tải lịch sử gửi ý kiến")
                    }

                }
                is UiState.Success -> {
                    val feedbacks = (feedbackState as UiState.Success<List<Feedback>>).data
                    if (feedbacks.isEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("No feedback found")
                        }
                    } else {
                        Text(
                            "Lịch sử gửi ý kiến",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(feedbacks) { feedback ->
                                FeedbackItem(
                                    feedback = feedback,
                                    onClick = {
                                        selectedFeedback = feedback
                                        viewModel.setCurrentFeedback(feedback.id)
                                        isEditing = true
                                        message = feedback.message
                                    }
                                )
                            }
                        }
                    }
                }
                is UiState.Error -> {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            (feedbackState as UiState.Error).message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                else -> { /* Idle state - no content to show */ }
            }
        }
    }
}
@Composable
fun FeedbackItem(
    feedback: Feedback,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(8.dp), // Increased padding for better spacing
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), // Increased elevation for more depth
        shape = RoundedCornerShape(12.dp), // More rounded corners
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface, // Explicit background color
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Optional badge for feedback type/status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = "Bài đánh giá", // Assuming there's a type field
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // Main message with enhanced styling
            Text(
                text = feedback.message,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = HNOULightBlue
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar and name
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = HNOULightBlue,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Text(
                            text = feedback.name.firstOrNull()?.toString() ?: "?",
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.labelMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxSize().wrapContentHeight(Alignment.CenterVertically)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = feedback.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Date with icon
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Date",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = feedback.createdAt, // A function to format the date nicely
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
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

//    FeedbackScreen(navController = NavHostController(LocalContext.current))

    FeedbackItem(
        feedback = feedback,
        onClick = {}
    )
}

