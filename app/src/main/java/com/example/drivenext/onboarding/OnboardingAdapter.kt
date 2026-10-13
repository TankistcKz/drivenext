package com.example.drivenext.onboarding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * Адаптер, связывающий ViewPager2 со списком слайдов онбординга.
 */
class OnboardingAdapter(
    activity: FragmentActivity,
    private val pages: List<OnboardingPage>
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = pages.size

    override fun createFragment(position: Int): Fragment =
        OnboardingFragment.newInstance(pages[position])
}
