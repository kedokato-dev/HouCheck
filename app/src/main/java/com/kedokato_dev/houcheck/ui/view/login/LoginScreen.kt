package com.kedokato_dev.houcheck.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.kedokato_dev.houcheck.BuildConfig
import com.kedokato_dev.houcheck.R
import com.kedokato_dev.houcheck.local.dao.AppDatabase
import com.kedokato_dev.houcheck.local.entity.AccountEntity
import com.kedokato_dev.houcheck.ui.theme.HNOUDarkBlue
import com.kedokato_dev.houcheck.ui.theme.HNOULightBlue
import com.kedokato_dev.houcheck.ui.view.login.AccountViewModel
import com.kedokato_dev.houcheck.ui.view.login.AuthViewModel
import com.kedokato_dev.houcheck.ui.view.login.LoginState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@Composable
fun LoginScreen(navHostController: NavHostController) {
    val context = LocalContext.current
    val sharedPreferences = remember {
        context.getSharedPreferences("sessionId", Context.MODE_PRIVATE)
    }

    val authViewModel: AuthViewModel = hiltViewModel()
    val accountViewModel: AccountViewModel = hiltViewModel()
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

    LoginScreenContent(
        username = username,
        password = password,
        passwordVisible = passwordVisible,
        rememberLogin = rememberLogin,
        onUsernameChange = { username = it },
        onPasswordChange = { password = it },
        onPasswordVisibilityToggle = { passwordVisible = !passwordVisible },
        onRememberLoginChange = { rememberLogin = it },
        isLoading = loginState is LoginState.Loading,
        onLoginClick = {
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

        }
    )

    LaunchedEffect(loginState) {
        when (loginState) {
            is LoginState.Success -> {
                withContext(Dispatchers.IO) {

                    sharedPreferences.edit()
                        .putString("student_id", username)
                        .putString("password", password)
                        .apply()

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

// Add this new function for previewing the UI without dependencies
@Composable
fun LoginScreenContent(
    username: String = "",
    password: String = "",
    passwordVisible: Boolean = false,
    rememberLogin: Boolean = false,
    onUsernameChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onPasswordVisibilityToggle: () -> Unit = {},
    onRememberLoginChange: (Boolean) -> Unit = {},
    onLoginClick: () -> Unit = {},
    isLoading: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
            Image(
                painter = painterResource(id = R.drawable.logo_app),
                contentDescription = "School Logo",
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 16.dp)
            )

            Text(
                text = stringResource(R.string.app_title),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = HNOUDarkBlue,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = stringResource(R.string.slogan),
                style = MaterialTheme.typography.titleMedium,
                color = HNOULightBlue,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    MaterialTheme.colorScheme.background
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
                        onValueChange = onUsernameChange,
                        label = { Text(stringResource(R.string.username)) },
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
                        onValueChange = onPasswordChange,
                        label = { Text(stringResource(R.string.password)) },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_lock),
                                contentDescription = "Password",
                                tint = HNOUDarkBlue
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = onPasswordVisibilityToggle) {
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
                            onCheckedChange = onRememberLoginChange,
                            colors = CheckboxDefaults.colors(
                                checkedColor = HNOUDarkBlue,
                                uncheckedColor = Color.Gray
                            )
                        )
                        Text(
                            text = stringResource(R.string.remember_me),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 8.dp),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }


                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onLoginClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HNOUDarkBlue
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                stringResource(R.string.login),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = BuildConfig.VERSION,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.help),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(R.string.terms_of_use),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewLoginScreen() {
    LoginScreenContent()
}