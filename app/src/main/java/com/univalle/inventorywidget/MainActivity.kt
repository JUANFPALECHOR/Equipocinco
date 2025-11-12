package com.univalle.inventorywidget

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.univalle.inventorywidget.ui.home.HomeFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // ✅ cambia widget_inventory por activity_main

        // Mostrar el fragmento de inventario
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.contenedorFragments, HomeFragment())
                .commit()
        }
    }
}
