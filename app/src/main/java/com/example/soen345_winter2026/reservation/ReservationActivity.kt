package com.example.soen345_winter2026.reservation

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.soen345_winter2026.databinding.ReservationBinding
import java.text.NumberFormat
import java.util.Locale

import com.example.soen345_winter2026.confirmation.ConfirmationManager
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID

class ReservationActivity : AppCompatActivity() {
    private lateinit var binding: ReservationBinding
    private var ticketCount = 1
    private var availableSeats = 0
    private var pricePerTicket = 0.0

    private val db = FirebaseFirestore.getInstance()

    private val smsPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(this, "SMS permission denied, confirmation will be email only", Toast.LENGTH_SHORT).show()
        }
        pendingReservation?.let { confirmReservation(it) }
        pendingReservation = null
    }

    private var pendingReservation: Reservation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ReservationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = intent.getStringExtra("event_title") ?: ""
        val category = intent.getStringExtra("event_category") ?: ""
        val date = intent.getStringExtra("event_date") ?: ""
        val location = intent.getStringExtra("event_location") ?: ""
        val imageUrl = intent.getStringExtra("event_image_url") ?: ""

        availableSeats = intent.getIntExtra("event_seats", 0)
        pricePerTicket = intent.getDoubleExtra("event_price", 0.0)
        binding.tvReservationTitle.text = title
        binding.tvReservationCategoryBadge.text = category
        binding.tvReservationDate.text = date
        binding.tvReservationLocation.text = location
        binding.tvReservationSeats.text = "Available: $availableSeats seats"
        binding.tvReservationPrice.text = "Price per ticket: ${formatPrice(pricePerTicket)}"
        if (imageUrl.isNotBlank()) {
            Glide.with(this)
                .load(imageUrl)
                .centerCrop()
                .into(binding.ivReservationImage)
        }
        if (availableSeats <= 0) {
            ticketCount = 0
            binding.btnConfirmReservation.isEnabled = false
        }
        updateTicketUI()
        binding.btnPlus.setOnClickListener {
            if (ticketCount < availableSeats) {
                ticketCount++
                updateTicketUI()
            } else {
                Toast.makeText(this, "No more seats available", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnMinus.setOnClickListener {
            if (ticketCount > 1) {
                ticketCount--
                updateTicketUI()
            }
        }

        binding.btnConfirmReservation.setOnClickListener {
            val reservationId = UUID.randomUUID().toString()
            val total = ticketCount * pricePerTicket
            val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(Date())

            val reservation = Reservation(
                reservationID = reservationId,
                userId = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                reservationDate = currentDate,
                totalAmount = total,
                status = "ACTIVE",
                eventTitle = title,
                eventCategory = category,
                eventDate = date,
                eventLocation = location,
                ticketCount = ticketCount
            )

            db.collection("reservations")
                .document(reservationId)
                .set(reservation)
                .addOnSuccessListener {
                    checkSmsPermissionAndNotify(reservation)
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this,
                        "Failed to save reservation",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun checkSmsPermissionAndNotify(reservation: Reservation) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                val phone = document.getString("phone") ?: ""
                val hasPhone = phone.isNotEmpty()

                if (!hasPhone) {
                    confirmReservation(reservation)
                    return@addOnSuccessListener
                }

                when {
                    ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                            == PackageManager.PERMISSION_GRANTED -> {
                        confirmReservation(reservation)
                    }
                    else -> {
                        pendingReservation = reservation
                        smsPermissionLauncher.launch(Manifest.permission.SEND_SMS)
                    }
                }
            }
            .addOnFailureListener {
                confirmReservation(reservation)
            }
    }

    private fun confirmReservation(reservation: Reservation) {
        ConfirmationManager.notify(this, reservation) { success, error ->
            runOnUiThread {
                val message = if (success) {
                    "Reserved ${reservation.ticketCount} ticket(s) — confirmation sent"
                } else {
                    "Reserved but confirmation failed: $error"
                }
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }

        val intent = Intent(this, MyReservationActivity::class.java)
        intent.putExtra("reservation_id", reservation.reservationID)
        startActivity(intent)
    }

    private fun updateTicketUI() {
        binding.tvTicketCount.text = ticketCount.toString()
        val total = ticketCount * pricePerTicket
        binding.tvTotalPrice.text = formatPrice(total)
    }
    private fun formatPrice(price: Double): String {
        return NumberFormat.getCurrencyInstance(Locale.US).format(price)
    }
}