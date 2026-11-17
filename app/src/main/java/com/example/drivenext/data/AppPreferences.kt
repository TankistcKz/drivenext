package com.example.drivenext.data

import android.content.Context
import androidx.core.content.edit

/**
 * Хранилище простых настроек приложения на основе SharedPreferences.
 */
class AppPreferences(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /** Пройден ли онбординг (экран приветствия). */
    var isOnboardingCompleted: Boolean
        get() = prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)
        set(value) = prefs.edit { putBoolean(KEY_ONBOARDING_COMPLETED, value) }

    /** URI выбранного пользователем изображения профиля. */
    var avatarUri: String?
        get() = prefs.getString(KEY_AVATAR_URI, null)
        set(value) = prefs.edit { putString(KEY_AVATAR_URI, value) }

    companion object {
        private const val PREFS_NAME = "drive_next_prefs"
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        private const val KEY_AVATAR_URI = "avatar_uri"
    }
}
