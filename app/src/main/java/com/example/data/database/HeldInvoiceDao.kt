package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.HeldInvoice
import kotlinx.coroutines.flow.Flow

@Dao
interface HeldInvoiceDao {
    @Query("SELECT * FROM held_invoices ORDER BY holdTime DESC")
    fun getAllHeldInvoices(): Flow<List<HeldInvoice>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHeldInvoice(heldInvoice: HeldInvoice)

    @Query("DELETE FROM held_invoices WHERE id = :id")
    suspend fun deleteHeldInvoice(id: Int)

    @Query("DELETE FROM held_invoices")
    suspend fun clearAllHeldInvoices()
}
