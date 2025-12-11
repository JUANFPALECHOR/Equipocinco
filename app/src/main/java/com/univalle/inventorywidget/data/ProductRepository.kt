package com.univalle.inventorywidget.data

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import com.univalle.inventorywidget.widget.InventoryWidgetProvider
import android.content.Intent

@Singleton
class ProductRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val context: Context
) {
    private val productosCollection = firestore.collection("productos")
    private val _productos = MutableLiveData<List<Product>>()
    private val prefs: SharedPreferences =
        context.getSharedPreferences("inventory_prefs", Context.MODE_PRIVATE)

    init {
        // Escuchar cambios en tiempo real
        productosCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                _productos.value = emptyList()
                return@addSnapshotListener
            }
            val lista = snapshot?.documents?.mapNotNull {
                it.toObject(Product::class.java)
            } ?: emptyList()
            _productos.value = lista

            // 💰 CALCULAR Y GUARDAR SALDO TOTAL
            calcularYGuardarSaldo(lista)
        }
    }

    private fun calcularYGuardarSaldo(productos: List<Product>) {
        // Saldo = suma de (precio × cantidad) de cada producto
        val saldoTotal = productos.sumOf { it.precio * it.cantidad }

        // Guardar en SharedPreferences
        prefs.edit().putFloat("saldo_total", saldoTotal.toFloat()).apply()

        // Actualizar widget
        actualizarWidget()
    }

    private fun actualizarWidget() {
        val intent = Intent(context, InventoryWidgetProvider::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        }
        val ids = AppWidgetManager.getInstance(context).getAppWidgetIds(
            ComponentName(context, InventoryWidgetProvider::class.java)
        )
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        context.sendBroadcast(intent)
    }

    fun obtenerProductos(): LiveData<List<Product>> = _productos

    suspend fun insert(product: Product): Boolean {
        return try {
            // Verificar si el código ya existe
            val existe = productosCollection
                .whereEqualTo("codigo", product.codigo)
                .get()
                .await()
                .isEmpty.not()

            if (existe) return false

            // Guardar en Firestore
            productosCollection.add(product).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun update(product: Product) {
        try {
            // Buscar documento por código
            val query = productosCollection
                .whereEqualTo("codigo", product.codigo)
                .get()
                .await()

            if (!query.isEmpty) {
                val docId = query.documents[0].id
                productosCollection.document(docId).set(product).await()
            }
        } catch (e: Exception) {
            // Manejar error
        }
    }

    suspend fun delete(product: Product) {
        try {
            // Buscar documento por código
            val query = productosCollection
                .whereEqualTo("codigo", product.codigo)
                .get()
                .await()

            if (!query.isEmpty) {
                val docId = query.documents[0].id
                productosCollection.document(docId).delete().await()
            }
        } catch (e: Exception) {
            // Manejar error
        }
    }

    suspend fun obtenerProductosDirecto(): List<Product> {
        return try {
            productosCollection.get().await().documents.mapNotNull {
                it.toObject(Product::class.java)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}