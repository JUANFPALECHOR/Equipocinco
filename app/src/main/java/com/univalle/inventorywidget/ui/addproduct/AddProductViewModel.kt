package com.univalle.inventorywidget.ui.addproduct

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.univalle.inventorywidget.data.Product
import com.univalle.inventorywidget.data.ProductRepository
import kotlinx.coroutines.launch
import com.univalle.inventorywidget.widget.WidgetUpdateHelper


class AddProductViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProductRepository.getInstance(application.applicationContext)

    // LiveData para comunicar el resultado al Fragment
    private val _insertResult = MutableLiveData<InsertResult>()
    val insertResult: LiveData<InsertResult> = _insertResult

    // CORRUTINAS (viewModelScope)
    fun insertProduct(product: Product) {
        viewModelScope.launch {
            try {
                val exito = repository.insert(product)
                if (exito) {
                    _insertResult.value = InsertResult.Success
                    // Actualizar widget después de insertar
                    WidgetUpdateHelper.updateWidget(getApplication())
                } else {
                    _insertResult.value = InsertResult.DuplicateCode
                }
            } catch (e: Exception) {
                _insertResult.value = InsertResult.Error(e.message ?: "Error desconocido")
            }
        }
    }


    // Clase sellada para manejar resultados
    sealed class InsertResult {
        object Success : InsertResult()
        object DuplicateCode : InsertResult()
        data class Error(val message: String) : InsertResult()
    }
}
