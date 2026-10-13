package com.example.drivenext.onboarding

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/**
 * Данные одного слайда онбординга.
 */
data class OnboardingPage(
    @param:DrawableRes val imageRes: Int,
    @param:StringRes val titleRes: Int,
    @param:StringRes val descriptionRes: Int
)
