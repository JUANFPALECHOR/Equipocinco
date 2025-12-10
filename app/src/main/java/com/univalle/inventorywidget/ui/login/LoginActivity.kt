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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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


        val currentUser = auth.currentUser
        if (currentUser != null) {

            val prefs = getSharedPreferences("inventory_prefs", MODE_PRIVATE)
            prefs.edit().putBoolean("sesionActiva", true).apply()
            navegarDespuesDeLogin()
            return
        }



        tilEmail = findViewById(R.id.tilEmail)
        etEmail = findViewById(R.id.etEmail)
        tilPassword = findViewById(R.id.tilPassword)
        etPassword = findViewById(R.id.etPassword)
        ivTogglePassword = findViewById(R.id.ivTogglePassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvRegistrarse = findViewById(R.id.tvRegistrarse)


        etPassword.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD


        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validarCampos()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        etEmail.addTextChangedListener(textWatcher)
        etPassword.addTextChangedListener(textWatcher)


        ivTogglePassword.setOnClickListener {
            togglePasswordVisibility()
        }


        btnLogin.setOnClickListener {
            realizarLogin()
        }



        tvRegistrarse.setOnClickListener {
            realizarRegistro()
        }
    }

    private fun validarCampos() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()


        if (password.isNotEmpty() && password.length < 6) {
            tilPassword.error = "Mínimo 6 dígitos"
            tilPassword.isErrorEnabled = true
        } else {
            tilPassword.error = null
            tilPassword.isErrorEnabled = false
        }


        val camposValidos = email.isNotEmpty() && password.length >= 6

        btnLogin.isEnabled = camposValidos
        tvRegistrarse.isEnabled = camposValidos


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

            etPassword.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            ivTogglePassword.setImageResource(R.drawable.ic_eye_open)
            isPasswordVisible = false
        } else {

            etPassword.inputType = InputType.TYPE_CLASS_NUMBER
            ivTogglePassword.setImageResource(R.drawable.ic_eye_closed)
            isPasswordVisible = true
        }

        etPassword.setSelection(etPassword.text?.length ?: 0)
    }

    private fun realizarLogin() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (email.isEmpty() || password.length < 6) {
            return
        }


        btnLogin.isEnabled = false

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                // Login exitoso - Guardar sesión en SharedPreferences
                val prefs = getSharedPreferences("inventory_prefs", MODE_PRIVATE)
                prefs.edit().putBoolean("sesionActiva", true).apply()


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


        tvRegistrarse.isEnabled = false

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {

                val prefs = getSharedPreferences("inventory_prefs", MODE_PRIVATE)
                prefs.edit().putBoolean("sesionActiva", true).apply()


                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                navegarDespuesDeLogin()
            }
            .addOnFailureListener { e ->

                Toast.makeText(this, "Error en el registro", Toast.LENGTH_SHORT).show()
                tvRegistrarse.isEnabled = true
            }
    }

    private fun navegarDespuesDeLogin() {
        when (fromWidget) {
            "EYE" -> {

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

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            else -> {

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}
