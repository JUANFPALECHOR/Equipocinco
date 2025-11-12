package com.univalle.inventorywidget.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ProductDao {

    @Query("SELECT * FROM products ORDER BY codigo ASC")
    fun getAll(): LiveData<List<Product>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(product: Product): Long

    @Update
    fun update(product: Product)

    @Delete
    fun delete(product: Product)

    @Query("SELECT * FROM products ORDER BY codigo ASC")
    fun getAllNow(): List<Product>

    @Query("SELECT COUNT(*) FROM products WHERE codigo = :codigo")
    fun existeCodigo(codigo: String): Int
}
