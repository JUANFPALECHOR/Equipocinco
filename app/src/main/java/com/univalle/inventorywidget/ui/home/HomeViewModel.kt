package com.univalle.inventorywidget.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.univalle.inventorywidget.data.Product
import com.univalle.inventorywidget.data.ProductRepository

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProductRepository.getInstance(application.applicationContext)
    val productos: LiveData<List<Product>> = repository.obtenerProductos()

}
