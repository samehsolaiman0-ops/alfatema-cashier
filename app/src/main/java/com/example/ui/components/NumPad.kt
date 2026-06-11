package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NumPad(
    onDigitClick: (String) -> Unit,
    onDeleteClick: () -> Unit,
    onOkClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val keys = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf(".", "0")
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        keys.forEachIndexed { rowIndex, row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                row.forEach { digit ->
                    NumKey(
                        text = digit,
                        onClick = { onDigitClick(digit) },
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1.8f)
                            .padding(4.dp)
                    )
                }

                // Append special keys on rows 3 & 4
                if (rowIndex == 3) {
                    // Backspace Key
                    SpecialKey(
                        onClick = onDeleteClick,
                        iconContent = {
                            Icon(
                                imageVector = Icons.Default.Backspace,
                                contentDescription = "حذف الكلمة",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1.8f)
                            .padding(4.dp)
                    )

                    // Confirm Key
                    SpecialKey(
                        onClick = onOkClick,
                        iconContent = {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "تأكيد",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        },
                        containerColor = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1.8f)
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun NumKey(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun SpecialKey(
    onClick: () -> Unit,
    iconContent: @Composable () -> Unit,
    containerColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.surfaceVariant,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(containerColor)
            .clickable(onClick = onClick)
    ) {
        iconContent()
    }
}
