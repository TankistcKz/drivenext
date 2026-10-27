package com.example.drivenext.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.drivenext.LoginActivity
import com.example.drivenext.R
import com.example.drivenext.data.AppPreferences

/**
 * Экран приветствия (онбординг) с последовательными слайдами.
 */
class OnboardingActivity : AppCompatActivity() {

    private val preferences by lazy { AppPreferences(this) }

    private val activeDotWidth by lazy {
        resources.getDimensionPixelSize(R.dimen.onboarding_dot_active_width)
    }
    private val inactiveDotWidth by lazy {
        resources.getDimensionPixelSize(R.dimen.onboarding_dot_inactive_width)
    }
    private val dotHeight by lazy {
        resources.getDimensionPixelSize(R.dimen.onboarding_dot_height)
    }
    private val dotMargin by lazy {
        resources.getDimensionPixelSize(R.dimen.onboarding_dot_margin)
    }

    private val pages = listOf(
        OnboardingPage(
            R.drawable.onboarding_step_1,
            R.string.onboarding_title_1,
            R.string.onboarding_description_1
        ),
        OnboardingPage(
            R.drawable.onboarding_step_2,
            R.string.onboarding_title_2,
            R.string.onboarding_description_2
        ),
        OnboardingPage(
            R.drawable.onboarding_step_3,
            R.string.onboarding_title_3,
            R.string.onboarding_description_3
        )
    )

    private lateinit var viewPager: ViewPager2
    private lateinit var btnNext: Button
    private lateinit var dotsContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        viewPager = findViewById(R.id.viewPager)
        btnNext = findViewById(R.id.btnNext)
        dotsContainer = findViewById(R.id.dotsContainer)

        viewPager.adapter = OnboardingAdapter(this, pages)
        setupDots()

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateDots(position)
                btnNext.setText(
                    if (position == pages.lastIndex) {
                        R.string.onboarding_start
                    } else {
                        R.string.onboarding_next
                    }
                )
            }
        })

        btnNext.setText(R.string.onboarding_next)
        btnNext.setOnClickListener {
            val nextPosition = viewPager.currentItem + 1
            if (nextPosition <= pages.lastIndex) {
                viewPager.setCurrentItem(nextPosition, true)
            } else {
                finishOnboarding()
            }
        }

        findViewById<Button>(R.id.btnSkip).setOnClickListener { finishOnboarding() }
    }

    private fun setupDots() {
        pages.forEachIndexed { index, _ ->
            val dot = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(inactiveDotWidth, dotHeight).apply {
                    marginStart = dotMargin
                    marginEnd = dotMargin
                }
                setBackgroundResource(R.drawable.dot_indicator)
                isSelected = index == 0
            }
            dotsContainer.addView(dot)
        }
        updateDots(0)
    }

    private fun updateDots(selectedPosition: Int) {
        for (index in 0 until dotsContainer.childCount) {
            val dot = dotsContainer.getChildAt(index)
            val selected = index == selectedPosition
            dot.isSelected = selected
            dot.layoutParams = (dot.layoutParams as LinearLayout.LayoutParams).apply {
                width = if (selected) activeDotWidth else inactiveDotWidth
            }
        }
    }

    private fun finishOnboarding() {
        preferences.isOnboardingCompleted = true
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
