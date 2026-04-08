package com.example.soen345_winter2026.reservation

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.soen345_winter2026.databinding.ReservationBinding
import java.text.NumberFormat
import java.util.Locale

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID

class ReservationActivity : AppCompatActivity() {
    private lateinit var binding: ReservationBinding
    private lateinit var calculator: ReservationCalculator
    private var eventId = ""

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
        eventId = intent.getStringExtra("event_id") ?: ""

        val availableSeats = intent.getIntExtra("event_seats", 0)
        val pricePerTicket = intent.getDoubleExtra("event_price", 0.0)

        calculator = ReservationCalculator(availableSeats, pricePerTicket)

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

        binding.btnConfirmReservation.isEnabled = calculator.canConfirm()

        updateTicketUI()

        binding.btnPlus.setOnClickListener {
            if (!calculator.increase()) {
                Toast.makeText(this, "No more seats available", Toast.LENGTH_SHORT).show()
            }
            updateTicketUI()
        }
        binding.btnMinus.setOnClickListener {
            calculator.decrease()
            updateTicketUI()
        }

        binding.btnConfirmReservation.setOnClickListener {
            val reservationId = UUID.randomUUID().toString()
            val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(Date())

            val reservation = Reservation(
                reservationID = reservationId,
                userId = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                eventId = eventId,
                reservationDate = currentDate,
                totalAmount = calculator.getTotalPrice(),
                status = "ACTIVE",
                eventTitle = title,
                eventCategory = category,
                eventDate = date,
                eventLocation = location,
                ticketCount = calculator.getTicketCount()
            )

            db.collection("reservations")
                .document(reservationId)
                .set(reservation)
                .addOnSuccessListener {
                    decrementSeats(eventId, calculator.getTicketCount())
                    Toast.makeText(
                        this,
                        "Reserved ${calculator.getTicketCount()} ticket(s)",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this, MyReservationActivity::class.java)
                    intent.putExtra("reservation_id", reservationId)
                    startActivity(intent)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to save reservation", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun decrementSeats(eventId: String, count: Int) {
        if (eventId.isBlank()) return
        db.collection("events")
            .document(eventId)
            .update("availableSeats", FieldValue.increment(-count.toLong()))
    }

    private fun updateTicketUI() {
        binding.tvTicketCount.text = calculator.getTicketCount().toString()
        binding.tvTotalPrice.text = formatPrice(calculator.getTotalPrice())
    }

    private fun formatPrice(price: Double): String {
        return NumberFormat.getCurrencyInstance(Locale.US).format(price)
    }
}
