package com.univalle.inventorywidget

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Navigation Component maneja todo automáticamente
        // El NavHostFragment carga el fragmento inicial definido en nav_graph.xml
    }
}
