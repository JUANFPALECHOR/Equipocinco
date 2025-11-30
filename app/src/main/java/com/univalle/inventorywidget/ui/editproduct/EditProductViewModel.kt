package com.univalle.inventorywidget.ui.editproduct

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.univalle.inventorywidget.data.Product
import com.univalle.inventorywidget.data.ProductRepository
import kotlinx.coroutines.launch
import com.univalle.inventorywidget.widget.WidgetUpdateHelper


class EditProductViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProductRepository.getInstance(application.applicationContext)

    private val _updateResult = MutableLiveData<UpdateResult>()
    val updateResult: LiveData<UpdateResult> = _updateResult

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            try {
                repository.update(product)
                _updateResult.value = UpdateResult.Success
                // Actualizar widget después de actualizar
                WidgetUpdateHelper.updateWidget(getApplication())
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
