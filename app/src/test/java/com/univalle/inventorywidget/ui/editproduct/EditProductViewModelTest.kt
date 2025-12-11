package com.univalle.inventorywidget.ui.editproduct

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.univalle.inventorywidget.data.Product
import com.univalle.inventorywidget.data.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any

@ExperimentalCoroutinesApi
class EditProductViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var repository: ProductRepository

    private lateinit var viewModel: EditProductViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = EditProductViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun updateProduct con exito retorna Success() = runTest {
        // Given - Dado un producto válido
        val product = Product("1234", "Test Actualizado", 15000.0, 10)

        // When - Cuando se actualiza el producto
        viewModel.updateProduct(product)

        // Then - Entonces el resultado debe ser Success
        val result = viewModel.updateResult.value
        assertTrue(result is EditProductViewModel.UpdateResult.Success)
        verify(repository).update(product)
    }

    @Test
    fun updateProduct con error retorna Error() = runTest {
        // Given - Dado que el repository lanza una excepción
        val product = Product("1234", "Test", 10000.0, 5)
        doThrow(RuntimeException("Error de actualización"))
            .when(repository).update(any())

        // When - Cuando se intenta actualizar
        viewModel.updateProduct(product)

        // Then - Entonces el resultado debe ser Error
        val result = viewModel.updateResult.value
        assertTrue(result is EditProductViewModel.UpdateResult.Error)
        assertEquals("Error de actualización", (result as EditProductViewModel.UpdateResult.Error).message)
    }
}

