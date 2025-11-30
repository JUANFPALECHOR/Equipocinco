package com.univalle.inventorywidget.ui.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.univalle.inventorywidget.data.Product
import com.univalle.inventorywidget.data.ProductRepository
import kotlinx.coroutines.launch
import com.univalle.inventorywidget.widget.WidgetUpdateHelper


class ProductDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProductRepository.getInstance(application.applicationContext)

    private val _product = MutableLiveData<Product?>()
    val product: LiveData<Product?> = _product

    private val _deleteResult = MutableLiveData<Boolean>()
    val deleteResult: LiveData<Boolean> = _deleteResult

    fun loadProduct(codigo: String) {
        viewModelScope.launch {
            val producto = repository.obtenerProductosDirecto().find { it.codigo == codigo }
            _product.value = producto
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            try {
                repository.delete(product)
                _deleteResult.value = true
                // Actualizar widget después de eliminar
                WidgetUpdateHelper.updateWidget(getApplication())
            } catch (e: Exception) {
                _deleteResult.value = false
            }
        }
    }

}
