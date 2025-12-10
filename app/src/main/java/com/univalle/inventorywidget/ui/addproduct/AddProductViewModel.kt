package com.univalle.inventorywidget.ui.addproduct

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univalle.inventorywidget.data.Product
import com.univalle.inventorywidget.data.ProductRepository
import com.univalle.inventorywidget.widget.WidgetUpdateHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddProductViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val _insertResult = MutableLiveData<InsertResult>()
    val insertResult: LiveData<InsertResult> = _insertResult

    fun insertProduct(product: Product) {
        viewModelScope.launch {
            try {
                val exito = repository.insert(product)
                if (exito) {
                    _insertResult.value = InsertResult.Success
                } else {
                    _insertResult.value = InsertResult.DuplicateCode
                }
            } catch (e: Exception) {
                _insertResult.value = InsertResult.Error(e.message ?: "Error desconocido")
            }
        }
    }

    sealed class InsertResult {
        object Success : InsertResult()
        object DuplicateCode : InsertResult()
        data class Error(val message: String) : InsertResult()
    }
}
