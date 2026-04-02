package com.example.soen345_winter2026.reservation

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.soen345_winter2026.databinding.ReservationBinding
import java.text.NumberFormat
import java.util.Locale

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
                    Toast.makeText(
                        this,
                        "Reserved $ticketCount ticket(s)",
                        Toast.LENGTH_SHORT
                    ).show()

                    val intent = Intent(this, MyReservationActivity::class.java)
                    intent.putExtra("reservation_id", reservationId)
                    startActivity(intent)
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
    private fun updateTicketUI() {
        binding.tvTicketCount.text = ticketCount.toString()
        val total = ticketCount * pricePerTicket
        binding.tvTotalPrice.text = formatPrice(total)
    }
    private fun formatPrice(price: Double): String {
        return NumberFormat.getCurrencyInstance(Locale.US).format(price)
    }
}