package com.example.soen345_winter2026

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.soen345_winter2026.databinding.ActivityMyTicketsBinding
import com.example.soen345_winter2026.events.Reservation
import com.example.soen345_winter2026.events.ReservationRepository
import com.google.firebase.auth.FirebaseAuth

class MyTicketsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyTicketsBinding
    private lateinit var adapter: MyTicketsAdapter
    var reservationRepository: ReservationRepository = ReservationRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyTicketsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ibtnBack.setOnClickListener { finish() }

        setupRecyclerView()
        loadTickets()
    }

    private fun setupRecyclerView() {
        adapter = MyTicketsAdapter(emptyList()) { reservation ->
            confirmCancel(reservation)
        }
        binding.rvTickets.layoutManager = LinearLayoutManager(this)
        binding.rvTickets.adapter = adapter
    }

    private fun loadTickets() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        binding.progressBar.visibility = View.VISIBLE
        binding.tvEmpty.visibility = View.GONE

        reservationRepository.getUserReservations(userId) { reservations, error ->
            binding.progressBar.visibility = View.GONE
            if (error != null) {
                binding.tvEmpty.text = "Failed to load tickets."
                binding.tvEmpty.visibility = View.VISIBLE
                return@getUserReservations
            }
            if (reservations.isEmpty()) {
                binding.tvEmpty.visibility = View.VISIBLE
            } else {
                adapter.updateTickets(reservations)
            }
        }
    }

    private fun confirmCancel(reservation: Reservation) {
        AlertDialog.Builder(this)
            .setTitle("Cancel Reservation")
            .setMessage("Cancel your booking for \"${reservation.eventTitle}\"?")
            .setPositiveButton("Yes, Cancel") { _, _ -> cancelReservation(reservation) }
            .setNegativeButton("No", null)
            .show()
    }

    private fun cancelReservation(reservation: Reservation) {
        reservationRepository.cancelReservation(
            reservation.reservationId,
            reservation.eventId
        ) { success, error ->
            if (success) {
                android.widget.Toast.makeText(this, "Reservation cancelled.", android.widget.Toast.LENGTH_SHORT).show()
                loadTickets()
            } else {
                android.widget.Toast.makeText(this, "Failed to cancel: $error", android.widget.Toast.LENGTH_LONG).show()
            }
        }
    }
}
