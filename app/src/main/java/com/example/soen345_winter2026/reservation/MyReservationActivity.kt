package com.example.soen345_winter2026.reservation

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.soen345_winter2026.databinding.ActivityMyReservationBinding
import com.google.firebase.firestore.FirebaseFirestore

class MyReservationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyReservationBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyReservationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val reservationId = intent.getStringExtra("reservation_id") ?: return

        loadReservation(reservationId)

        binding.btnCancelReservation.setOnClickListener {
            db.collection("reservations")
                .document(reservationId)
                .update("status", "CANCELLED")
                .addOnSuccessListener {
                    Toast.makeText(this, "Reservation cancelled", Toast.LENGTH_SHORT).show()
                    loadReservation(reservationId)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to cancel reservation", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun loadReservation(reservationId: String) {
        db.collection("reservations")
            .document(reservationId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val reservation = document.toObject(Reservation::class.java)

                    if (reservation != null) {
                        binding.tvReservationId.text = reservation.reservationID
                        binding.tvReservationDate.text = reservation.reservationDate
                        binding.tvReservationStatus.text = reservation.status
                        binding.tvEventTitle.text = reservation.eventTitle
                        binding.tvEventCategory.text = reservation.eventCategory
                        binding.tvEventDate.text = reservation.eventDate
                        binding.tvEventLocation.text = reservation.eventLocation
                        binding.tvTicketCount.text = reservation.ticketCount.toString()
                        binding.tvTotalAmount.text = "$${reservation.totalAmount}"

                        binding.btnCancelReservation.isEnabled =
                            reservation.status == "ACTIVE"
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load reservation", Toast.LENGTH_SHORT).show()
            }
    }
}