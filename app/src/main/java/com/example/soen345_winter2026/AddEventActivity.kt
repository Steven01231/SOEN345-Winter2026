package com.example.soen345_winter2026

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
import java.util.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class AddEventActivity : AppCompatActivity() {

    private lateinit var binding: AddEventBinding

    private var selectedImageUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it // Save the URI to our variable

            // Update UI
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

        setupClickListeners()
    }

    private fun setupClickListeners() {
        // 1. Gallery Picker Trigger
        binding.layoutUploadImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // 2. Date Picker
        binding.etDate.setOnClickListener {
            showDatePicker()
        }

        // 3. The "Create Event" Button (Now linked to Firebase)
        binding.btnCreateEvent.setOnClickListener {
            val title = binding.etEventTitle.text.toString().trim()
            val capacity = binding.etCapacity.text.toString().trim()

            // Simple validation before starting the cloud upload
            if (title.isEmpty()) {
                binding.etEventTitle.error = "Title is required"
                return@setOnClickListener
            }

            if (capacity.isEmpty()) {
                binding.etCapacity.error = "Please specify capacity"
                return@setOnClickListener
            }

            // Call the asynchronous upload/save chain
            saveEventToDatabase()
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
            // Format: YYYY-MM-DD is usually better for databases
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

    private fun saveEventToDatabase() {
        val title = binding.etEventTitle.text.toString()
        val category = binding.etCategory.text.toString()
        val location = binding.etLocation.text.toString()
        val date = binding.etDate.text.toString()
        val capacity = binding.etCapacity.text.toString().toIntOrNull() ?: 0

        if (title.isEmpty()) {
            binding.etEventTitle.error = "Title required"
            return
        }

        binding.btnCreateEvent.isEnabled = false
        Toast.makeText(this, "Uploading event...", Toast.LENGTH_SHORT).show()

        // 1. If user picked an image, upload to Firebase Storage first
        if (selectedImageUri != null) {
            val storageRef = FirebaseStorage.getInstance().reference
            val imageRef = storageRef.child("event_images/${UUID.randomUUID()}.jpg")

            imageRef.putFile(selectedImageUri!!)
                .addOnSuccessListener {
                    // 2. Get the public download URL
                    imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        uploadToFirestore(title, category, location, date, capacity, downloadUri.toString())
                    }
                }
                .addOnFailureListener {
                    binding.btnCreateEvent.isEnabled = true
                    Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
                }
        } else {
            // No image selected, upload with empty URL
            uploadToFirestore(title, category, location, date, capacity, "")
        }
    }

    private fun uploadToFirestore(title: String, cat: String, loc: String, date: String, cap: Int, downloadUrl: String) {
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()

        val adminEmail = auth.currentUser?.email ?: "unknown_admin"

        val uniqueId = "${adminEmail.replace(".", "_")}_${System.currentTimeMillis()}"

        val newEvent = Event(
            uniqueId, title, cat, date, loc,
            cap, "Event Description", EventStatus.ACTIVE,
            null, null, downloadUrl
        ).apply {
            // Associate the admin email with this event
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