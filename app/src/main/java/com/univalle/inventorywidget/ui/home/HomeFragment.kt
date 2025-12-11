package com.univalle.inventorywidget.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.univalle.inventorywidget.R
import com.univalle.inventorywidget.ui.adapters.ProductAdapter
import com.univalle.inventorywidget.ui.login.LoginActivity
import com.google.android.material.appbar.MaterialToolbar
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import androidx.fragment.app.viewModels

import com.univalle.inventorywidget.ui.addproduct.AddProductFragment

@AndroidEntryPoint  
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var toolbar: MaterialToolbar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val vista = inflater.inflate(R.layout.fragment_home, container, false)


        recyclerView = vista.findViewById(R.id.recyclerProductos)
        toolbar = vista.findViewById(R.id.toolbarInventario)

        toolbar.title = "Inventario"
        toolbar.setTitleTextColor(resources.getColor(android.R.color.white))


        toolbar.inflateMenu(R.menu.menu_toolbar)
        toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_logout) {
                cerrarSesion()
                true
            } else false
        }


        recyclerView.layoutManager = LinearLayoutManager(requireContext())




        // Observar los productos en vivo
        viewModel.productos.observe(viewLifecycleOwner) { lista ->
            recyclerView.adapter = ProductAdapter(lista)
        }

        // 🔘 Navegar a la pantalla de Agregar Producto
        val btnAgregar = vista.findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.btnAgregar)
        btnAgregar.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_addProduct)
        }


        return vista
    }

    // 🔐 Función para cerrar sesión
    private fun cerrarSesion() {

        com.google.firebase.auth.FirebaseAuth.getInstance().signOut()


        val prefs = requireActivity()
            .getSharedPreferences("inventory_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("sesionActiva", false).apply()


        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }


}
