package com.example.soen345_winter2026

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.soen345_winter2026.databinding.AddEventBinding
import com.example.soen345_winter2026.events.Event
import com.example.soen345_winter2026.events.EventStatus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.time.LocalDateTime
import java.util.*

class AddEventActivity : AppCompatActivity() {

    private lateinit var binding: AddEventBinding

    private var selectedImageUri: Uri? = null

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

        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Gallery Picker
        binding.layoutUploadImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // Date Picker
        binding.etDate.setOnClickListener {
            showDatePicker()
        }

        // Start Time Picker
        binding.etStartTime.setOnClickListener {
            showTimePicker { time -> binding.etStartTime.setText(time) }
        }

        // End Time Picker
        binding.etEndTime.setOnClickListener {
            showTimePicker { time -> binding.etEndTime.setText(time) }
        }

        // Create Event Button
        binding.btnCreateEvent.setOnClickListener {
            if (validateForm()) {
                saveEventToDatabase()
            }
        }

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun validateForm(): Boolean {
        val title       = binding.etEventTitle.text.toString().trim()
        val date        = binding.etDate.text.toString().trim()
        val startTime   = binding.etStartTime.text.toString().trim()
        val endTime     = binding.etEndTime.text.toString().trim()
        val capacity    = binding.etCapacity.text.toString().trim()
        val ticketPrice = binding.etTicketPrice.text.toString().trim()

        if (title.isEmpty()) {
            binding.etEventTitle.error = "Title is required"
            return false
        }
        if (date.isEmpty()) {
            binding.etDate.error = "Date is required"
            return false
        }
        if (startTime.isEmpty()) {
            binding.etStartTime.error = "Start time is required"
            return false
        }
        if (endTime.isEmpty()) {
            binding.etEndTime.error = "End time is required"
            return false
        }
        if (capacity.isEmpty()) {
            binding.etCapacity.error = "Capacity is required"
            return false
        }
        if (ticketPrice.isEmpty()) {
            binding.etTicketPrice.error = "Ticket price is required"
            return false
        }
        return true
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, day ->
                binding.etDate.setText(String.format("%d-%02d-%02d", year, month + 1, day))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePicker(onTimeSet: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            this,
            { _, hour, minute ->
                onTimeSet(String.format("%02d:%02d", hour, minute))
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true // 24hr format
        ).show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun toLocalDateTime(date: String, time: String): LocalDateTime? {
        return try {
            LocalDateTime.parse("${date}T${time}")
        } catch (e: Exception) {
            null
        }
    }

    private fun saveEventToDatabase() {
        val title       = binding.etEventTitle.text.toString().trim()
        val category    = binding.etCategory.text.toString().trim()
        val location    = binding.etLocation.text.toString().trim()
        val date        = binding.etDate.text.toString().trim()
        val startTime   = binding.etStartTime.text.toString().trim()
        val endTime     = binding.etEndTime.text.toString().trim()
        val capacity    = binding.etCapacity.text.toString().toIntOrNull() ?: 0
        val ticketPrice = binding.etTicketPrice.text.toString().toIntOrNull() ?: 0
        val description = binding.etDescription.text.toString().trim()

        // Convert HH:MM + date into LocalDateTime (requires API 26+)
        val startDateTime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            toLocalDateTime(date, startTime)
        } else null

        val endDateTime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            toLocalDateTime(date, endTime)
        } else null

        binding.btnCreateEvent.isEnabled = false
        Toast.makeText(this, "Uploading event...", Toast.LENGTH_SHORT).show()

        if (selectedImageUri != null) {
            val storageRef = FirebaseStorage.getInstance().reference
            val imageRef = storageRef.child("event_images/${UUID.randomUUID()}.jpg")

            imageRef.putFile(selectedImageUri!!)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        uploadToFirestore(
                            title, category, location, date,
                            startDateTime, endDateTime,
                            capacity, ticketPrice, description,
                            downloadUri.toString()
                        )
                    }
                }
                .addOnFailureListener {
                    binding.btnCreateEvent.isEnabled = true
                    Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
                }
        } else {
            uploadToFirestore(
                title, category, location, date,
                startDateTime, endDateTime,
                capacity, ticketPrice, description,
                ""
            )
        }
    }

    private fun uploadToFirestore(
        title: String,
        category: String,
        location: String,
        date: String,
        startDateTime: LocalDateTime?,
        endDateTime: LocalDateTime?,
        capacity: Int,
        ticketPrice: Int,
        description: String,
        downloadUrl: String
    ) {
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val adminEmail = auth.currentUser?.email ?: "unknown_admin"
        val uniqueId = "${adminEmail.replace(".", "_")}_${System.currentTimeMillis()}"

        val newEvent = Event(
            uniqueId, title, category, date, location,
            capacity, capacity, description, EventStatus.ACTIVE,
            startDateTime, endDateTime, downloadUrl, adminEmail, ticketPrice
        )

        db.collection("events")
            .document(uniqueId)
            .set(newEvent)
            .addOnSuccessListener {
                Toast.makeText(this, "Event created successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                binding.btnCreateEvent.isEnabled = true
                Toast.makeText(this, "Firestore error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}