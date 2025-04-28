package com.kedokato_dev.houcheck.ui

import android.content.Context
import android.util.Log
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
import androidx.compose.ui.res.stringResource
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
import com.google.gson.internal.GsonBuildConfig
import com.kedokato_dev.houcheck.R
import com.kedokato_dev.houcheck.data.repository.AccountRepository
import com.kedokato_dev.houcheck.data.repository.AuthRepository
import com.kedokato_dev.houcheck.database.dao.AccountDAO
import com.kedokato_dev.houcheck.database.dao.AppDatabase
import com.kedokato_dev.houcheck.database.entity.AccountEntity
import com.kedokato_dev.houcheck.ui.theme.HNOUDarkBlue
import com.kedokato_dev.houcheck.ui.theme.HNOULightBlue
import com.kedokato_dev.houcheck.ui.viewmodel.AccountViewModel
import com.kedokato_dev.houcheck.ui.viewmodel.AccountViewModelFactory
import com.kedokato_dev.houcheck.ui.viewmodel.AuthViewModel
import com.kedokato_dev.houcheck.ui.viewmodel.AuthViewModelFactory
import com.kedokato_dev.houcheck.ui.viewmodel.LoginState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext



@Composable
fun LoginScreen(navHostController: NavHostController) {
    val context = LocalContext.current
    val sharedPreferences = remember {
        context.getSharedPreferences("sessionId", Context.MODE_PRIVATE)
    }

    val accountDao : AccountDAO = AppDatabase.buildDatabase(context).accountDAO()

    val authRepository = remember { AuthRepository(sharedPreferences) }
    val accountRepository = remember { AccountRepository(accountDao) }

    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(authRepository)
    )

    val accountViewModel: AccountViewModel = viewModel(
        factory = AccountViewModelFactory(accountRepository)
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
            val savedPassword = sharedPreferences.getString("password", "") ?: ""
            username = savedUsername
            password = savedPassword
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
                painter = painterResource(id = R.drawable.logo_app),
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
                text = "Nhanh chóng - Tiện lợi - Hiệu quả",
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
                            // Save credentials if remember login is checked
                            if (rememberLogin) {
                                sharedPreferences.edit()
                                    .putBoolean("remember_login", true)
                                    .putString("saved_username", username)
                                    .putString("saved_password", password)
                                    .apply()
                            } else {
                                sharedPreferences.edit()
                                    .putBoolean("remember_login", false)
                                    .remove("saved_username")
                                    .remove("saved_password")
                                    .apply()
                            }

                            authViewModel.login(username, password)

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

                    Text(text = stringResource(R.string.version_app) , fontSize = 12.sp, color = Color.Gray)
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
                    withContext(Dispatchers.IO) {
                        // save session prefs
                        sharedPreferences.edit()
                            .putString("student_id", username)
                            .putString("password", password)
                            .apply()

                        // clear or insert account as needed
                        val exists = accountViewModel.checkAccountExist(username)
                        if (!exists) {
                            AppDatabase.buildDatabase(context).clearAllTables()
                            accountViewModel.insertAccount(
                                AccountEntity(0, username, password)
                            )
                        }
                        if (accountViewModel.getAllAccounts() == null) {
                            accountViewModel.insertAccount(
                                AccountEntity(0, username, password)
                            )
                        }
                    }
                    navHostController.navigate("home")
                }
                is LoginState.Error -> {
                    Toast.makeText(
                        context,
                        "Lỗi đăng nhập: ${(loginState as LoginState.Error).message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                else -> { }
            }
        }
    }
}