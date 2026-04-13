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
import com.google.firebase.storage.FirebaseStorage
import java.time.LocalDate
import java.util.*

abstract class BaseEventActivity : AppCompatActivity() {

    protected lateinit var binding: AddEventBinding
    protected var selectedImageUri: Uri? = null
    protected var existingImageUrl: String = ""

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

        setupCommonClickListeners()
        onActivityCreated(savedInstanceState)
    }

    protected open fun onActivityCreated(savedInstanceState: Bundle?) {
        // Override in subclasses for specific initialization
    }

    private fun setupCommonClickListeners() {
        binding.layoutUploadImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.etDate.setOnClickListener {
            showDatePicker()
        }

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }

        binding.btnCreateEvent.setOnClickListener {
            onSaveButtonClicked()
        }
    }

    protected abstract fun onSaveButtonClicked()

    private fun showDatePicker() {
        val now = LocalDate.now()
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            val formattedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, day)
            binding.etDate.setText(formattedDate)
        }

        DatePickerDialog(
            this, dateSetListener,
            now.year,
            now.monthValue - 1,
            now.dayOfMonth
        ).show()
    }

    protected fun uploadImageAndSave(
        title: String,
        category: String,
        location: String,
        date: String,
        capacity: Int,
        onSuccess: (String) -> Unit
    ) {
        if (selectedImageUri != null) {
            val storageRef = FirebaseStorage.getInstance().reference
            val imageRef = storageRef.child("event_images/${UUID.randomUUID()}.jpg")

            imageRef.putFile(selectedImageUri!!)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        onSuccess(downloadUri.toString())
                    }
                }
                .addOnFailureListener {
                    binding.btnCreateEvent.isEnabled = true
                    Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
                }
        } else {
            onSuccess(existingImageUrl)
        }
    }

    protected fun validateFields(): Boolean {
        val title = binding.etEventTitle.text.toString().trim()
        val category = binding.etCategory.text.toString().trim()
        val location = binding.etLocation.text.toString().trim()
        val date = binding.etDate.text.toString().trim()
        val capacity = binding.etCapacity.text.toString().trim()
        val price = binding.etPrice.text.toString().trim()

        if (title.isEmpty()) {
            binding.etEventTitle.error = "Title is required"
            return false
        }

        if (category.isEmpty()) {
            binding.etCategory.error = "Category is required"
            return false
        }

        if (location.isEmpty()) {
            binding.etLocation.error = "Location is required"
            return false
        }

        if (date.isEmpty()) {
            binding.etDate.error = "Date is required"
            return false
        }

        if (capacity.isEmpty()) {
            binding.etCapacity.error = "Capacity is required"
            return false
        }

        if (price.isEmpty()) {
            binding.etPrice.error = "Price is required"
            return false
        }

        if (price.toDoubleOrNull() == null || price.toDouble() < 0) {
            binding.etPrice.error = "Enter a valid price"
            return false
        }

        return true
    }

    protected fun getFieldValues(): FieldValues {
        return FieldValues(
            title = binding.etEventTitle.text.toString().trim(),
            category = binding.etCategory.text.toString().trim(),
            location = binding.etLocation.text.toString().trim(),
            date = binding.etDate.text.toString().trim(),
            capacity = binding.etCapacity.text.toString().toIntOrNull() ?: 0,
            price = binding.etPrice.text.toString().toDoubleOrNull() ?: 0.0
        )
    }

    data class FieldValues(
        val title: String,
        val category: String,
        val location: String,
        val date: String,
        val capacity: Int,
        val price: Double
    )
}
