// unused?

package com.example.soen345_winter2026.database

import com.google.firebase.firestore.FirebaseFirestore

class EventDB {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun createEvent(
        availableSeats: Int,
        category: String,
        date: String,
        location: String,
        status: String,
        imageUrl: String?,
        title: String,
        callback: (Boolean, String?) -> Unit
    ) {
        val event = hashMapOf(
            "availableSeats" to availableSeats,
            "category" to category,
            "date" to date,
            "location" to location,
            "status" to status,
            "imageUrl" to imageUrl,
            "title" to title
        )

        db.collection("events")
            .add(event)
            .addOnSuccessListener { documentReference ->
                callback(true, documentReference.id)
            }
            .addOnFailureListener { exception ->
                callback(false, exception.message)
            }
    }

}