package com.example.drivenext

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Экран авторизации.
 */
class SignInActivity : AppCompatActivity() {

    private var passwordVisible = false
    private val uiScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val credentialManager by lazy { CredentialManager.create(this) }

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
        val btnLoginSubmit = findViewById<AppCompatButton>(R.id.btnLoginSubmit)

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
            val intent = android.content.Intent(this, HomepageActivity::class.java).apply {
                flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }

        findViewById<AppCompatButton>(R.id.btnLoginGoogle).setOnClickListener {
            signInWithGoogle()
        }

        findViewById<TextView>(R.id.btnForgotPassword).setOnClickListener {
            Toast.makeText(this, "Восстановление пароля", Toast.LENGTH_SHORT).show()
        }

        findViewById<TextView>(R.id.btnGoRegister).setOnClickListener {
            startActivity(android.content.Intent(this, RegisterActivity::class.java))
        }
    }

    /**
     * Реальная авторизация через Google Identity Services (Credential Manager).
     */
    private fun signInWithGoogle() {
        val clientId = BuildConfig.GOOGLE_WEB_CLIENT_ID
        if (clientId.isBlank()) {
            Toast.makeText(
                this,
                "Google-вход не настроен",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(clientId)
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        uiScope.launch {
            try {
                val response = credentialManager.getCredential(this@SignInActivity, request)
                val credential = response.credential
                if (credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val account = googleCredential.displayName ?: googleCredential.id
                    Toast.makeText(
                        this@SignInActivity,
                        "Токен получен ($account)",
                        Toast.LENGTH_LONG
                    ).show()
                    val intent = android.content.Intent(this@SignInActivity, HomepageActivity::class.java).apply {
                        flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                    finish()
                }
            } catch (_: GetCredentialCancellationException) {
                // Пользователь закрыл окно выбора аккаунта — ничего не показываем.
            } catch (_: NoCredentialException) {
                Toast.makeText(
                    this@SignInActivity,
                    "На устройстве нет доступных Google-аккаунтов.",
                    Toast.LENGTH_LONG
                ).show()
            } catch (_: GoogleIdTokenParsingException) {
                Toast.makeText(
                    this@SignInActivity,
                    "Не удалось получить Google-токен.",
                    Toast.LENGTH_LONG
                ).show()
            } catch (_: GetCredentialException) {
                Toast.makeText(
                    this@SignInActivity,
                    "Не удалось войти через Google.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onDestroy() {
        uiScope.cancel()
        super.onDestroy()
    }
}
