package com.univalle.inventorywidget

import android.app.Application
import com.google.firebase.FirebaseApp

class InventoryApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Inicializar Firebase explícitamente
        FirebaseApp.initializeApp(this)
    }
}
