package com.example.drivenext

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * Экран авторизации.
 */
class SignInActivity : AppCompatActivity() {

    private var passwordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        val editPassword = findViewById<EditText>(R.id.editPassword)
        val btnTogglePassword = findViewById<ImageButton>(R.id.btnTogglePassword)

        btnTogglePassword.setImageResource(R.drawable.ic_visibility)

        btnTogglePassword.setOnClickListener {
            passwordVisible = !passwordVisible
            editPassword.transformationMethod = if (passwordVisible) {
                HideReturnsTransformationMethod.getInstance()
            } else {
                PasswordTransformationMethod.getInstance()
            }
            btnTogglePassword.setImageResource(if (passwordVisible) R.drawable.ic_visibility_off else R.drawable.ic_visibility)
            editPassword.setSelection(editPassword.text.length)
        }

        val editEmail = findViewById<EditText>(R.id.editEmail)
        val btnLoginSubmit = findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btnLoginSubmit)

        fun validateAndToggle() {
            val email = editEmail.text.toString().trim()
            val pass = editPassword.text.toString().trim()
            // Кнопка активна после заполнения полей; корректность e-mail проверяется по нажатию.
            btnLoginSubmit.isEnabled = email.isNotEmpty() && pass.isNotEmpty()
            btnLoginSubmit.alpha = if (btnLoginSubmit.isEnabled) 1.0f else 0.5f
        }

        editEmail.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { validateAndToggle() }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
        editPassword.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { validateAndToggle() }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })

        btnLoginSubmit.isEnabled = false
        btnLoginSubmit.alpha = 0.5f
        btnLoginSubmit.setOnClickListener {
            val email = editEmail.text.toString().trim()
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() || (!email.endsWith(".ru") && !email.contains(".com"))) {
                Toast.makeText(this, "Введите корректный адрес электронной почты.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Toast.makeText(this, "Токен получен", Toast.LENGTH_SHORT).show()
        }

        findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btnLoginGoogle).setOnClickListener {
            Toast.makeText(this, "Токен получен", Toast.LENGTH_SHORT).show()
        }

        findViewById<TextView>(R.id.btnForgotPassword).setOnClickListener {
            Toast.makeText(this, "Восстановление пароля", Toast.LENGTH_SHORT).show()
        }

        findViewById<TextView>(R.id.btnGoRegister).setOnClickListener {
            startActivity(android.content.Intent(this, RegisterActivity::class.java))
        }
    }
}
