package com.univalle.inventorywidget.ui.addproduct

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.univalle.inventorywidget.R
import com.univalle.inventorywidget.data.Product
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddProductFragment : Fragment() {

    private lateinit var etCodigo: TextInputEditText
    private lateinit var etNombre: TextInputEditText
    private lateinit var etPrecio: TextInputEditText
    private lateinit var etCantidad: TextInputEditText
    private lateinit var btnGuardar: Button
    private lateinit var toolbar: MaterialToolbar

    private val viewModel: AddProductViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val vista = inflater.inflate(R.layout.fragment_add_product, container, false)


        etCodigo = vista.findViewById(R.id.etCodigo)
        etNombre = vista.findViewById(R.id.etNombre)
        etPrecio = vista.findViewById(R.id.etPrecio)
        etCantidad = vista.findViewById(R.id.etCantidad)
        btnGuardar = vista.findViewById(R.id.btnGuardar)
        toolbar = vista.findViewById(R.id.toolbarAgregar)

        // --- LÍMITES DE LONGITUD ---
        etCodigo.filters = arrayOf(InputFilter.LengthFilter(4))
        etNombre.filters = arrayOf(InputFilter.LengthFilter(40))
        etPrecio.filters = arrayOf(InputFilter.LengthFilter(20))
        etCantidad.filters = arrayOf(InputFilter.LengthFilter(4))

        // --- BOTÓN VOLVER ---
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }


        // --- WATCHER PARA ACTIVAR BOTÓN ---
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val camposLlenos = etCodigo.text!!.isNotEmpty() &&
                                   etNombre.text!!.isNotEmpty() &&
                                   etPrecio.text!!.isNotEmpty() &&
                                   etCantidad.text!!.isNotEmpty()
                btnGuardar.isEnabled = camposLlenos
                if (camposLlenos) {
                    btnGuardar.setTypeface(null, Typeface.BOLD)
                    btnGuardar.setTextColor(Color.WHITE)
                } else {
                    btnGuardar.setTypeface(null, Typeface.NORMAL)
                    btnGuardar.setTextColor(Color.parseColor("#DDFFFFFF"))
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        etCodigo.addTextChangedListener(watcher)
        etNombre.addTextChangedListener(watcher)
        etPrecio.addTextChangedListener(watcher)
        etCantidad.addTextChangedListener(watcher)

        // --- GUARDAR PRODUCTO ---
        btnGuardar.setOnClickListener {
            val codigo = etCodigo.text.toString().trim()
            val nombre = etNombre.text.toString().trim()
            val precioText = etPrecio.text.toString().trim()
            val cantidadText = etCantidad.text.toString().trim()

            val precio = precioText.replace(",", ".").toDoubleOrNull()
            val cantidad = cantidadText.toIntOrNull()

            if (codigo.isEmpty() || nombre.isEmpty() || precio == null || cantidad == null) {
                Toast.makeText(requireContext(), "Verifique los datos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val product = Product(
                codigo = codigo,
                nombre = nombre,
                precio = precio,
                cantidad = cantidad
            )


            viewModel.insertProduct(product)
        }


        viewModel.insertResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is AddProductViewModel.InsertResult.Success -> {
                    Toast.makeText(requireContext(), "Producto guardado", Toast.LENGTH_SHORT).show()
                    limpiarCampos()
                    irAHome()
                }
                is AddProductViewModel.InsertResult.DuplicateCode -> {
                    Toast.makeText(requireContext(), "El código ya existe", Toast.LENGTH_SHORT).show()
                }
                is AddProductViewModel.InsertResult.Error -> {
                    Toast.makeText(requireContext(), "Error: ${result.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return vista
    }


    private fun limpiarCampos() {
        etCodigo.text?.clear()
        etNombre.text?.clear()
        etPrecio.text?.clear()
        etCantidad.text?.clear()
    }

    private fun irAHome() {
        findNavController().navigateUp()
    }

}
