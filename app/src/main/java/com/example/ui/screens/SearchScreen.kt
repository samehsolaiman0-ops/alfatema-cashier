package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProductModel
import com.example.ui.components.SearchBar
import com.example.ui.viewmodel.SalesViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SalesViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<ProductModel>>(emptyList()) }
    var selectedProductForQty by remember { mutableStateOf<ProductModel?>(null) }
    var selectedQty by remember { mutableIntStateOf(1) }

    // Synchronize search query change
    LaunchedEffect(searchQuery) {
        searchResults = if (searchQuery.isBlank()) {
            emptyList()
        } else {
            com.example.di.AppModule.getProductRepository().searchProducts(uiState.serverUrl, searchQuery)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("بحث عن صنف بالاسم أو الباركود", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "رجوع", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            // Search Input Row
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholderText = "أدخل اسم الصنف أو امسح الكود الخاص به...",
                onSearchTriggered = {}
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Categories Table Headers
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp, horizontal = 8.dp)
            ) {
                Text("الصنف والمعلومات الأساسية", Modifier.weight(2.5f), fontWeight = FontWeight.Bold, color = Color.Gray, fontSize = 12.sp)
                Text("الفئة", Modifier.weight(1.2f), fontWeight = FontWeight.Bold, color = Color.Gray, fontSize = 12.sp)
                Text("السعر والوفرة", Modifier.weight(1.5f), fontWeight = FontWeight.Bold, color = Color.Gray, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Search list
            if (searchResults.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color.LightGray,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (searchQuery.isEmpty()) "ابدأ بكتابة اسم المنتج أو كوده للبحث" else "لا توجد نتائج مطابقة لـ \"$searchQuery\"",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(searchResults) { index, prod ->
                        val isAlternate = index % 2 == 0
                        Card(
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isAlternate) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedProductForQty = prod
                                    selectedQty = 1
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Info Column
                                Column(modifier = Modifier.weight(2.5f)) {
                                    Text(text = prod.materialName, fontWeight = FontWeight.Bold)
                                    Text(text = "رقم الباركود: ${prod.materialBarCode}", fontSize = 11.sp, color = Color.Gray)
                                }

                                // Category Column
                                Text(text = prod.sanf, modifier = Modifier.weight(1.2f), style = MaterialTheme.typography.bodyMedium)

                                // Price & Avail Column
                                Column(
                                    modifier = Modifier.weight(1.5f),
                                    horizontalAlignment = Alignment.End
                                ) {
                                    Text(
                                        text = "${String.format(Locale.ENGLISH, "%.2f", prod.sellingPrice)} ر.س",
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                    Text(
                                        text = "المتاح: ${prod.quantityAvailable.toInt()} ${prod.wahda}",
                                        fontSize = 11.sp,
                                        color = if (prod.quantityAvailable < 10) MaterialTheme.colorScheme.error else Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Interactive Quantity Dialog selection before inserting
    if (selectedProductForQty != null) {
        val prod = selectedProductForQty!!
        androidx.compose.ui.window.Dialog(onDismissRequest = { selectedProductForQty = null }) {
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
                        text = "إضافة الصنف إلى السلة",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = prod.materialName, fontWeight = FontWeight.Bold, fontSize = 18.sp, textAlign = TextAlign.Center)
                    Text(text = "${String.format(Locale.ENGLISH, "%.2f", prod.sellingPrice)} ر.س / ${prod.wahda}", color = Color.Gray)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Quantity selector counters
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(onClick = { if (selectedQty > 1) selectedQty-- }) {
                            Icon(Icons.Default.Remove, contentDescription = "إنقاص")
                        }
                        Text(
                            text = "$selectedQty",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                        IconButton(onClick = { selectedQty++ }) {
                            Icon(Icons.Default.Add, contentDescription = "زيادة")
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = { selectedProductForQty = null },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                        ) {
                            Text("إلغاء", color = Color.Black)
                        }

                        Button(
                            onClick = {
                                val itemToInsert = prod
                                for (i in 1..selectedQty) {
                                    viewModel.addProductToCart(itemToInsert)
                                }
                                selectedProductForQty = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            modifier = Modifier.testTag("submit_qty_select")
                        ) {
                            Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("تأكيد الإضافة", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
