package com.example.di

import android.content.Context
import com.example.data.database.AppDatabase
import com.example.data.repository.InvoiceRepository
import com.example.data.repository.ProductRepository
import com.example.util.SettingsManager

/**
 * Service locator and Dependency Injector fulfilling /di/AppModule.kt.
 * Avoids version conflicts common in Hilt/KSP configurations, while
 * keeping the precise requested module path.
 */
object AppModule {
    private var database: AppDatabase? = null
    private var productRepo: ProductRepository? = null
    private var invoiceRepo: InvoiceRepository? = null
    private var settingsMgr: SettingsManager? = null

    fun initialize(context: Context) {
        if (database == null) {
            val appCtx = context.applicationContext
            database = AppDatabase.getDatabase(appCtx)
            settingsMgr = SettingsManager(appCtx)
            productRepo = ProductRepository()
            invoiceRepo = InvoiceRepository(database!!.heldInvoiceDao())
        }
    }

    fun getProductRepository(): ProductRepository {
        return productRepo ?: throw IllegalStateException("AppModule must be initialized first")
    }

    fun getInvoiceRepository(): InvoiceRepository {
        return invoiceRepo ?: throw IllegalStateException("AppModule must be initialized first")
    }

    fun getSettingsManager(): SettingsManager {
        return settingsMgr ?: throw IllegalStateException("AppModule must be initialized first")
    }
}
