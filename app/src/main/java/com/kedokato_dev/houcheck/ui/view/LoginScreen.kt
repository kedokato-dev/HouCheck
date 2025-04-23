package com.kedokato_dev.houcheck.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.kedokato_dev.houcheck.R
import com.kedokato_dev.houcheck.data.repository.AuthRepository
import com.kedokato_dev.houcheck.ui.theme.HNOUDarkBlue
import com.kedokato_dev.houcheck.ui.theme.HNOULightBlue
import com.kedokato_dev.houcheck.ui.viewmodel.AuthViewModel
import com.kedokato_dev.houcheck.ui.viewmodel.AuthViewModelFactory
import com.kedokato_dev.houcheck.ui.viewmodel.LoginState

@Composable
fun LoginScreen(navHostController: NavHostController) {
    val context = LocalContext.current
    val sharedPreferences = remember {
        context.getSharedPreferences("sessionId", Context.MODE_PRIVATE)
    }

    val authRepository = remember { AuthRepository(sharedPreferences) }

    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(authRepository)
    )

    val loginState by authViewModel.loginState.collectAsState()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberLogin by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val shouldRemember = sharedPreferences.getBoolean("remember_login", false)
        if (shouldRemember) {
            val savedUsername = sharedPreferences.getString("saved_username", "") ?: ""
            username = savedUsername
            rememberLogin = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .windowInsetsPadding(WindowInsets.ime)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo and App Name
            Image(
                painter = painterResource(id = R.drawable._3_mo_ha_noi),
                contentDescription = "School Logo",
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 16.dp)
            )

            Text(
                text = "My HOU",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = HNOUDarkBlue,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Mở cơ hội học tập cho mọi người",
                style = MaterialTheme.typography.titleMedium,
                color = HNOULightBlue,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Login Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Đăng nhập",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )


                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Mã sinh viên") },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_person),
                                contentDescription = "Student ID",
                                tint = HNOUDarkBlue
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Text
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )

                    // Password Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Mật khẩu") },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_lock),
                                contentDescription = "Password",
                                tint = HNOUDarkBlue
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    painter = painterResource(
                                        id = if (passwordVisible) R.drawable.ic_visibility
                                        else R.drawable.ic_visibility_off
                                    ),
                                    contentDescription = "Toggle password visibility",
                                    tint = HNOUDarkBlue
                                )
                            }
                        },
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None
                        else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done,
                            keyboardType = KeyboardType.Password
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = rememberLogin,
                            onCheckedChange = { rememberLogin = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = HNOUDarkBlue,
                                uncheckedColor = Color.Gray
                            )
                        )
                        Text(
                            text = "Ghi nhớ đăng nhập",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }


                    Spacer(modifier = Modifier.height(24.dp))

                    // Login Button
                    Button(
                        onClick = {
                            authViewModel.login(username, password)
                            // Save credentials if remember login is checked
                            if (rememberLogin) {
                                sharedPreferences.edit()
                                    .putBoolean("remember_login", true)
                                    .putString("saved_username", username)
                                    .apply()
                            } else {
                                sharedPreferences.edit()
                                    .putBoolean("remember_login", false)
                                    .remove("saved_username")
                                    .apply()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HNOUDarkBlue
                        )
                    ) {
                        if (loginState is LoginState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                "ĐĂNG NHẬP",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Version 1.0.0", fontSize = 12.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Help Text
            Text(
                text = "Liên hệ phòng đào tạo nếu bạn gặp vấn đề khi đăng nhập",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }

        // Handle login states
        LaunchedEffect(loginState) {
            when (loginState) {
                is LoginState.Success -> {
                    navHostController.navigate("home")
                }

                is LoginState.Error -> {
                    Toast.makeText(
                        context,
                        "Lỗi đăng nhập: ${(loginState as LoginState.Error).message}",
                        Toast.LENGTH_LONG
                    ).show()
                }

                else -> {}
            }
        }
    }
}