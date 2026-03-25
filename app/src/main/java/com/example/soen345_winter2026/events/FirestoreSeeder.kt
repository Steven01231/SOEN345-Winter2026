package com.example.soen345_winter2026.events

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Seeds the Firestore `events` collection with sample data.
 * Only runs if the collection is empty, so it is safe to call on every launch.
 */
object FirestoreSeeder {

    private val sampleEvents = listOf(
        mapOf(
            "title" to "Jazz Night at Place des Arts",
            "category" to "Concert",
            "date" to "2026-04-10",
            "location" to "Montreal, QC",
            "availableSeats" to 200,
            "status" to EventStatus.ACTIVE,
            "imageUrl" to ""
        ),
        mapOf(
            "title" to "Indie Rock Festival",
            "category" to "Concert",
            "date" to "2026-05-03",
            "location" to "Parc Jean-Drapeau, Montreal",
            "availableSeats" to 500,
            "status" to EventStatus.ACTIVE,
            "imageUrl" to ""
        ),
        mapOf(
            "title" to "Dune: Part Three",
            "category" to "Movie",
            "date" to "2026-04-18",
            "location" to "Cineplex Odeon, Montreal",
            "availableSeats" to 120,
            "status" to EventStatus.ACTIVE,
            "imageUrl" to ""
        ),
        mapOf(
            "title" to "Classic Cinema Night",
            "category" to "Movie",
            "date" to "2026-04-25",
            "location" to "Cinema du Parc, Montreal",
            "availableSeats" to 80,
            "status" to EventStatus.ACTIVE,
            "imageUrl" to ""
        ),
        mapOf(
            "title" to "Montreal Canadiens vs Maple Leafs",
            "category" to "Sports",
            "date" to "2026-04-12",
            "location" to "Bell Centre, Montreal",
            "availableSeats" to 0,
            "status" to EventStatus.ACTIVE,
            "imageUrl" to ""
        ),
        mapOf(
            "title" to "Grand Prix du Canada",
            "category" to "Sports",
            "date" to "2026-06-14",
            "location" to "Circuit Gilles Villeneuve, Montreal",
            "availableSeats" to 300,
            "status" to EventStatus.ACTIVE,
            "imageUrl" to ""
        ),
        mapOf(
            "title" to "European Highlights Tour",
            "category" to "Travel",
            "date" to "2026-07-01",
            "location" to "Paris, France",
            "availableSeats" to 40,
            "status" to EventStatus.ACTIVE,
            "imageUrl" to ""
        ),
        mapOf(
            "title" to "Japan Cherry Blossom Trip",
            "category" to "Travel",
            "date" to "2026-04-05",
            "location" to "Tokyo, Japan",
            "availableSeats" to 25,
            "status" to EventStatus.ACTIVE,
            "imageUrl" to ""
        )
    )

    fun seedIfEmpty(
        db: FirebaseFirestore = FirebaseFirestore.getInstance(),
        onReady: () -> Unit
    ) {
        Log.d("FirestoreSeeder", "Checking if events collection is empty...")
        db.collection("events").limit(1).get()
            .addOnSuccessListener { snapshot ->
                Log.d("FirestoreSeeder", "Check succeeded. isEmpty=${snapshot.isEmpty}")
                if (snapshot.isEmpty) {
                    Log.d("FirestoreSeeder", "Seeding ${sampleEvents.size} events...")
                    var remaining = sampleEvents.size
                    sampleEvents.forEach { event ->
                        db.collection("events").add(event)
                            .addOnSuccessListener { Log.d("FirestoreSeeder", "Added: ${event["title"]}") }
                            .addOnCompleteListener {
                                remaining--
                                if (remaining == 0) {
                                    Log.d("FirestoreSeeder", "Seeding complete, calling onReady")
                                    onReady()
                                }
                            }
                    }
                } else {
                    Log.d("FirestoreSeeder", "Events already exist, calling onReady")
                    onReady()
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreSeeder", "Check failed: ${e.message}", e)
                onReady()
            }
    }
}