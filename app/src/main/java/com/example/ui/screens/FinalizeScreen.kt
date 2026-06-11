package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.NumPad
import com.example.ui.viewmodel.SalesViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinalizeScreen(
    viewModel: SalesViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var paidAmountStr by remember { mutableStateOf("") }
    var customerNameInput by remember { mutableStateOf("") }
    var activeMethod by remember { mutableStateOf("كاش") }
    var dropdownExpanded by remember { mutableStateOf(false) }

    val totalToPay = viewModel.netTotal
    val changeAmount = ((paidAmountStr.toDoubleOrNull() ?: 0.0) - totalToPay).coerceAtLeast(0.0)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("إتمام عملية البيع ودفع الفاتورة", fontWeight = FontWeight.Black, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "رجوع", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        }
    ) { innerPadding ->
        Row(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            // Left Panel: Form Settings & NumPad (Weight 3)
            Column(
                modifier = Modifier
                    .weight(3f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
                    .padding(end = 12.dp)
            ) {
                // Info Summary Card
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "رقم الفاتورة", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            Text(text = "#${uiState.activeInvoiceNumber}", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black))
                        }
                        Column {
                            Text(text = "عدد الأصناف", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            Text(text = "${viewModel.totalQuantityCount} قطع", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "المبلغ الإجمالي المطلـوب", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                            Text(
                                text = "${String.format(Locale.ENGLISH, "%.2f", totalToPay)} ر.س",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Payment Method Selector Grid
                Text(
                    text = "طريقة الدفع",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val methods = listOf(
                        Triple("كاش", Icons.Default.Money, "نقدى"),
                        Triple("شبكة", Icons.Default.CreditCard, "بطاقة/مدى"),
                        Triple("آجل", Icons.Default.Schedule, "حساب آجل")
                    )

                    methods.forEach { (keyword, icon, label) ->
                        val isSelected = activeMethod == keyword
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                                .border(1.dp, if (isSelected) Color.Transparent else Color.LightGray, RoundedCornerShape(10.dp))
                                .clickable {
                                    activeMethod = keyword
                                    if (keyword != "كاش") {
                                        paidAmountStr = String.format(Locale.ENGLISH, "%.2f", totalToPay)
                                    } else {
                                        paidAmountStr = ""
                                    }
                                }
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = label,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Customer Selection Dropdown Autocomplete
                Text(
                    text = "العميل المرتبط",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = customerNameInput,
                        onValueChange = { newVal ->
                            customerNameInput = newVal
                            viewModel.searchCustomer(newVal)
                            dropdownExpanded = true
                        },
                        placeholder = { Text("بحث عن عميل أو تسجيل عميل جديد (اختياري)") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("customer_dropdown")
                    )

                    if (dropdownExpanded && uiState.customersList.isNotEmpty() && customerNameInput.isNotEmpty()) {
                        Card(
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .padding(top = 58.dp)
                        ) {
                            LazyColumn {
                                items(uiState.customersList) { cus ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                customerNameInput = cus.name
                                                dropdownExpanded = false
                                            }
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = cus.name, fontWeight = FontWeight.Bold)
                                        Text(text = cus.phone, color = Color.Gray, fontSize = 12.sp)
                                    }
                                    Divider()
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Cash Calculations
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "المبلغ المدفوع للـ كاشير:", fontWeight = FontWeight.Bold)
                            Text(
                                text = "${paidAmountStr.ifBlank { "0" }} ر.س",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        if (activeMethod == "كاش") {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "المتبقي للعميل (الباقي):", fontWeight = FontWeight.Bold)
                                Text(
                                    text = "${String.format(Locale.ENGLISH, "%.2f", changeAmount)} ر.س",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Black,
                                        color = if (changeAmount > 0.0) MaterialTheme.colorScheme.primary else Color.Gray
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Custom numeric keypad for typing calculations
                if (activeMethod == "كاش") {
                    NumPad(
                        onDigitClick = { digit ->
                            if (digit == "." && paidAmountStr.contains(".")) return@NumPad
                            paidAmountStr += digit
                        },
                        onDeleteClick = {
                            if (paidAmountStr.isNotEmpty()) {
                                paidAmountStr = paidAmountStr.substring(0, paidAmountStr.length - 1)
                            }
                        },
                        onOkClick = {
                            // Shortcut to fill exact sum
                            paidAmountStr = String.format(Locale.ENGLISH, "%.2f", totalToPay)
                        }
                    )
                }
            }

            // Right Panel: Thermal Receipt Simulator & Printing Actions (Weight 2)
            Column(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Receipt Box
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFCFCFB)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "شريط معاينة تيكيت الطباعة (58mm)",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )

                        if (uiState.isLoading) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        } else if (uiState.isFinalizedSuccessfully) {
                            // Render simulated thermal paper with monospaced style
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.White)
                                    .verticalScroll(rememberScrollState())
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = uiState.finalizedInvoiceText,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp,
                                    color = Color.Black,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "بانتظار تأكيد وحفظ الفاتورة لاستعراض تيكيت القبض",
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Actions Save / Print
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (uiState.isFinalizedSuccessfully) {
                        // Success block with simulation options
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Button(
                                onClick = { /* Perform actual thermal print */ },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(54.dp)
                            ) {
                                Icon(Icons.Default.Print, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("اطبع الفاتورة", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }

                        Button(
                            onClick = {
                                viewModel.clearCart()
                                viewModel.fetchNextInvoiceNumber()
                                onBack()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("payment_new_cart")
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("فتح فاتورة جديدة", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        // Standard confirm button
                        Button(
                            onClick = {
                                val pVal = paidAmountStr.toDoubleOrNull() ?: 0.0
                                viewModel.finalizeAndPostInvoice(
                                    selectedCustomerName = customerNameInput,
                                    method = activeMethod,
                                    paid = if (activeMethod != "كاش") totalToPay else pVal,
                                    discountVal = uiState.discount
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .testTag("confirm_save_invoice")
                        ) {
                            Text(
                                text = "تأكيد واستلام النقد ✓",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
