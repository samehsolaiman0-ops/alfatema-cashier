package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CustomerModel(
    @Json(name = "CID") val cid: Int,
    @Json(name = "Name") val name: String,
    @Json(name = "Phone") val phone: String
)
