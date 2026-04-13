package com.example.soen345_winter2026.admin

import android.widget.Toast
import com.example.soen345_winter2026.BaseEventActivity
import com.google.firebase.firestore.FirebaseFirestore

class EditEventActivity : BaseEventActivity() {

    private var eventId: String = ""

    override fun onActivityCreated(savedInstanceState: android.os.Bundle?) {
        loadEventData()
        binding.btnCreateEvent.text = "Update Event"
    }

    private fun loadEventData() {
        eventId = intent.getStringExtra("eventId") ?: ""
        binding.etEventTitle.setText(intent.getStringExtra("title"))
        binding.etCategory.setText(intent.getStringExtra("category"))
        binding.etLocation.setText(intent.getStringExtra("location"))
        binding.etDate.setText(intent.getStringExtra("date"))
        binding.etCapacity.setText(intent.getIntExtra("availableSeats", 0).toString())
        binding.etPrice.setText(intent.getDoubleExtra("price", 0.0).toString())

        existingImageUrl = intent.getStringExtra("imageUrl") ?: ""
        if (existingImageUrl.isNotBlank()) {
            com.bumptech.glide.Glide.with(this)
                .load(existingImageUrl)
                .centerCrop()
                .into(binding.ivImagePreview)
            binding.ivImagePreview.layoutParams.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT
            binding.ivImagePreview.layoutParams.height = android.view.ViewGroup.LayoutParams.MATCH_PARENT
            binding.ivImagePreview.scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
            binding.tvUploadStatus.visibility = android.view.View.GONE
        }
    }

    override fun onSaveButtonClicked() {
        if (!validateFields()) return

        val values = getFieldValues()

        binding.btnCreateEvent.isEnabled = false
        Toast.makeText(this, "Updating event...", Toast.LENGTH_SHORT).show()

        uploadImageAndSave(values.title, values.category, values.location, values.date, values.capacity) { imageUrl ->
            updateEventInDatabase(values, imageUrl)
        }
    }

    private fun updateEventInDatabase(values: FieldValues, imageUrl: String) {
        val db = FirebaseFirestore.getInstance()

        val updatedEvent = hashMapOf(
            "title" to values.title,
            "category" to values.category,
            "location" to values.location,
            "date" to values.date,
            "availableSeats" to values.capacity,
            "price" to values.price,
            "imageUrl" to imageUrl
        )

        db.collection("events")
            .document(eventId)
            .set(updatedEvent, com.google.firebase.firestore.SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(this, "Event updated!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                binding.btnCreateEvent.isEnabled = true
                Toast.makeText(this, "Failed to update event: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}