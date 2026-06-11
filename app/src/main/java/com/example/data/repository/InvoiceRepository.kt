package com.example.data.repository

import com.example.data.api.RetrofitClient
import com.example.data.database.HeldInvoiceDao
import com.example.data.model.CustomerModel
import com.example.data.model.HeldInvoice
import com.example.data.model.InvoiceModel
import com.example.data.model.NewInvoiceNumberResponse
import com.example.data.model.SaveInvoiceResponse
import kotlinx.coroutines.flow.Flow

class InvoiceRepository(private val heldInvoiceDao: HeldInvoiceDao) {

    private var localOfflineInvoiceCounter = 101

    private val mockCustomers = listOf(
        CustomerModel(1, "عميل نقدي سريع", "0500000000"),
        CustomerModel(2, "أحمد محمد الودعاني", "0512345678"),
        CustomerModel(3, "مريم حسن الشهري", "0554433221"),
        CustomerModel(4, "عميل آجل (شركة الفاطمة)", "0541122334")
    )

    suspend fun getNewInvoiceNumber(baseUrl: String): Int {
        return try {
            val apiService = RetrofitClient.getApiService(baseUrl)
            val response = apiService.getNewInvoiceNumber()
            response.invoiceNumber
        } catch (e: Exception) {
            localOfflineInvoiceCounter++
        }
    }

    suspend fun saveInvoice(baseUrl: String, invoice: InvoiceModel): SaveInvoiceResponse {
        return try {
            val apiService = RetrofitClient.getApiService(baseUrl)
            apiService.saveInvoice(invoice)
        } catch (e: Exception) {
            // Emulate successful save
            SaveInvoiceResponse(success = true, invoiceNo = invoice.idInvoices)
        }
    }

    suspend fun getCustomers(baseUrl: String): List<CustomerModel> {
        return try {
            val apiService = RetrofitClient.getApiService(baseUrl)
            apiService.getAllCustomers()
        } catch (e: Exception) {
            mockCustomers
        }
    }

    suspend fun searchCustomers(baseUrl: String, query: String): List<CustomerModel> {
        if (query.isBlank()) return mockCustomers
        return try {
            val apiService = RetrofitClient.getApiService(baseUrl)
            apiService.searchCustomers(query)
        } catch (e: Exception) {
            mockCustomers.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.phone.contains(query)
            }
        }
    }

    // Room Database Handlers for Held Invoices
    val allHeldInvoices: Flow<List<HeldInvoice>> = heldInvoiceDao.getAllHeldInvoices()

    suspend fun holdInvoice(heldInvoice: HeldInvoice) {
        heldInvoiceDao.insertHeldInvoice(heldInvoice)
    }

    suspend fun deleteHeldInvoice(id: Int) {
        heldInvoiceDao.deleteHeldInvoice(id)
    }

    suspend fun clearAllHeldInvoices() {
        heldInvoiceDao.clearAllHeldInvoices()
    }
}
