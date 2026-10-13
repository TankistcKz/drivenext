package com.example.drivenext

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.drivenext.data.AppPreferences
import com.example.drivenext.onboarding.OnboardingActivity
import com.example.drivenext.ui.NoInternetActivity
import com.example.drivenext.util.NetworkUtils

/**
 * Стартовый экран загрузки: название, логотип и слоган приложения.
 * Переход выполняется по клику по экрану.
 */
class EntryActivity : AppCompatActivity() {

    private val preferences by lazy { AppPreferences(this) }

    private val noInternetLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            continueFlow()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)

        findViewById<View>(R.id.entryRoot).setOnClickListener { onEntryClicked() }
    }

    private fun onEntryClicked() {
        if (NetworkUtils.isNetworkAvailable(this)) {
            continueFlow()
        } else {
            noInternetLauncher.launch(Intent(this, NoInternetActivity::class.java))
        }
    }

    private fun continueFlow() {
        if (!preferences.isOnboardingCompleted) {
            startActivity(Intent(this, OnboardingActivity::class.java))
        } else {
            // TODO: переход на экран входа/регистрации (будет реализован позже)
            Toast.makeText(this, "Экран входа/регистрации будет реализован позже", Toast.LENGTH_SHORT).show()
        }
    }
}
