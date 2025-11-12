package com.univalle.inventorywidget.data

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import androidx.lifecycle.LiveData
import com.univalle.inventorywidget.widget.InventoryWidgetProvider

class ProductRepository private constructor(private val context: Context) {

    private val productDao = AppDatabase.getInstance(context).productDao()

    fun obtenerProductos(): LiveData<List<Product>> = productDao.getAll()

    suspend fun insert(product: Product): Boolean {
        val existe = productDao.existeCodigo(product.codigo) > 0
        if (existe) return false // código duplicado
        productDao.insert(product)
        notificarCambioWidget(context)
        return true
    }

    suspend fun update(product: Product) {
        productDao.update(product)
        notificarCambioWidget(context)
    }

    suspend fun delete(product: Product) {
        productDao.delete(product)
        notificarCambioWidget(context)
    }

    fun obtenerProductosDirecto(): List<Product> = productDao.getAllNow()

    private fun notificarCambioWidget(context: Context) {
        val manager = AppWidgetManager.getInstance(context)
        val ids = manager.getAppWidgetIds(
            ComponentName(context, InventoryWidgetProvider::class.java)
        )
        InventoryWidgetProvider().onUpdate(context, manager, ids)
    }

    companion object {
        @Volatile private var INSTANCE: ProductRepository? = null
        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: ProductRepository(context).also { INSTANCE = it }
            }
    }
}
