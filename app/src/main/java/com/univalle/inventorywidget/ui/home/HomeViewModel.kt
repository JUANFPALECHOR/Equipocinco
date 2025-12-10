package com.univalle.inventorywidget.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.univalle.inventorywidget.data.Product
import com.univalle.inventorywidget.data.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel  
class HomeViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {
    val productos: LiveData<List<Product>> = repository.obtenerProductos()
}
