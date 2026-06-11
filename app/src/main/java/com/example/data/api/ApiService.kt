package com.example.data.api

import com.example.data.model.CustomerModel
import com.example.data.model.InvoiceModel
import com.example.data.model.NewInvoiceNumberResponse
import com.example.data.model.ProductModel
import com.example.data.model.SaveInvoiceResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("api/invoice/newnumber")
    suspend fun getNewInvoiceNumber(): NewInvoiceNumberResponse

    @POST("api/invoice/save")
    suspend fun saveInvoice(@Body invoice: InvoiceModel): SaveInvoiceResponse

    @GET("api/product/{barcode}")
    suspend fun getProductByBarcode(@Path("barcode") barcode: String): ProductModel

    @GET("api/product/search")
    suspend fun searchProducts(@Query("name") name: String): List<ProductModel>

    @GET("api/product/categories")
    suspend fun getCategories(): List<String>

    @GET("api/customer/all")
    suspend fun getAllCustomers(): List<CustomerModel>

    @GET("api/customer/search")
    suspend fun searchCustomers(@Query("q") query: String): List<CustomerModel>
}
