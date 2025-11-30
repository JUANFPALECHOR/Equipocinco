package com.univalle.inventorywidget.ui.editproduct

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.univalle.inventorywidget.R
import com.univalle.inventorywidget.data.Product
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController



/**
 * Fragment para editar un producto existente
 * HU 6.0: Ventana Editar Producto
 */
class EditProductFragment : Fragment() {


    private lateinit var tvProductId: TextView
    private lateinit var etNombre: TextInputEditText
    private lateinit var etPrecio: TextInputEditText
    private lateinit var etCantidad: TextInputEditText
    private lateinit var btnEditar: MaterialButton
    private lateinit var btnBack: ImageButton

    private val viewModel: EditProductViewModel by viewModels()


    private var producto: Product? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_product, container, false)



        // Inicializar views
        tvProductId = view.findViewById(R.id.tvProductId)
        etNombre = view.findViewById(R.id.etNombreEdit)
        etPrecio = view.findViewById(R.id.etPrecioEdit)
        etCantidad = view.findViewById(R.id.etCantidadEdit)
        btnEditar = view.findViewById(R.id.btnEditarProducto)
        btnBack = view.findViewById(R.id.btnBack)

        // Recuperar datos enviados desde ProductDetailFragment
        arguments?.let {
            val codigo = it.getString("codigo")
            val nombre = it.getString("nombre")
            val precio = it.getDouble("precio")
            val cantidad = it.getInt("cantidad")

            producto = Product(
                codigo = codigo!!,
                nombre = nombre!!,
                precio = precio,
                cantidad = cantidad
            )

            // Criterio 2: Mostrar ID (no editable)
            tvProductId.text = "Id: $codigo"

            // Criterio 3: Precargar campos
            etNombre.setText(nombre)
            etPrecio.setText(precio.toString())
            etCantidad.setText(cantidad.toString())
        }

        // Criterio 5: TextWatcher para validar campos y habilitar botón
        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val isValid = etNombre.text?.isNotBlank() == true &&
                        etPrecio.text?.isNotBlank() == true &&
                        etCantidad.text?.isNotBlank() == true

                btnEditar.isEnabled = isValid
                btnEditar.alpha = if (isValid) 1.0f else 0.5f
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        etNombre.addTextChangedListener(watcher)
        etPrecio.addTextChangedListener(watcher)
        etCantidad.addTextChangedListener(watcher)

        btnEditar.setOnClickListener {
            try {
                val nuevoProducto = producto!!.copy(
                    nombre = etNombre.text.toString().trim(),
                    precio = etPrecio.text.toString().toDouble(),
                    cantidad = etCantidad.text.toString().toInt()
                )

                viewModel.updateProduct(nuevoProducto)

            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Error en los datos: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


        viewModel.updateResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is EditProductViewModel.UpdateResult.Success -> {
                    Toast.makeText(requireContext(), "Producto actualizado", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
                is EditProductViewModel.UpdateResult.Error -> {
                    Toast.makeText(requireContext(), "Error: ${result.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }




        btnBack.setOnClickListener {
            findNavController().navigateUp()
        }


        return view
    }
}