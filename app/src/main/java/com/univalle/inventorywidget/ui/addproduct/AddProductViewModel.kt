package com.univalle.inventorywidget.ui.addproduct

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.univalle.inventorywidget.data.Product

class AddProductViewModel : ViewModel() {

    val codigo = MutableLiveData<String>()
    val nombre = MutableLiveData<String>()
    val precio = MutableLiveData<String>()
    val cantidad = MutableLiveData<String>()

    fun camposCompletos(): Boolean {
        return !codigo.value.isNullOrBlank()
                && !nombre.value.isNullOrBlank()
                && !precio.value.isNullOrBlank()
                && !cantidad.value.isNullOrBlank()
    }

    fun crearProducto(): Product {
        return Product(
            codigo = codigo.value!!,
            nombre = nombre.value!!,
            precio = precio.value!!.toDouble(),
            cantidad = cantidad.value!!.toInt()
        )
    }
}
