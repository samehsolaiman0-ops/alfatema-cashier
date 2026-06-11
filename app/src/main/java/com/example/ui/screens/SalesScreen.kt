package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LibraryAdd
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CartRow
import com.example.ui.components.ProductCard
import com.example.ui.components.SearchBar
import com.example.ui.components.TotalBar
import com.example.ui.viewmodel.SalesViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesScreen(
    viewModel: SalesViewModel,
    onNavigateSetting: () -> Unit,
    onNavigateHoldList: () -> Unit,
    onNavigateSearch: () -> Unit,
    onTriggerFinalize: () -> Unit,
    onTriggerLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var barcodeInput by remember { mutableStateOf("") }
    var timeString by remember { mutableStateOf("") }
    var holdCustomerName by remember { mutableStateOf("") }
    var showHoldNoteDialog by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Collect messages
    LaunchedEffect(key1 = true) {
        viewModel.messageFlow.collect { message ->
            coroutineScope.launch {
                when (message) {
                    is com.example.ui.viewmodel.UiMessage.Success -> snackbarHostState.showSnackbar(message.message)
                    is com.example.ui.viewmodel.UiMessage.Error -> snackbarHostState.showSnackbar(message.message)
                }
            }
        }
    }

    // Refresh live time string
    LaunchedEffect(key1 = true) {
        while (true) {
            val sdf = SimpleDateFormat("hh:mm aa", Locale.getDefault())
            timeString = sdf.format(Date())
            kotlinx.coroutines.delay(1000)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "رقم الفاتورة: #${uiState.activeInvoiceNumber}",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                                color = Color.White
                            )
                            Text(
                                text = "الكاشير: ${uiState.employeeName}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }

                        // Date and Live clock
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = timeString,
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            val dateSdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            Text(
                                text = dateSdf.format(Date()),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = onNavigateSearch) {
                        Icon(Icons.Default.Search, contentDescription = "بحث المنتجات", tint = Color.White)
                    }
                    IconButton(onClick = onNavigateHoldList) {
                        Icon(Icons.Default.History, contentDescription = "الفواتير المعلقة", tint = Color.White)
                    }
                    IconButton(onClick = onNavigateSetting) {
                        Icon(Icons.Default.Settings, contentDescription = "الإعدادات", tint = Color.White)
                    }
                    IconButton(onClick = onTriggerLogout) {
                        Icon(Icons.Default.Logout, contentDescription = "خروج", tint = Color.White)
                    }
                }
            )
        },
        bottomBar = {
            // Totals and Discount fields sticky bottom-anchored
            TotalBar(
                subtotal = viewModel.cartSubtotal,
                discountAmount = viewModel.discountAmount,
                netTotal = viewModel.netTotal,
                itemsCount = viewModel.totalItemsCount,
                quantityCount = viewModel.totalQuantityCount,
                onDiscountChanged = { value, isPercent ->
                    viewModel.setDiscount(value, isPercent)
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Barcode input segment
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = barcodeInput,
                    onValueChange = { barcodeInput = it },
                    placeholder = { Text("امسح الباركود أو أدخله يدوياً") },
                    leadingIcon = { Icon(Icons.Default.QrCodeScanner, contentDescription = null) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("barcode_input")
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        viewModel.scanOrAddProductByBarcode(barcodeInput)
                        barcodeInput = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(56.dp)
                ) {
                    Text("إضافة")
                }
            }

            // Quick Category Tab Horizontal Scroll
            if (uiState.categories.isNotEmpty()) {
                ScrollableTabRow(
                    selectedTabIndex = uiState.categories.indexOf(uiState.selectedCategory).coerceAtLeast(0),
                    edgePadding = 12.dp,
                    indicator = {},
                    divider = {},
                    containerColor = Color.Transparent,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    uiState.categories.forEach { categoryName ->
                        val isSelected = uiState.selectedCategory == categoryName
                        Tab(
                            selected = isSelected,
                            onClick = { viewModel.selectCategory(categoryName) },
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                                )
                                .padding(vertical = 6.dp, horizontal = 12.dp)
                        ) {
                            Text(
                                text = categoryName,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Cart and Quick Action panels split
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                // Cart DataGrid (Weight 3)
                Column(
                    modifier = Modifier
                        .weight(3f)
                        .fillMaxHeight()
                        .padding(bottom = 4.dp)
                ) {
                    // Header Labels
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                            )
                            .padding(vertical = 8.dp, horizontal = 12.dp)
                    ) {
                        Text(
                            text = "اسم المنتج (الوحدة)",
                            Modifier.weight(2.5f),
                            textAlign = TextAlign.Start,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "السعر",
                            Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "الكمية",
                            Modifier.weight(1.5f),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "الإجمالي",
                            Modifier.weight(1.2f),
                            textAlign = TextAlign.End,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    if (uiState.cartList.isEmpty()) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                                .padding(24.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                    modifier = Modifier.size(56.dp)
                                )
                                Spacer(modifier = Modifier.height(11.dp))
                                Text(
                                    text = "سلة المبيعات فارغة حالياً.",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.Gray
                                )
                                Text(
                                    text = "تصفح الفئات أعلاه لإضافة منتجات، أو امسح الباركود.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray.copy(alpha = 0.8f)
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                        ) {
                            itemsIndexed(uiState.cartList) { index, item ->
                                CartRow(
                                    item = item,
                                    isAlternate = index % 2 == 0,
                                    onQtyIncrease = { viewModel.updateCartItemQuantity(item.product.materialBarCode, 1) },
                                    onQtyDecrease = { viewModel.updateCartItemQuantity(item.product.materialBarCode, -1) },
                                    onDelete = { viewModel.deleteCartItem(item.product.materialBarCode) }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Quick Product Side Panel and Checkout Actions (Weight 2)
                Column(
                    modifier = Modifier
                        .weight(2.1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Category Quick Sheet items
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = "منتجات سريعة: ${uiState.selectedCategory}",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )

                            if (uiState.categoryProducts.isEmpty()) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Text("لا توجد منتجات متوفرة", color = Color.Gray)
                                }
                            } else {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(uiState.categoryProducts) { prod ->
                                        ProductCard(
                                            product = prod,
                                            onClick = {
                                                viewModel.addProductToCart(prod)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Checkout Actions Block
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Hold & Recall row
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Button(
                                onClick = { showHoldNoteDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                                border = ButtonDefaults.outlinedButtonBorder,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                            ) {
                                Icon(Icons.Default.PauseCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("تعليق", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = onNavigateHoldList,
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                                border = ButtonDefaults.outlinedButtonBorder,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("استرجاع", color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Cancel & Complete Row
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Button(
                                onClick = { viewModel.cancelInvoice() },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp)
                            ) {
                                Icon(Icons.Default.Cancel, contentDescription = null, tint = MaterialTheme.colorScheme.onErrorContainer)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("إلغاء", color = MaterialTheme.colorScheme.onErrorContainer, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = onTriggerFinalize,
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1.3f)
                                    .height(52.dp)
                                    .testTag("finalize_invoice")
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("إنهاء ودفع", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    // Hold Invoice note dialog
    if (showHoldNoteDialog) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showHoldNoteDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "تعليق الفاتورة الحالية",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = holdCustomerName,
                        onValueChange = { holdCustomerName = it },
                        label = { Text("ملاحظة أو اسم العميل للتعليق") },
                        placeholder = { Text("مثال: طاولة رقم 4") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = { showHoldNoteDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("رجوع", color = Color.Black)
                        }

                        Button(
                            onClick = {
                                viewModel.holdCurrentInvoice(holdCustomerName)
                                holdCustomerName = ""
                                showHoldNoteDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("تأكيد التعليق", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
