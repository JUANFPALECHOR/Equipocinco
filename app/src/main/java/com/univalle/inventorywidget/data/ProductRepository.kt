package com.univalle.inventorywidget.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val productosCollection = firestore.collection("productos")
    private val _productos = MutableLiveData<List<Product>>()

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
        }
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
