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

class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var toolbar: MaterialToolbar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val vista = inflater.inflate(R.layout.fragment_home, container, false)


        recyclerView = vista.findViewById(R.id.recyclerProductos)
        toolbar = vista.findViewById(R.id.toolbarInventario)


        toolbar.inflateMenu(R.menu.menu_toolbar)
        toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_logout) {
                cerrarSesion()
                true
            } else false
        }


        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)


        viewModel.productos.observe(viewLifecycleOwner) { lista ->
            recyclerView.adapter = ProductAdapter(lista)
        }

        return vista
    }

    // 🔐 Función para cerrar sesión
    private fun cerrarSesion() {
        val prefs = requireActivity()
            .getSharedPreferences("sesion_usuario", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("isLoggedIn", false).apply()

        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onResume() {
        super.onResume()
        verificarSesion()
    }

    // 🔒 Verifica si la sesión sigue activa, si no redirige al login
    private fun verificarSesion() {
        val prefs = requireActivity()
            .getSharedPreferences("sesion_usuario", Context.MODE_PRIVATE)
        val isLogged = prefs.getBoolean("isLoggedIn", false)

        if (!isLogged) {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }
}
