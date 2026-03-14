package com.example.soen345_winter2026.events

import com.google.firebase.firestore.FirebaseFirestore

class EventRepository {

    private val db = FirebaseFirestore.getInstance()

    fun fetchActiveEvents(callback: (List<Event>, String?) -> Unit) {
        db.collection("events")
            .whereEqualTo("status", "active")
            .get()
            .addOnSuccessListener { result ->
                val events = result.documents.mapNotNull { doc ->
                    doc.toObject(Event::class.java)?.copy(id = doc.id)
                }
                callback(events, null)
            }
            .addOnFailureListener { e ->
                callback(emptyList(), e.message)
            }
    }
}
