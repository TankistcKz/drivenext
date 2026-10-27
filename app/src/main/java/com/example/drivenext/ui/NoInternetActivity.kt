package com.example.drivenext.ui

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.drivenext.R
import com.example.drivenext.util.NetworkUtils

/**
 * Универсальный экран отсутствия подключения к интернету.
 *
 * Запускается через Activity Result API. При успешной повторной попытке
 * возвращает [RESULT_OK], после чего вызывающий экран продолжает работу.
 */
class NoInternetActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_internet)

        findViewById<Button>(R.id.btnRetry).setOnClickListener {
            if (NetworkUtils.isNetworkAvailable(this)) {
                setResult(RESULT_OK)
                finish()
            }
        }
    }
}
