package com.univalle.inventorywidget.data

class ProductRepository {

    fun obtenerProductos(): List<Product> {
        return listOf(
            Product(12, "zapatos", 23000.00),
            Product(3, "teclado", 75000.00),
            Product(45, "mouse", 50000.00)
        )
    }
}