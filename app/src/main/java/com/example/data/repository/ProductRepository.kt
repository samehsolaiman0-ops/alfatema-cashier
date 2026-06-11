package com.example.data.repository

import com.example.data.api.RetrofitClient
import com.example.data.model.ProductModel

class ProductRepository {

    // Seed mock data for interactive local showcase when server is unreachable.
    private val mockProducts = listOf(
        ProductModel("62810011", "شوكولاتة جالاكسي كراميل", "مواد غذائية", "حبة", 5.50, 45.0, 1.20),
        ProductModel("62810022", "حليب نادك كامل الدسم 1 لتر", "مواد غذائية", "حبة", 7.00, 30.0, 1.50),
        ProductModel("62810033", "بيبسي علبة 325 مل", "مشروبات", "علبة", 2.50, 120.0, 0.50),
        ProductModel("62810044", "مياه هنا 330 مل", "مشروبات", "كرتون", 18.00, 80.0, 3.50),
        ProductModel("62810055", "عصير المراعي برتقال 1 لتر", "مشروبات", "حبة", 9.00, 25.0, 1.75),
        ProductModel("62810066", "شاي ربيع 100 كيس", "مواد غذائية", "حبة", 14.50, 15.0, 3.00),
        ProductModel("112233", "سمن المراعي طبيعي", "مواد غذائية", "حبة", 28.00, 10.0, 4.50),
        ProductModel("445566", "قهوة نسكافيه جولد", "مشروبات", "علبة", 35.00, 20.0, 6.00)
    )

    suspend fun getProductByBarcode(baseUrl: String, barcode: String): ProductModel {
        return try {
            val apiService = RetrofitClient.getApiService(baseUrl)
            apiService.getProductByBarcode(barcode)
        } catch (e: Exception) {
            // Unreachable fallbacks to mock barcodes for simulation.
            mockProducts.firstOrNull { it.materialBarCode == barcode }
                ?: throw Exception("المنتج ذو الرمز $barcode غير موجود")
        }
    }

    suspend fun searchProducts(baseUrl: String, query: String): List<ProductModel> {
        if (query.isBlank()) return mockProducts
        return try {
            val apiService = RetrofitClient.getApiService(baseUrl)
            apiService.searchProducts(query)
        } catch (e: Exception) {
            mockProducts.filter {
                it.materialName.contains(query, ignoreCase = true) ||
                        it.materialBarCode.contains(query)
            }
        }
    }

    suspend fun getCategories(baseUrl: String): List<String> {
        return try {
            val apiService = RetrofitClient.getApiService(baseUrl)
            apiService.getCategories()
        } catch (e: Exception) {
            mockProducts.map { it.sanf }.distinct()
        }
    }
}
