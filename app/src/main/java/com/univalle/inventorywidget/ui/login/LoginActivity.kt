package com.univalle.inventorywidget.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.univalle.inventorywidget.R
import com.univalle.inventorywidget.MainActivity
import com.airbnb.lottie.LottieAnimationView
import java.util.concurrent.Executor
import com.google.firebase.auth.FirebaseAuth
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import com.univalle.inventorywidget.widget.InventoryWidgetProvider
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    private lateinit var auth: FirebaseAuth
    private var fromWidget: String? = null // "EYE", "MANAGE" o null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        fromWidget = intent.getStringExtra("FROM_WIDGET")


        val currentUser = auth.currentUser
        if (currentUser != null) {

            navegarDespuesDeLogin()
            return
        }

        executor = ContextCompat.getMainExecutor(this)

        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(applicationContext, "Autenticación exitosa ✅", Toast.LENGTH_SHORT).show()


                    auth.signInAnonymously()
                        .addOnSuccessListener {
                            // Guardar la sesión activa también en SharedPreferences
                            val prefs = getSharedPreferences("sesion_usuario", MODE_PRIVATE)
                            prefs.edit().putBoolean("isLoggedIn", true).apply()

                            navegarDespuesDeLogin()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(applicationContext, "Error en Firebase: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(applicationContext, "Error: $errString", Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(applicationContext, "Autenticación fallida ❌", Toast.LENGTH_SHORT).show()
                }
            })

        //ventana emergente
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Autenticación con biometría")
            .setSubtitle("Usa tu huella digital para continuar")
            .setNegativeButtonText("Cancelar")
            .build()

        val biometricManager = BiometricManager.from(this)
        val puedeAutenticar = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)

        if (puedeAutenticar != BiometricManager.BIOMETRIC_SUCCESS) {
            Toast.makeText(this, "El dispositivo no soporta biometría", Toast.LENGTH_LONG).show()
        }

        // conecto la animacion de huella
        val huellaView: LottieAnimationView = findViewById(R.id.huellaAnim)
        huellaView.setOnClickListener {
            biometricPrompt.authenticate(promptInfo)
        }
    }



    private fun navegarDespuesDeLogin() {
        when (fromWidget) {
            "EYE" -> {
                // Vino del ícono del ojo → Actualizar widget y cerrar
                val appWidgetManager = AppWidgetManager.getInstance(this)
                val ids = appWidgetManager.getAppWidgetIds(
                    ComponentName(this, InventoryWidgetProvider::class.java)
                )
                val intent = Intent(this, InventoryWidgetProvider::class.java).apply {
                    action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                }
                sendBroadcast(intent)
                finish()
            }
            "MANAGE" -> {
                // Vino del botón Gestionar → Ir a MainActivity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            else -> {
                // Apertura normal → Ir a MainActivity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }


}
