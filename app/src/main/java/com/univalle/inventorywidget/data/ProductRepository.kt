package com.univalle.inventorywidget.data

import android.content.Context
import androidx.lifecycle.LiveData

class ProductRepository private constructor(private val context: Context) {

    private val productDao = AppDatabase.getInstance(context).productDao()

    fun obtenerProductos(): LiveData<List<Product>> = productDao.getAll()

    suspend fun insert(product: Product): Boolean {
        val existe = productDao.existeCodigo(product.codigo) > 0
        if (existe) return false
        productDao.insert(product)
        return true
    }

    suspend fun update(product: Product) {
        productDao.update(product)
    }

    suspend fun delete(product: Product) {
        productDao.delete(product)
    }

    suspend fun obtenerProductosDirecto(): List<Product> = productDao.getAllNow()

    companion object {
        @Volatile private var INSTANCE: ProductRepository? = null
        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: ProductRepository(context).also { INSTANCE = it }
            }
    }
}
