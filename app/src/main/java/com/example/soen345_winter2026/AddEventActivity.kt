package com.example.soen345_winter2026

import android.os.Bundle
import android.widget.Toast
import com.example.soen345_winter2026.events.Event
import com.example.soen345_winter2026.events.EventStatus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddEventActivity : BaseEventActivity() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        // Set button text for adding
        binding.btnCreateEvent.text = "Create Event"
    }

    override fun onSaveButtonClicked() {
        if (!validateFields()) return

        val values = getFieldValues()

        binding.btnCreateEvent.isEnabled = false
        Toast.makeText(this, "Creating event...", Toast.LENGTH_SHORT).show()

        uploadImageAndSave(values.title, values.category, values.location, values.date, values.capacity) { imageUrl ->
            saveEventToDatabase(values, imageUrl)
        }
    }

    private fun saveEventToDatabase(values: FieldValues, imageUrl: String) {
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()

        val adminEmail = auth.currentUser?.email ?: "unknown_admin"
        val uniqueId = "${adminEmail.replace(".", "_")}_${System.currentTimeMillis()}"

        val newEvent = Event(
            uniqueId, values.title, values.category, values.date, values.location,
            values.capacity, "Event Description", EventStatus.ACTIVE,
            null, null, imageUrl
        ).apply {
            this.creatorEmail = adminEmail
        }

        db.collection("events")
            .document(uniqueId)
            .set(newEvent)
            .addOnSuccessListener {
                Toast.makeText(this, "Event Created by $adminEmail", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                binding.btnCreateEvent.isEnabled = true
                Toast.makeText(this, "Firestore error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
