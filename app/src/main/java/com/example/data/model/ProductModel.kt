package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductModel(
    @Json(name = "MaterialBarCode") val materialBarCode: String,
    @Json(name = "MaterialName") val materialName: String,
    @Json(name = "SANF") val sanf: String,
    @Json(name = "WAHDA") val wahda: String,
    @Json(name = "SellingPrice") val sellingPrice: Double,
    @Json(name = "QuantityAvailable") val quantityAvailable: Double,
    @Json(name = "Rabh") val rabh: Double
)
