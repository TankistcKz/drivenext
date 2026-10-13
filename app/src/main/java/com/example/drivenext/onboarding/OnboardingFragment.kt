package com.example.drivenext.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.drivenext.R

/**
 * Универсальный слайд онбординга: картинка, заголовок и краткое описание.
 */
class OnboardingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_onboarding, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = requireArguments()
        view.findViewById<ImageView>(R.id.imageOnboarding)
            .setImageResource(args.getInt(ARG_IMAGE))
        view.findViewById<TextView>(R.id.textOnboardingTitle)
            .setText(args.getInt(ARG_TITLE))
        view.findViewById<TextView>(R.id.textOnboardingDescription)
            .setText(args.getInt(ARG_DESCRIPTION))
    }

    companion object {
        private const val ARG_IMAGE = "arg_image"
        private const val ARG_TITLE = "arg_title"
        private const val ARG_DESCRIPTION = "arg_description"

        fun newInstance(page: OnboardingPage): OnboardingFragment =
            OnboardingFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_IMAGE, page.imageRes)
                    putInt(ARG_TITLE, page.titleRes)
                    putInt(ARG_DESCRIPTION, page.descriptionRes)
                }
            }
    }
}
