package com.example.soen345_winter2026

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.soen345_winter2026.databinding.AddEventBinding // Ensure this matches your XML filename
import java.util.*

class AddEventActivity : AppCompatActivity() {

    private lateinit var binding: AddEventBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Back Button
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Date Picker
        binding.etDate.setOnClickListener {
            showDatePicker()
        }

        // Time Picker
        binding.etTime.setOnClickListener {
            showTimePicker()
        }

        // Create Event Button
        binding.btnCreateEvent.setOnClickListener {
            val title = binding.etEventTitle.text.toString()
            if (title.isNotEmpty()) {
                // Logic to call your upload function goes here
                Toast.makeText(this, "Creating Event: $title", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show()
            }
        }

        // Cancel Button
        binding.btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            binding.etDate.setText("$day/${month + 1}/$year")
        }

        DatePickerDialog(
            this, dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            binding.etTime.setText(String.format("%02d:%02d", hour, minute))
        }

        TimePickerDialog(
            this, timeSetListener,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }
}