package com.univalle.inventorywidget.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.univalle.inventorywidget.data.Product
import com.univalle.inventorywidget.data.ProductRepository

class HomeViewModel : ViewModel() {

    private val repository = ProductRepository()

    private val _productos = MutableLiveData<List<Product>>()
    val productos: LiveData<List<Product>> = _productos

    init {
        cargarProductos()
    }

    private fun cargarProductos() {
        val lista = repository.obtenerProductos()
        _productos.value = lista
    }
}
