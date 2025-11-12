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
import com.univalle.inventorywidget.data.ProductRepository
import com.univalle.inventorywidget.data.Product
import com.univalle.inventorywidget.ui.home.HomeFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.widget.Toast

class ProductDetailFragment : Fragment() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var tvNombre: TextView
    private lateinit var tvPrecio: TextView
    private lateinit var tvCantidad: TextView
    private lateinit var tvTotal: TextView
    private lateinit var btnEliminar: Button
    private lateinit var fabEditar: FloatingActionButton
    private lateinit var repo: ProductRepository
    private var product: Product? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val vista = inflater.inflate(R.layout.fragment_product_detail, container, false)

        repo = ProductRepository.getInstance(requireContext())

        toolbar = vista.findViewById(R.id.toolbarDetalle)
        tvNombre = vista.findViewById(R.id.tvNombreDetalle)
        tvPrecio = vista.findViewById(R.id.tvPrecioDetalle)
        tvCantidad = vista.findViewById(R.id.tvCantidadDetalle)
        tvTotal = vista.findViewById(R.id.tvTotalDetalle)
        btnEliminar = vista.findViewById(R.id.btnEliminar)
        fabEditar = vista.findViewById(R.id.fabEditar)

        val codigo = arguments?.getString("codigoProducto")

        product = repo.obtenerProductosDirecto().find { it.codigo == codigo }

        product?.let {
            tvNombre.text = "Nombre: ${it.nombre}"
            tvPrecio.text = "Precio unidad: ${it.precio}"
            tvCantidad.text = "Cantidad: ${it.cantidad}"
            tvTotal.text = "Total: ${it.precio * it.cantidad}"
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
                        CoroutineScope(Dispatchers.IO).launch {
                            repo.delete(p)
                            withContext(Dispatchers.Main) {
                                requireActivity().supportFragmentManager.beginTransaction()
                                    .replace(R.id.contenedorFragments, HomeFragment())
                                    .commit()
                            }
                        }
                    }
                }
                .show()
        }

        fabEditar.setOnClickListener {
            Toast.makeText(requireContext(), "Ir a editar (HU6)", Toast.LENGTH_SHORT).show()
        }

        return vista
    }
}

