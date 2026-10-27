package com.example.drivenext

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Экран регистрации (шаг 2).
 */
class RegisterStep2Activity : AppCompatActivity() {

    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_step2)

        val editLastName = findViewById<EditText>(R.id.editLastName)
        val editFirstName = findViewById<EditText>(R.id.editFirstName)
        val editBirthDate = findViewById<EditText>(R.id.editBirthDate)
        val btnCalendar = findViewById<ImageButton>(R.id.btnCalendar)
        val checkMale = findViewById<CheckBox>(R.id.checkMale)
        val checkFemale = findViewById<CheckBox>(R.id.checkFemale)
        val btnNext = findViewById<AppCompatButton>(R.id.btnRegisterNext2)

        val showPicker = {
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    editBirthDate.setText(dateFormat.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        btnCalendar.setOnClickListener { showPicker() }

        fun isValidDate(value: String): Boolean {
            if (!value.matches(Regex("^\\d{2}/\\d{2}/\\d{4}$"))) return false
            // Принимаем оба порядка: DD/MM/YYYY (из календаря) и MM/DD/YYYY (из ТЗ).
            for (pattern in listOf("dd/MM/yyyy", "MM/dd/yyyy")) {
                val format = SimpleDateFormat(pattern, Locale.getDefault()).apply { isLenient = false }
                try {
                    format.parse(value)
                    return true
                } catch (_: Exception) {
                }
            }
            return false
        }

        fun validate() {
            val last = editLastName.text.toString().trim()
            val first = editFirstName.text.toString().trim()
            val date = editBirthDate.text.toString().trim()
            btnNext.isEnabled = last.isNotEmpty() && first.isNotEmpty() && date.isNotEmpty()
            btnNext.alpha = if (btnNext.isEnabled) 1.0f else 0.5f
        }

        checkMale.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked && checkFemale.isChecked) checkFemale.isChecked = false
            validate()
        }
        checkFemale.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked && checkMale.isChecked) checkMale.isChecked = false
            validate()
        }

        val watcher = object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { validate() }
            override fun afterTextChanged(s: android.text.Editable?) {}
        }
        editLastName.addTextChangedListener(watcher)
        editFirstName.addTextChangedListener(watcher)
        editBirthDate.addTextChangedListener(watcher)

        btnNext.isEnabled = false
        btnNext.alpha = 0.5f

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        btnNext.setOnClickListener {
            val date = editBirthDate.text.toString().trim()
            if (editLastName.text.toString().trim().isEmpty() ||
                editFirstName.text.toString().trim().isEmpty() ||
                (!checkMale.isChecked && !checkFemale.isChecked)
            ) {
                Toast.makeText(this, "Пожалуйста, заполните все обязательные поля.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!isValidDate(date)) {
                Toast.makeText(this, "Введите корректную дату рождения.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            startActivity(android.content.Intent(this, RegisterStep3Activity::class.java))
        }
    }
}
