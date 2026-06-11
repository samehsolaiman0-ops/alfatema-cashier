package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.CartItem
import java.util.Locale

@Composable
fun CartRow(
    item: CartItem,
    isAlternate: Boolean,
    onQtyIncrease: () -> Unit,
    onQtyDecrease: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isAlternate) {
        MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
    } else {
        MaterialTheme.colorScheme.background.copy(alpha = 0.4f)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(vertical = 8.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Name and Barcode Group
        Row(
            modifier = Modifier.weight(2.5f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("delete_${item.product.materialBarCode}")
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "حذف العنصر",
                    tint = MaterialTheme.colorScheme.error
                )
            }
            Row(
                modifier = Modifier.padding(start = 4.dp)
            ) {
                Text(
                    text = item.product.materialName,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2
                )
                Text(
                    text = " (${item.product.wahda})",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        // Price Column
        Text(
            text = String.format(Locale.ENGLISH, "%.2f", item.product.sellingPrice),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )

        // Quantity Selector Column
        Row(
            modifier = Modifier.weight(1.5f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = onQtyDecrease,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "تقليل",
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = "${item.quantity}",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            IconButton(
                onClick = onQtyIncrease,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "زيادة",
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Total Column
        Text(
            text = String.format(Locale.ENGLISH, "%.2f", item.total),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Black),
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.weight(1.2f),
            textAlign = TextAlign.End
        )
    }
}
