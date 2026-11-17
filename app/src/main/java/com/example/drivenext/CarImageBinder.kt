package com.example.drivenext

import android.widget.ImageView
import coil.load

/**
 * Подбирает изображение для карточки машины.
 *
 * Приоритет: [Car.imageUrl] (сеть) → [Car.imageName] (локальный drawable) → заглушка `car.jpg`.
 */
object CarImageBinder {

    private const val FALLBACK_IMAGE = R.drawable.car

    /** Локальные изображения по имени из поля `image` документа Firestore. */
    private val localImages: Map<String, Int> = mapOf(
        "mercedes_s500" to R.drawable.mercedes_s500,
    )

    fun bind(imageView: ImageView, car: Car) {
        imageView.contentDescription = "${car.brand} ${car.model}".trim()

        val url = car.imageUrl?.takeIf { it.startsWith("http", ignoreCase = true) }
        val image: Any = when {
            url != null -> url
            else -> car.imageName?.let { localImages[it] } ?: FALLBACK_IMAGE
        }

        // Картинка должна целиком влезать в рамку карточки, без обрезки краёв.
        imageView.scaleType = ImageView.ScaleType.FIT_CENTER

        imageView.load(image) {
            placeholder(FALLBACK_IMAGE)
            error(FALLBACK_IMAGE)
            fallback(FALLBACK_IMAGE)
            crossfade(url != null)
        }
    }
}
