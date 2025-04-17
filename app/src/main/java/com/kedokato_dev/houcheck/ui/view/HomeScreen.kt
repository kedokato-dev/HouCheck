package com.kedokato_dev.houcheck.ui.view
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.kedokato_dev.houcheck.R

@Composable
fun HomeScreen(navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF03A9F4)) // Blue background color
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background), // Replace with your image resource
            contentDescription = "Avatar",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(16.dp))

        // User Information
        Column {
            Text(
                text = "Lê Khánh Linh",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "MSV: B21DCDT016",
                fontSize = 14.sp,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // GPA
        Box(
            modifier = Modifier
                .background(Color.White, CircleShape)
                .padding(horizontal = 12.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "3.25/4",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF000000) // Red text color
            )
        }
    }
}

@Preview(showBackground =  true, showSystemUi = true)
@Composable
fun StudentProfileScreenPreview() {
    val navController = NavHostController(context = LocalContext.current)
    HomeScreen(navController)
}

