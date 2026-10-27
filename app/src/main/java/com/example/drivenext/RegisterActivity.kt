package com.example.drivenext

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * Экран регистрации.
 */
class RegisterActivity : AppCompatActivity() {

    private var passwordVisible = false
    private var confirmPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val editPassword = findViewById<EditText>(R.id.editPassword)
        val btnTogglePassword = findViewById<ImageButton>(R.id.btnTogglePassword)
        val editConfirmPassword = findViewById<EditText>(R.id.editConfirmPassword)
        val btnToggleConfirmPassword = findViewById<ImageButton>(R.id.btnToggleConfirmPassword)

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

        btnToggleConfirmPassword.setOnClickListener {
            confirmPasswordVisible = !confirmPasswordVisible
            editConfirmPassword.transformationMethod = if (confirmPasswordVisible) {
                HideReturnsTransformationMethod.getInstance()
            } else {
                PasswordTransformationMethod.getInstance()
            }
            if (confirmPasswordVisible) {
                btnToggleConfirmPassword.setImageResource(R.drawable.ic_visibility_off)
            } else {
                btnToggleConfirmPassword.setImageResource(R.drawable.ic_visibility)
            }
            editConfirmPassword.setSelection(editConfirmPassword.text.length)
        }

        val editEmail = findViewById<EditText>(R.id.editEmail)
        val btnNext = findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btnRegisterNext)
        val checkAgree = findViewById<android.widget.CheckBox>(R.id.checkAgree)

        fun validate() {
            val email = editEmail.text.toString().trim()
            val pass = editPassword.text.toString().trim()
            val confirm = editConfirmPassword.text.toString().trim()
            // Кнопка активна после заполнения всех полей; корректность проверяется по нажатию.
            btnNext.isEnabled = email.isNotEmpty() && pass.isNotEmpty() && confirm.isNotEmpty()
            btnNext.alpha = if (btnNext.isEnabled) 1.0f else 0.5f
        }

        val watcher = object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { validate() }
            override fun afterTextChanged(s: android.text.Editable?) {}
        }
        editEmail.addTextChangedListener(watcher)
        editPassword.addTextChangedListener(watcher)
        editConfirmPassword.addTextChangedListener(watcher)
        checkAgree.setOnCheckedChangeListener { _, _ -> validate() }

        btnNext.isEnabled = false
        btnNext.alpha = 0.5f

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
        btnNext.setOnClickListener {
            val email = editEmail.text.toString().trim()
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() || (!email.endsWith(".ru") && !email.contains(".com"))) {
                Toast.makeText(this, "Введите корректный адрес электронной почты.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (editPassword.text.toString() != editConfirmPassword.text.toString()) {
                Toast.makeText(this, "Пароли не совпадают.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!checkAgree.isChecked) {
                Toast.makeText(this, "Необходимо согласиться с условиями...", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            startActivity(android.content.Intent(this, RegisterStep2Activity::class.java))
        }
    }
}
