package com.univalle.inventorywidget.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.univalle.inventorywidget.MainActivity
import com.univalle.inventorywidget.R
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import com.univalle.inventorywidget.widget.InventoryWidgetProvider

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var tilEmail: TextInputLayout
    private lateinit var etEmail: TextInputEditText
    private lateinit var tilPassword: TextInputLayout
    private lateinit var etPassword: TextInputEditText
    private lateinit var ivTogglePassword: ImageView
    private lateinit var btnLogin: Button
    private lateinit var tvRegistrarse: TextView

    private var fromWidget: String? = null
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        fromWidget = intent.getStringExtra("FROM_WIDGET")

        // Verificar si ya hay sesión activa
        val currentUser = auth.currentUser
        if (currentUser != null) {
            navegarDespuesDeLogin()
            return
        }

        // Inicializar vistas
        tilEmail = findViewById(R.id.tilEmail)
        etEmail = findViewById(R.id.etEmail)
        tilPassword = findViewById(R.id.tilPassword)
        etPassword = findViewById(R.id.etPassword)
        ivTogglePassword = findViewById(R.id.ivTogglePassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvRegistrarse = findViewById(R.id.tvRegistrarse)

        // Configurar campo de password (solo números)
        etPassword.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD

        // TextWatcher para validación en tiempo real
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validarCampos()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        etEmail.addTextChangedListener(textWatcher)
        etPassword.addTextChangedListener(textWatcher)

        // Toggle password visibility
        ivTogglePassword.setOnClickListener {
            togglePasswordVisibility()
        }

        // Botón Login
        btnLogin.setOnClickListener {
            realizarLogin()
        }


        // Botón Registrarse
        tvRegistrarse.setOnClickListener {
            realizarRegistro()
        }
    }

    private fun validarCampos() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        // Validar password (mínimo 6 dígitos)
        if (password.isNotEmpty() && password.length < 6) {
            tilPassword.error = "Mínimo 6 dígitos"
            tilPassword.isErrorEnabled = true
        } else {
            tilPassword.error = null
            tilPassword.isErrorEnabled = false
        }

        // Habilitar botones si ambos campos están llenos y password válido
        val camposValidos = email.isNotEmpty() && password.length >= 6

        btnLogin.isEnabled = camposValidos
        tvRegistrarse.isEnabled = camposValidos

        // Cambiar estilo de los botones según estado
        if (camposValidos) {
            btnLogin.alpha = 1.0f
            tvRegistrarse.setTextColor(getColor(android.R.color.white))
            tvRegistrarse.setTypeface(null, android.graphics.Typeface.BOLD)
        } else {
            btnLogin.alpha = 0.5f
            tvRegistrarse.setTextColor(getColor(R.color.gray_inactive))
            tvRegistrarse.setTypeface(null, android.graphics.Typeface.NORMAL)
        }
    }

    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Ocultar contraseña
            etPassword.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            ivTogglePassword.setImageResource(R.drawable.ic_eye_open)
            isPasswordVisible = false
        } else {
            // Mostrar contraseña
            etPassword.inputType = InputType.TYPE_CLASS_NUMBER
            ivTogglePassword.setImageResource(R.drawable.ic_eye_closed)
            isPasswordVisible = true
        }
        // Mover cursor al final
        etPassword.setSelection(etPassword.text?.length ?: 0)
    }

    private fun realizarLogin() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (email.isEmpty() || password.length < 6) {
            return
        }

        // Deshabilitar botón mientras se procesa
        btnLogin.isEnabled = false

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                // Login exitoso - Guardar sesión en SharedPreferences
                val prefs = getSharedPreferences("inventory_prefs", MODE_PRIVATE)
                prefs.edit().putBoolean("isLoggedIn", true).apply()

                Toast.makeText(this, "Login exitoso", Toast.LENGTH_SHORT).show()
                navegarDespuesDeLogin()
            }
            .addOnFailureListener { e ->
                // Login fallido
                Toast.makeText(this, "Login incorrecto", Toast.LENGTH_SHORT).show()
                btnLogin.isEnabled = true
            }
    }

    private fun realizarRegistro() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (email.isEmpty() || password.length < 6) {
            return
        }

        // Deshabilitar botón mientras se procesa
        tvRegistrarse.isEnabled = false

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                // Registro exitoso - Guardar sesión en SharedPreferences
                val prefs = getSharedPreferences("sesion_usuario", MODE_PRIVATE)
                prefs.edit().putBoolean("isLoggedIn", true).apply()

                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                navegarDespuesDeLogin()
            }
            .addOnFailureListener { e ->
                // Registro fallido (usuario ya existe)
                Toast.makeText(this, "Error en el registro", Toast.LENGTH_SHORT).show()
                tvRegistrarse.isEnabled = true
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
