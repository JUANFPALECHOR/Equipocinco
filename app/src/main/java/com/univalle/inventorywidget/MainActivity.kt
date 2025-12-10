package com.univalle.inventorywidget

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.univalle.inventorywidget.ui.login.LoginActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔐 Verificar sesión ANTES de cargar el UI
        val prefs = getSharedPreferences("sesion_usuario", MODE_PRIVATE)
        val isLoggedIn = prefs.getBoolean("isLoggedIn", false)

        if (!isLoggedIn) {
            // Si no hay sesión, ir directamente a LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        // Solo cargar el UI si hay sesión activa
        setContentView(R.layout.activity_main)

        // Navigation Component maneja todo automáticamente
        // El NavHostFragment carga el fragmento inicial definido en nav_graph.xml
    }
}
