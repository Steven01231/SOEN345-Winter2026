package com.example.soen345_winter2026.admin

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.soen345_winter2026.databinding.AddEventBinding
import com.example.soen345_winter2026.events.Event
import com.example.soen345_winter2026.events.EventStatus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class EditEventActivity : AppCompatActivity() {

    private lateinit var binding: AddEventBinding
    private var selectedImageUri: Uri? = null
    private var eventId: String = ""
    private var existingImageUrl: String = ""

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            binding.ivImagePreview.setImageURI(it)
            binding.ivImagePreview.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            binding.ivImagePreview.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            binding.ivImagePreview.scaleType = ImageView.ScaleType.CENTER_CROP
            binding.tvUploadStatus.visibility = View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadEventData()
        setupClickListeners()
    }

    private fun loadEventData() {
        // Pull data from intent
        eventId = intent.getStringExtra("eventId") ?: ""
        binding.etEventTitle.setText(intent.getStringExtra("title"))
        binding.etCategory.setText(intent.getStringExtra("category"))
        binding.etLocation.setText(intent.getStringExtra("location"))
        binding.etDate.setText(intent.getStringExtra("date"))
        binding.etCapacity.setText(intent.getIntExtra("availableSeats", 0).toString())

        existingImageUrl = intent.getStringExtra("imageUrl") ?: ""
        if (existingImageUrl.isNotBlank()) {
            // Show existing image
            val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(existingImageUrl)
            // Or use Glide/Picasso if needed
            binding.ivImagePreview.setImageURI(Uri.parse(existingImageUrl))
            binding.tvUploadStatus.visibility = View.GONE
        }

        // Change button text to "Update Event"
        binding.btnCreateEvent.text = "Update Event"
    }

    private fun setupClickListeners() {
        binding.layoutUploadImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.etDate.setOnClickListener {
            showDatePicker()
        }

        binding.btnCreateEvent.setOnClickListener {
            updateEventInDatabase()
        }

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            val formattedDate = String.format("%d-%02d-%02d", year, month + 1, day)
            binding.etDate.setText(formattedDate)
        }

        DatePickerDialog(
            this, dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateEventInDatabase() {
        val title = binding.etEventTitle.text.toString().trim()
        val category = binding.etCategory.text.toString().trim()
        val location = binding.etLocation.text.toString().trim()
        val date = binding.etDate.text.toString().trim()
        val capacity = binding.etCapacity.text.toString().toIntOrNull() ?: 0

        if (title.isEmpty() || category.isEmpty() || location.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnCreateEvent.isEnabled = false
        Toast.makeText(this, "Updating event...", Toast.LENGTH_SHORT).show()

        // If a new image is selected, upload it
        if (selectedImageUri != null) {
            val storageRef = FirebaseStorage.getInstance().reference
            val imageRef = storageRef.child("event_images/${UUID.randomUUID()}.jpg")

            imageRef.putFile(selectedImageUri!!)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        saveUpdatedEvent(title, category, location, date, capacity, downloadUri.toString())
                    }
                }
                .addOnFailureListener {
                    binding.btnCreateEvent.isEnabled = true
                    Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
                }
        } else {
            // No new image, keep the old one
            saveUpdatedEvent(title, category, location, date, capacity, existingImageUrl)
        }
    }

    private fun saveUpdatedEvent(
        title: String,
        category: String,
        location: String,
        date: String,
        capacity: Int,
        imageUrl: String
    ) {
        val db = FirebaseFirestore.getInstance()

        // Only update the fields that actually changed
        val updatedEvent = hashMapOf(
            "title" to title,
            "category" to category,
            "location" to location,
            "date" to date,
            "availableSeats" to capacity,
            "imageUrl" to imageUrl
        )

        db.collection("events")
            .document(eventId)
            .set(updatedEvent, com.google.firebase.firestore.SetOptions.merge()) // safer merge
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