package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "held_invoices")
data class HeldInvoice(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val customerName: String,
    val employeeName: String,
    val paymentMethod: String,
    val subTotal: Double,
    val discount: Double,
    val netTotal: Double,
    val holdTime: Long = System.currentTimeMillis(),
    val itemsJson: String // Serialized List<InvoiceItem>
)
