package com.univalle.inventorywidget.ui.editproduct

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
class EditProductViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val _updateResult = MutableLiveData<UpdateResult>()
    val updateResult: LiveData<UpdateResult> = _updateResult

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            try {
                repository.update(product)
                _updateResult.value = UpdateResult.Success
            } catch (e: Exception) {
                _updateResult.value = UpdateResult.Error(e.message ?: "Error desconocido")
            }
        }
    }

    sealed class UpdateResult {
        object Success : UpdateResult()
        data class Error(val message: String) : UpdateResult()
    }
}
