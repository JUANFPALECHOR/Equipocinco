package com.univalle.inventorywidget.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey val codigo: String, 
    val nombre: String,
    val precio: Double,
    val cantidad: Int
)
