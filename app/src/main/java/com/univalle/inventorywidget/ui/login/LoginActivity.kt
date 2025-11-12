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

class LoginActivity : AppCompatActivity() {

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 🔒 Verificador de la sesion
        val prefs = getSharedPreferences("sesion_usuario", MODE_PRIVATE)
        val isLoggedIn = prefs.getBoolean("isLoggedIn", false)

        if (isLoggedIn) {
            // Si ya hay sesión, ir directo al Home sin pedir huella
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        executor = ContextCompat.getMainExecutor(this)


        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(applicationContext, "Autenticación exitosa ✅", Toast.LENGTH_SHORT).show()

                    // Guardar la sesión activa
                    val prefs = getSharedPreferences("sesion_usuario", MODE_PRIVATE)
                    prefs.edit().putBoolean("isLoggedIn", true).apply()


                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
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


        val huellaView: LottieAnimationView = findViewById(R.id.huellaAnim)
        huellaView.setOnClickListener {
            biometricPrompt.authenticate(promptInfo)
        }
    }
}
