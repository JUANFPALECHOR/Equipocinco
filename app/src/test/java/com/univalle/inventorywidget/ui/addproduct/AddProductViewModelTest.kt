package com.univalle.inventorywidget.ui.addproduct

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.univalle.inventorywidget.data.Product
import com.univalle.inventorywidget.data.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*

@ExperimentalCoroutinesApi
class AddProductViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Mock
    private lateinit var repository: ProductRepository

    private lateinit var viewModel: AddProductViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = AddProductViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `insertProduct con exito retorna Success`() = runTest {
        // Given - Dado un producto válido
        val product = Product("1234", "Test", 10000.0, 5)
        `when`(repository.insert(any())).thenReturn(true)

        // When - Cuando se inserta el producto
        viewModel.insertProduct(product)

        // Then - Entonces el resultado debe ser Success
        val result = viewModel.insertResult.value
        assertTrue(result is AddProductViewModel.InsertResult.Success)
    }

    @Test
    fun `insertProduct con codigo duplicado retorna DuplicateCode`() = runTest {
        // Given - Dado un producto con código duplicado
        val product = Product("1234", "Test", 10000.0, 5)
        `when`(repository.insert(any())).thenReturn(false)

        // When - Cuando se intenta insertar
        viewModel.insertProduct(product)

        // Then - Entonces el resultado debe ser DuplicateCode
        val result = viewModel.insertResult.value
        assertTrue(result is AddProductViewModel.InsertResult.DuplicateCode)
    }

    @Test
    fun `insertProduct con error retorna Error`() = runTest {
        // Given - Dado que el repository lanza una excepción
        val product = Product("1234", "Test", 10000.0, 5)
        `when`(repository.insert(any())).thenThrow(RuntimeException("Error de prueba"))

        // When - Cuando se intenta insertar
        viewModel.insertProduct(product)

        // Then - Entonces el resultado debe ser Error
        val result = viewModel.insertResult.value
        assertTrue(result is AddProductViewModel.InsertResult.Error)
        assertEquals("Error de prueba", (result as AddProductViewModel.InsertResult.Error).message)
    }
}