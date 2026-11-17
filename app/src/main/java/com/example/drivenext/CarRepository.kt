package com.example.drivenext

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

object CarRepository {

    private const val TAG = "CarRepository"
    private const val COLLECTION_CARS = "Cars"
    private const val FALLBACK_LIMIT = 50L

    fun search(query: String, onSuccess: (List<Car>) -> Unit, onError: (Exception) -> Unit) {
        val q = query.trim().lowercase()
        val db = FirebaseFirestore.getInstance()

        db.collection(COLLECTION_CARS)
            .whereArrayContains("searchKeywords", q)
            .get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    onSuccess(snapshot.documents.mapNotNull { Car.fromDocument(it) })
                } else {
                    fallbackSearch(db, q, onSuccess, onError)
                }
            }
            .addOnFailureListener {
                Log.e(TAG, "Firestore search failed: collection=$COLLECTION_CARS, query=\"$q\"", it)
                onError(it)
            }
    }

    fun loadCars(onSuccess: (List<Car>) -> Unit, onError: (Exception) -> Unit) {
        FirebaseFirestore.getInstance()
            .collection(COLLECTION_CARS)
            .limit(FALLBACK_LIMIT)
            .get()
            .addOnSuccessListener { snapshot ->
                onSuccess(snapshot.documents.mapNotNull { Car.fromDocument(it) })
            }
            .addOnFailureListener {
                Log.e(TAG, "Firestore load failed: collection=$COLLECTION_CARS", it)
                onError(it)
            }
    }

    private fun fallbackSearch(
        db: FirebaseFirestore,
        query: String,
        onSuccess: (List<Car>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        db.collection(COLLECTION_CARS)
            .limit(FALLBACK_LIMIT)
            .get()
            .addOnSuccessListener { snapshot ->
                val cars = snapshot.documents
                    .mapNotNull { Car.fromDocument(it) }
                    .filter { it.matches(query) }
                onSuccess(cars)
            }
            .addOnFailureListener {
                Log.e(TAG, "Firestore fallback search failed: collection=$COLLECTION_CARS, query=\"$query\"", it)
                onError(it)
            }
    }
}
