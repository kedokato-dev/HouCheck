package com.kedokato_dev.houcheck.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Refresh

/**
 * Reusable empty state component that can be used throughout the app
 *
 * @param icon Icon to display (defaults to calendar icon)
 * @param title Main title text for empty state (defaults to "Chưa có dữ liệu")
 * @param subtitle Optional subtitle text (defaults to "Bấm nút để tải dữ liệu")
 * @param buttonText Text for the action button (defaults to "Tải dữ liệu")
 * @param buttonIcon Icon for the action button (defaults to refresh icon)
 * @param onButtonClick Action to perform when button is clicked
 * @param primaryColor Theme color for icon and button (defaults to blue)
 * @param showButton Whether to show the action button (defaults to true)
 * @param verticalPadding Padding to apply vertically (defaults to 32.dp)
 * @param iconSize Size of the icon (defaults to 80.dp)
 * @param titleColor Color for the title text
 * @param subtitleColor Color for the subtitle text
 */
@Composable
fun EmptyStateComponent(
    icon: ImageVector = Icons.Default.DateRange,
    title: String = "Chưa có dữ liệu",
    subtitle: String = "Bấm nút bên dưới để tải dữ liệu",
    buttonText: String = "Tải dữ liệu",
    buttonIcon: ImageVector = Icons.Default.Refresh,
    onButtonClick: () -> Unit,
    primaryColor: Color = Color(0xFF03A9F4),
    showButton: Boolean = true,
    verticalPadding: Int = 32,
    iconSize: Int = 80,
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
        // Icon placeholder for an illustration
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = primaryColor.copy(alpha = 0.7f),
            modifier = Modifier
                .size(iconSize.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            color = titleColor
        )

        if (subtitle.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = subtitleColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }

        if (showButton) {
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onButtonClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryColor,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .height(48.dp)
                    .padding(horizontal = 16.dp)
            ) {
                Icon(
                    imageVector = buttonIcon,
                    contentDescription = buttonText,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = buttonText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}