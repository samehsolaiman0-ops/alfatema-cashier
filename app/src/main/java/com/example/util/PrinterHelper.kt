package com.example.util

import com.example.data.model.InvoiceModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PrinterHelper {

    /**
     * Formats an invoice into a structured 32-character wide (58mm standard) text ticket
     */
    fun formatInvoiceFor58mm(invoice: InvoiceModel): String {
        val sb = StringBuilder()
        val width = 32

        // Header (Arabic/Center)
        sb.appendLine("       الفاطمة نيو       ")
        sb.appendLine("     نظام نقاط البيع     ")
        sb.appendLine("-".repeat(width))

        // Metadata
        sb.appendLine("رقم الفاتورة: ${invoice.idInvoices}")
        val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
        sb.appendLine("التاريخ: ${sdf.format(Date())}")
        sb.appendLine("الموظف: ${invoice.employeeName}")
        sb.appendLine("العميل: ${invoice.invCusNam}")
        sb.appendLine("طريقة الدفع: ${invoice.treqa}")
        sb.appendLine("=".repeat(width))

        // Columns
        sb.appendLine("المنتج          الكمية * السعر")
        sb.appendLine("-".repeat(width))

        for (item in invoice.items) {
            // Trim name if too long for 58mm sheet
            val cleanName = if (item.materialName.length > 14) {
                item.materialName.substring(0, 12) + ".."
            } else {
                item.materialName.padEnd(14)
            }
            val qtyPrice = "${item.quantity} * ${String.format(Locale.ENGLISH, "%.2f", item.sellingPrice)}"
            val lineTotal = String.format(Locale.ENGLISH, "%.2f", item.sellingPriceTotal)

            sb.appendLine(cleanName)
            // right-align values
            val spacerLimit = width - qtyPrice.length - lineTotal.length
            val spacer = if (spacerLimit > 0) " ".repeat(spacerLimit) else " "
            sb.appendLine("  $qtyPrice$spacer$lineTotal")
        }

        sb.appendLine("=".repeat(width))
        sb.appendLine(padLeftRight("المجموع:", String.format(Locale.ENGLISH, "%.2f", invoice.invTotal), width))
        val changeText = String.format(Locale.ENGLISH, "%.2f", invoice.invMtabke)
        val paidText = String.format(Locale.ENGLISH, "%.2f", invoice.invMdfo)
        sb.appendLine(padLeftRight("المدفوع:", paidText, width))
        sb.appendLine(padLeftRight("المتبقي للعميل:", changeText, width))
        sb.appendLine("=".repeat(width))

        // Footer
        sb.appendLine("      شكراً لتعاملكم معنا      ")
        sb.appendLine("   تطوير الفاطمة كاشير 1.0   ")
        sb.appendLine("\n\n")

        return sb.toString()
    }

    private fun padLeftRight(left: String, right: String, totalWidth: Int): String {
        val totalLength = left.length + right.length
        val spaceNeeded = totalWidth - totalLength
        val spaces = if (spaceNeeded > 0) " ".repeat(spaceNeeded) else " "
        return "$left$spaces$right"
    }
}
