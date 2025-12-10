package com.univalle.inventorywidget.data

import androidx.lifecycle.LiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    private val productDao: ProductDao
) {
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
}
