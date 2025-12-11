package com.univalle.inventorywidget.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univalle.inventorywidget.data.Product
import com.univalle.inventorywidget.data.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

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
            } catch (e: Exception) {
                _deleteResult.value = false
            }
        }
    }
}
