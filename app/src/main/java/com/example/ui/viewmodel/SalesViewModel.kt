package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.CustomerModel
import com.example.data.model.HeldInvoice
import com.example.data.model.InvoiceItem
import com.example.data.model.InvoiceModel
import com.example.data.model.ProductModel
import com.example.di.AppModule
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface UiMessage {
    data class Success(val message: String) : UiMessage
    data class Error(val message: String) : UiMessage
}

data class CartItem(
    val product: ProductModel,
    var quantity: Int
) {
    val total: Double get() = product.sellingPrice * quantity
}

data class SalesUiState(
    val serverUrl: String = "",
    val employeeName: String = "",
    val activeInvoiceNumber: Int = 0,
    val cartList: List<CartItem> = emptyList(),
    val discount: Double = 0.0,
    val isDiscountPercent: Boolean = false,
    val categories: List<String> = emptyList(),
    val selectedCategory: String = "",
    val categoryProducts: List<ProductModel> = emptyList(),
    val isLoading: Boolean = false,
    val customerQuery: String = "",
    val customersList: List<CustomerModel> = emptyList(),
    val selectedCustomer: CustomerModel? = null,
    val paymentMethod: String = "كاش", // كاش | شبكة | آجل | أخرى
    val amountPaid: Double = 0.0,
    val isFinalizedSuccessfully: Boolean = false,
    val finalizedInvoiceText: String = "",
    val activeStep: String = "SALES" // LOGIN | SALES | FINALIZE | HOLDS | SETTINGS
)

class SalesViewModel : ViewModel() {

    private val productRepository = AppModule.getProductRepository()
    private val invoiceRepository = AppModule.getInvoiceRepository()
    private val settingsManager = AppModule.getSettingsManager()

    private val _uiState = MutableStateFlow(SalesUiState())
    val uiState: StateFlow<SalesUiState> = _uiState.asStateFlow()

    private val _messageFlow = MutableSharedFlow<UiMessage>()
    val messageFlow: SharedFlow<UiMessage> = _messageFlow.asSharedFlow()

    val heldInvoices = invoiceRepository.allHeldInvoices

    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val cartAdapter = moshi.adapter<List<InvoiceItem>>(
        Types.newParameterizedType(List::class.java, InvoiceItem::class.java)
    )

    init {
        loadSettingsAndInvoiceAndCategories()
    }

    private fun loadSettingsAndInvoiceAndCategories() {
        viewModelScope.launch {
            val url = settingsManager.serverUrl.first()
            val employee = settingsManager.employeeName.first()
            _uiState.update { it.copy(serverUrl = url, employeeName = employee) }

            // Get live categories
            _uiState.update { it.copy(isLoading = true) }
            try {
                val cats = productRepository.getCategories(url)
                val defaultCat = cats.firstOrNull() ?: ""
                val defaultProds = if (defaultCat.isNotEmpty()) {
                    productRepository.searchProducts(url, "").filter { it.sanf == defaultCat }
                } else emptyList()

                _uiState.update {
                    it.copy(
                        categories = cats,
                        selectedCategory = defaultCat,
                        categoryProducts = defaultProds
                    )
                }
            } catch (e: Exception) {
                _messageFlow.emit(UiMessage.Error("فشل تحميل الفئات: ${e.message}"))
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }

            // Always request fresh invoice number
            fetchNextInvoiceNumber()
        }
    }

