package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class InvoiceModel(
    @Json(name = "IDInvoices") val idInvoices: Int,
    @Json(name = "InvCusNam") val invCusNam: String,
    @Json(name = "Treqa") val treqa: String,
    @Json(name = "InvTotal") val invTotal: Double,
    @Json(name = "InvMdfo") val invMdfo: Double,
    @Json(name = "InvMtabke") val invMtabke: Double,
    @Json(name = "EmployeeName") val employeeName: String,
    @Json(name = "Items") val items: List<InvoiceItem>
)

@JsonClass(generateAdapter = true)
data class InvoiceItem(
    @Json(name = "MaterialBarCode") val materialBarCode: String,
    @Json(name = "SANF") val sanf: String,
    @Json(name = "MaterialName") val materialName: String,
    @Json(name = "WAHDA") val wahda: String,
    @Json(name = "SellingPrice") val sellingPrice: Double,
    @Json(name = "Quantity") val quantity: Int,
    @Json(name = "SellingPriceTotal") val sellingPriceTotal: Double,
    @Json(name = "ML") val ml: String = "",
    @Json(name = "Rabh") val rabh: Double
)

@JsonClass(generateAdapter = true)
data class SaveInvoiceResponse(
    @Json(name = "Success") val success: Boolean,
    @Json(name = "InvoiceNo") val invoiceNo: Int? = null
)

@JsonClass(generateAdapter = true)
data class NewInvoiceNumberResponse(
    @Json(name = "InvoiceNumber") val invoiceNumber: Int
)
