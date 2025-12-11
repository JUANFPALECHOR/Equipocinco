package com.univalle.inventorywidget.data

data class Product(
    val codigo: String = "",
    val nombre: String = "",
    val precio: Double = 0.0,
    val cantidad: Int = 0
) {

    constructor() : this("", "", 0.0, 0)
}
