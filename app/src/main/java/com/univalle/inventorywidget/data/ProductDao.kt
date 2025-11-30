package com.univalle.inventorywidget.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ProductDao {

    @Query("SELECT * FROM products ORDER BY codigo ASC")
    fun getAll(): LiveData<List<Product>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(product: Product): Long

    @Update
    suspend fun update(product: Product)

    @Delete
    suspend fun delete(product: Product)

    @Query("SELECT * FROM products ORDER BY codigo ASC")
    suspend fun getAllNow(): List<Product>

    @Query("SELECT COUNT(*) FROM products WHERE codigo = :codigo")
    suspend fun existeCodigo(codigo: String): Int
}
