package com.example.drivenext

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.drivenext.onboarding.OnboardingActivity
import com.example.drivenext.ui.NoInternetActivity
import com.example.drivenext.util.NetworkUtils

/**
 * Стартовый экран загрузки: название, слоган и логотип приложения.
 * Через короткую паузу автоматически переходит к онбордингу.
 */
class EntryActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())

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

        handler.postDelayed({ onSplashFinished() }, SPLASH_DURATION_MS)
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    private fun onSplashFinished() {
        if (NetworkUtils.isNetworkAvailable(this)) {
            continueFlow()
        } else {
            noInternetLauncher.launch(Intent(this, NoInternetActivity::class.java))
        }
    }

    private fun continueFlow() {
        // TODO: временно онбординг показывается при каждом входе.
        //  Вернуть проверку первого запуска: if (!preferences.isOnboardingCompleted) { ... }
        startActivity(Intent(this, OnboardingActivity::class.java))
    }

    companion object {
        private const val SPLASH_DURATION_MS = 2500L
    }
}
