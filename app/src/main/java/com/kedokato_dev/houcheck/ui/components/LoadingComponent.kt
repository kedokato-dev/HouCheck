package com.kedokato_dev.houcheck.ui.components


import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun LoadingComponent(
    primaryColor: Color,
    title: String,
    subtitle: String = "Vui lòng chờ trong giây lát",
    verticalPadding: Int = 64,
    progressIndicatorSize: Int = 56,
    titleColor: Color = Color(0xFF555555),
    subtitleColor: Color = Color.Gray
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = verticalPadding.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            color = primaryColor,
            strokeWidth = 4.dp,
            modifier = Modifier.size(progressIndicatorSize.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = titleColor
        )

        if (subtitle.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = subtitleColor
            )
        }
    }
}