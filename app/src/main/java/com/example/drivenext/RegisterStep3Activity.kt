package com.example.drivenext

import android.app.DatePickerDialog
import android.graphics.Outline
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Экран регистрации (шаг 3).
 */
class RegisterStep3Activity : AppCompatActivity() {

    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    private var avatarUri: Uri? = null
    private var licenseUri: Uri? = null
    private var passportUri: Uri? = null

    private lateinit var ivAvatar: ImageView
    private lateinit var ivLicenseIcon: ImageView
    private lateinit var ivPassportIcon: ImageView
    private lateinit var tvLicenseAction: TextView
    private lateinit var tvPassportAction: TextView
    private lateinit var editLicenseNumber: EditText
    private lateinit var editIssueDate: EditText
    private lateinit var btnNext: AppCompatButton

    private val pickAvatar = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri ?: return@registerForActivityResult
        avatarUri = uri
        ivAvatar.setImageURI(uri)
    }

    private val pickLicense = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri ?: return@registerForActivityResult
        licenseUri = uri
        ivLicenseIcon.setImageURI(uri)
        tvLicenseAction.text = getString(R.string.upload_done)
        validate()
    }

    private val pickPassport = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri ?: return@registerForActivityResult
        passportUri = uri
        ivPassportIcon.setImageURI(uri)
        tvPassportAction.text = getString(R.string.upload_done)
        validate()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_step3)

        ivAvatar = findViewById(R.id.ivAvatar)
        ivLicenseIcon = findViewById(R.id.ivLicenseIcon)
        ivPassportIcon = findViewById(R.id.ivPassportIcon)
        tvLicenseAction = findViewById(R.id.tvLicenseAction)
        tvPassportAction = findViewById(R.id.tvPassportAction)
        editLicenseNumber = findViewById(R.id.editLicenseNumber)
        editIssueDate = findViewById(R.id.editIssueDate)
        val btnCalendarIssue = findViewById<ImageButton>(R.id.btnCalendarIssue)
        btnNext = findViewById(R.id.btnRegisterNext3)

        ivAvatar.clipToOutline = true
        ivAvatar.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setOval(0, 0, view.width, view.height)
            }
        }

        val showPicker = {
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    editIssueDate.setText(dateFormat.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        btnCalendarIssue.setOnClickListener { showPicker() }

        editLicenseNumber.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { validate() }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
        editIssueDate.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { validate() }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })

        btnNext.isEnabled = false
        btnNext.alpha = 0.5f

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        btnNext.setOnClickListener {
            val number = editLicenseNumber.text.toString().trim()
            val date = editIssueDate.text.toString().trim()

            if (number.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, "Пожалуйста, заполните все обязательные поля.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (number.length != 10) {
                Toast.makeText(this, "Пожалуйста, заполните все обязательные поля.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!isValidDate(date)) {
                Toast.makeText(this, "Введите корректную дату выдачи.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (licenseUri == null || passportUri == null) {
                Toast.makeText(this, "Пожалуйста, загрузите все необходимые фото.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            startActivity(android.content.Intent(this, CongratsActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.btnUploadLicense).setOnClickListener {
            pickLicense.launch("image/*")
        }
        findViewById<LinearLayout>(R.id.btnUploadPassport).setOnClickListener {
            pickPassport.launch("image/*")
        }
        findViewById<ImageButton>(R.id.btnAddPhoto).setOnClickListener {
            pickAvatar.launch("image/*")
        }
    }

    private fun validate() {
        if (!::btnNext.isInitialized) return
        val number = editLicenseNumber.text.toString().trim()
        val date = editIssueDate.text.toString().trim()
        btnNext.isEnabled = number.isNotEmpty() && date.isNotEmpty()
        btnNext.alpha = if (btnNext.isEnabled) 1.0f else 0.5f
    }

    private fun isValidDate(value: String): Boolean {
        if (!value.matches(Regex("^\\d{2}/\\d{2}/\\d{4}$"))) return false
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply { isLenient = false }
        return try {
            format.parse(value)
            true
        } catch (_: Exception) {
            false
        }
    }
}
