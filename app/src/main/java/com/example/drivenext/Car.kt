package com.example.drivenext

import com.google.firebase.firestore.DocumentSnapshot
import java.io.Serializable

data class Car(
    val model: String,
    val brand: String,
    val price: String,
    val transmission: String,
    val fuel: String,
    val imageUrl: String? = null,
    val imageName: String? = null
) : Serializable {

    fun matches(query: String): Boolean {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return false
        return model.lowercase().contains(q) ||
            brand.lowercase().contains(q) ||
            "$model $brand".lowercase().contains(q)
    }

    companion object {
        private const val serialVersionUID = 1L

        fun fromDocument(doc: DocumentSnapshot): Car? {
            val model = doc.getString("model") ?: doc.getString("name") ?: return null
            return Car(
                model = model,
                brand = doc.getString("brand").orEmpty(),
                price = formatPrice(doc.get("price")),
                transmission = doc.getString("transmission") ?: doc.getString("gearbox").orEmpty(),
                fuel = doc.getString("fuel").orEmpty(),
                imageUrl = doc.getString("imageUrl")?.takeIf { it.isNotBlank() },
                imageName = doc.getString("image")?.takeIf { it.isNotBlank() }
            )
        }

        private fun formatPrice(value: Any?): String = when (value) {
            is Number -> "${value.toLong()}₽ в день"
            is String -> value
            else -> ""
        }
    }
}
