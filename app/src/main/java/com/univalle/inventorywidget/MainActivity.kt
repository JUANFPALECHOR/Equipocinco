package com.univalle.inventorywidget

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.univalle.inventorywidget.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Verificar si hay sesión activa en Firebase O en SharedPreferences
        val prefs = getSharedPreferences("inventory_prefs", MODE_PRIVATE)
        val sesionActiva = prefs.getBoolean("sesionActiva", false) ||
                com.google.firebase.auth.FirebaseAuth.getInstance().currentUser != null


        if (!sesionActiva) {
            // No hay sesión → Ir a LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        // Si hay sesión, cargar el layout
        setContentView(R.layout.activity_main)
    }
}
