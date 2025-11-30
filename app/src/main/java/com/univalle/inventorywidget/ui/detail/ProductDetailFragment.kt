package com.univalle.inventorywidget.ui.detail

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.univalle.inventorywidget.R
import com.univalle.inventorywidget.data.Product
import com.univalle.inventorywidget.ui.home.HomeFragment
import android.widget.Toast
import com.univalle.inventorywidget.ui.editproduct.EditProductFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope


class ProductDetailFragment : Fragment() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var tvNombre: TextView
    private lateinit var tvPrecio: TextView
    private lateinit var tvCantidad: TextView
    private lateinit var tvTotal: TextView
    private lateinit var btnEliminar: Button

    private val viewModel: ProductDetailViewModel by viewModels()

    private lateinit var fabEditar: FloatingActionButton
    private var product: Product? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val vista = inflater.inflate(R.layout.fragment_product_detail, container, false)


        toolbar = vista.findViewById(R.id.toolbarDetalle)
        tvNombre = vista.findViewById(R.id.tvNombreDetalle)
        tvPrecio = vista.findViewById(R.id.tvPrecioDetalle)
        tvCantidad = vista.findViewById(R.id.tvCantidadDetalle)
        tvTotal = vista.findViewById(R.id.tvTotalDetalle)
        btnEliminar = vista.findViewById(R.id.btnEliminar)
        fabEditar = vista.findViewById(R.id.fabEditar)

        val codigo = arguments?.getString("codigoProducto")

        // Cargar producto desde ViewModel
        codigo?.let {
            viewModel.loadProduct(it)
        }

        // Observar el producto
        viewModel.product.observe(viewLifecycleOwner) { producto ->
            producto?.let {
                product = it
                tvNombre.text = "Nombre: ${it.nombre}"
                tvPrecio.text = "Precio unidad: ${it.precio}"
                tvCantidad.text = "Cantidad: ${it.cantidad}"
                tvTotal.text = "Total: ${it.precio * it.cantidad}"
            }
        }


        toolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.contenedorFragments, HomeFragment())
                .addToBackStack(null)
                .commit()
        }

        btnEliminar.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Confirmar eliminación")
                .setMessage("¿Deseas eliminar este producto?")
                .setNegativeButton("No", null)
                .setPositiveButton("Sí") { _, _ ->
                    product?.let { p ->
                        viewModel.deleteProduct(p)
                    }
                }
                .show()
        }


        viewModel.deleteResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Producto eliminado", Toast.LENGTH_SHORT).show()
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.contenedorFragments, HomeFragment())
                    .commit()
            } else {
                Toast.makeText(requireContext(), "Error al eliminar", Toast.LENGTH_SHORT).show()
            }
        }


        fabEditar.setOnClickListener {
            product?.let { producto ->
                val bundle = Bundle().apply {
                    putString("codigo", producto.codigo)
                    putString("nombre", producto.nombre)
                    putDouble("precio", producto.precio)
                    putInt("cantidad", producto.cantidad)
                }

                val fragment = EditProductFragment()
                fragment.arguments = bundle

                parentFragmentManager.beginTransaction()
                    .replace(R.id.contenedorFragments, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }

        return vista
    }
}

