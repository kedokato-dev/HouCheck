import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.kedokato_dev.houcheck.ui.theme.colorTheme.AppColors
import com.kedokato_dev.houcheck.ui.theme.colorTheme.ThemeColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check

val colorOptionLists = listOf(
    AppColors.PinkSSR,
    AppColors.Pink,
    AppColors.Blue,
    AppColors.Amber,
    AppColors.Red,
    AppColors.Green,
    AppColors.Black,
    AppColors.Teal
)

@Composable
fun ColorPickerDialog(
    currentColor: ThemeColors,
    onDismiss: () -> Unit,
    onColorSelected: (ThemeColors) -> Unit,
    onReset: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Chọn màu chủ đề",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp),
                contentPadding = PaddingValues(vertical = 8.dp, horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(colorOptionLists) { colorOption ->
                    val isSelected = colorOption == currentColor

                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(colorOption.primary)
                            .clickable {
                                onColorSelected(colorOption)
                                onDismiss()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(24.dp)
                                    .zIndex(1f)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onReset()
                onDismiss()
            }) {
                Text("Reset")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