    fun fetchNextInvoiceNumber() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val num = invoiceRepository.getNewInvoiceNumber(_uiState.value.serverUrl)
                _uiState.update { it.copy(activeInvoiceNumber = num) }
            } catch (e: Exception) {
                _messageFlow.emit(UiMessage.Error("فشل جلب رقم الفاتورة: ${e.message}"))
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    // Dynamic Server changes trigger
    fun updateServerConfig(url: String, employee: String) {
        viewModelScope.launch {
            settingsManager.setServerUrl(url)
            settingsManager.setEmployeeName(employee)
            _uiState.update { it.copy(serverUrl = url, employeeName = employee) }
            _messageFlow.emit(UiMessage.Success("تم تحديث الإعدادات وحفظها"))
            loadSettingsAndInvoiceAndCategories()
        }
    }

    fun selectCategory(cat: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(selectedCategory = cat, isLoading = true) }
            try {
                val prods = productRepository.searchProducts(_uiState.value.serverUrl, "").filter { it.sanf == cat }
                _uiState.update { it.copy(categoryProducts = prods) }
            } catch (e: Exception) {
                _messageFlow.emit(UiMessage.Error("فشل جلب منتجات الفئة: ${e.message}"))
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    // Barcode Scanning Implementation
    fun scanOrAddProductByBarcode(barcode: String) {
        if (barcode.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val product = productRepository.getProductByBarcode(_uiState.value.serverUrl, barcode)
                addProductToCart(product)
                _messageFlow.emit(UiMessage.Success("تمت إضافة: ${product.materialName}"))
            } catch (e: Exception) {
                _messageFlow.emit(UiMessage.Error(e.message ?: "المنتج غير متوفر بالرمز المدخل"))
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun addProductToCart(product: ProductModel) {
        val currentCart = _uiState.value.cartList.toMutableList()
        val existingIndex = currentCart.indexOfFirst { it.product.materialBarCode == product.materialBarCode }

        if (existingIndex != -1) {
            // Duplicate barcode increment quantity
            val item = currentCart[existingIndex]
            currentCart[existingIndex] = item.copy(quantity = item.quantity + 1)
        } else {
            currentCart.add(CartItem(product, 1))
        }
        _uiState.update { it.copy(cartList = currentCart) }
    }

    fun updateCartItemQuantity(barcode: String, change: Int) {
        val currentCart = _uiState.value.cartList.toMutableList()
        val index = currentCart.indexOfFirst { it.product.materialBarCode == barcode }
        if (index != -1) {
            val item = currentCart[index]
            val newQty = item.quantity + change
            if (newQty <= 0) {
                currentCart.removeAt(index)
            } else {
                currentCart[index] = item.copy(quantity = newQty)
            }
            _uiState.update { it.copy(cartList = currentCart) }
        }
    }

    fun deleteCartItem(barcode: String) {
        val currentCart = _uiState.value.cartList.toMutableList()
        currentCart.removeAll { it.product.materialBarCode == barcode }
        _uiState.update { it.copy(cartList = currentCart) }
    }

    fun setDiscount(value: Double, isPercent: Boolean) {
        _uiState.update { it.copy(discount = value, isDiscountPercent = isPercent) }
    }

    fun clearCart() {
        _uiState.update { it.copy(cartList = emptyList(), discount = 0.0, isDiscountPercent = false) }
    }

    fun cancelInvoice() {
        clearCart()
        fetchNextInvoiceNumber()
        viewModelScope.launch {
            _messageFlow.emit(UiMessage.Success("تم إلغاء الفاتورة وتصفير السلة"))
        }
    }

    // Metrics calculation
    val cartSubtotal: Double
        get() = _uiState.value.cartList.sumOf { it.total }

    val discountAmount: Double
        get() {
            val discount = _uiState.value.discount
            return if (_uiState.value.isDiscountPercent) {
                (cartSubtotal * discount) / 100.0
            } else {
                discount
            }
        }

    val netTotal: Double
        get() = (cartSubtotal - discountAmount).coerceAtLeast(0.0)

    val totalItemsCount: Int
        get() = _uiState.value.cartList.size

    val totalQuantityCount: Int
        get() = _uiState.value.cartList.sumOf { it.quantity }

    // Hold / Save locally (Room)
    fun holdCurrentInvoice(customerName: String) {
        if (_uiState.value.cartList.isEmpty()) {
            viewModelScope.launch { _messageFlow.emit(UiMessage.Error("السلة فارغة، لا يمكن تعليق فاتورة فارغة")) }
            return
        }
        viewModelScope.launch {
            try {
                val listInvoiceItems = _uiState.value.cartList.map {
                    InvoiceItem(
                        materialBarCode = it.product.materialBarCode,
                        sanf = it.product.sanf,
                        materialName = it.product.materialName,
                        wahda = it.product.wahda,
                        sellingPrice = it.product.sellingPrice,
                        quantity = it.quantity,
                        sellingPriceTotal = it.total,
                        rabh = it.product.rabh * it.quantity
                    )
                }
                val itemsJson = cartAdapter.toJson(listInvoiceItems) ?: "[]"
                val held = HeldInvoice(
                    customerName = customerName.ifBlank { "زبون معلق" },
                    employeeName = _uiState.value.employeeName,
                    paymentMethod = _uiState.value.paymentMethod,
                    subTotal = cartSubtotal,
                    discount = discountAmount,
                    netTotal = netTotal,
                    itemsJson = itemsJson
                )
                invoiceRepository.holdInvoice(held)
                clearCart()
                fetchNextInvoiceNumber()
                _messageFlow.emit(UiMessage.Success("تم تعليق الفاتورة بنجاح"))
            } catch (e: Exception) {
                _messageFlow.emit(UiMessage.Error("فشل تعليق الفاتورة: ${e.message}"))
            }
        }
    }

    fun restoreHeldInvoice(held: HeldInvoice) {
        viewModelScope.launch {
            try {
                clearCart()
                val items = cartAdapter.fromJson(held.itemsJson) ?: emptyList()
                val cartItems = items.map {
                    CartItem(
                        product = ProductModel(
                            materialBarCode = it.materialBarCode,
                            materialName = it.materialName,
                            sanf = it.sanf,
                            wahda = it.wahda,
                            sellingPrice = it.sellingPrice,
                            quantityAvailable = 100.0,
                            rabh = it.rabh / it.quantity
                        ),
                        quantity = it.quantity
                    )
                }
                _uiState.update {
                    it.copy(
                        cartList = cartItems,
                        discount = held.discount,
                        isDiscountPercent = false, // Apply absolute restored discount
                        paymentMethod = held.paymentMethod
                    )
                }
                // Delete once loaded back to cart
                invoiceRepository.deleteHeldInvoice(held.id)
                _messageFlow.emit(UiMessage.Success("تم استعادة الفاتورة المعلقة بنجاح"))
            } catch (e: Exception) {
                _messageFlow.emit(UiMessage.Error("فشل استرجاع الفاتورة: ${e.message}"))
            }
        }
    }

    fun deleteHeldInvoiceId(id: Int) {
        viewModelScope.launch {
            invoiceRepository.deleteHeldInvoice(id)
            _messageFlow.emit(UiMessage.Success("تم حذف الفاتورة المعلقة"))
        }
    }

    // Customer Queries
    fun searchCustomer(query: String) {
        _uiState.update { it.copy(customerQuery = query) }
        viewModelScope.launch {
            try {
                val list = invoiceRepository.searchCustomers(_uiState.value.serverUrl, query)
                _uiState.update { it.copy(customersList = list) }
            } catch (e: Exception) {
                // Squelch errors to preserve user typing
            }
        }
    }

    fun setPaymentMethod(method: String) {
        _uiState.update { it.copy(paymentMethod = method) }
    }

    fun setAmountPaid(amount: Double) {
        _uiState.update { it.copy(amountPaid = amount) }
    }

    fun finalizeAndPostInvoice(selectedCustomerName: String, method: String, paid: Double, discountVal: Double) {
        if (_uiState.value.cartList.isEmpty()) {
            viewModelScope.launch { _messageFlow.emit(UiMessage.Error("السلة فارغة")) }
            return
        }

        val needed = netTotal
        if (paid < needed && method == "كاش") {
            viewModelScope.launch { _messageFlow.emit(UiMessage.Error("المبلغ المدفوع أقل من الصافي المطلوب")) }
            return
        }

        _uiState.update { it.copy(isLoading = true, isFinalizedSuccessfully = false) }

        viewModelScope.launch {
            try {
                val finalItems = _uiState.value.cartList.map {
                    InvoiceItem(
                        materialBarCode = it.product.materialBarCode,
                        sanf = it.product.sanf,
                        materialName = it.product.materialName,
                        wahda = it.product.wahda,
                        sellingPrice = it.product.sellingPrice,
                        quantity = it.quantity,
                        sellingPriceTotal = it.total,
                        rabh = it.product.rabh * it.quantity
                    )
                }

                val invoiceObj = InvoiceModel(
                    idInvoices = _uiState.value.activeInvoiceNumber,
                    invCusNam = selectedCustomerName.ifBlank { "عميل نقدي" },
                    treqa = method,
                    invTotal = needed,
                    invMdfo = paid,
                    invMtabke = if (method == "كاش") (paid - needed).coerceAtLeast(0.0) else 0.0,
                    employeeName = _uiState.value.employeeName,
                    items = finalItems
                )

                val response = invoiceRepository.saveInvoice(_uiState.value.serverUrl, invoiceObj)

                if (response.success) {
                    val formattedText = com.example.util.PrinterHelper.formatInvoiceFor58mm(invoiceObj)
                    _uiState.update {
                        it.copy(
                            isFinalizedSuccessfully = true,
                            finalizedInvoiceText = formattedText
                        )
                    }
                    _messageFlow.emit(UiMessage.Success("تم حفظ الفاتورة بنجاح برقم ${response.invoiceNo ?: invoiceObj.idInvoices}"))
                } else {
                    _messageFlow.emit(UiMessage.Error("فشل حفظ الفاتورة بالخادم"))
                }
            } catch (e: Exception) {
                _messageFlow.emit(UiMessage.Error("خطأ بالخادم: ${e.message}"))
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun submitPinCode(pin: String): Boolean {
        // Simple security logic
        return pin == "1234" || pin == "0000"
    }

    fun setStep(step: String) {
        _uiState.update { it.copy(activeStep = step) }
    }
}
