package com.example.soen345_winter2026.events

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.soen345_winter2026.MyTicketsActivity
import com.example.soen345_winter2026.ProfileActivity
import com.example.soen345_winter2026.R
import com.example.soen345_winter2026.confirmation.ConfirmationManager
import com.example.soen345_winter2026.databinding.ActivityEventListBinding
import com.example.soen345_winter2026.reservation.Reservation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EventListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEventListBinding
    private lateinit var adapter: EventAdapter
    var repository: EventRepository = EventRepository()
    var reservationRepository: ReservationRepository = ReservationRepository()

    private var allEvents: List<Event> = emptyList()
    private var searchQuery = ""
    private var selectedCategory = ""

    private var pendingReservation: Reservation? = null

    private val smsPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        val pending = pendingReservation
        pendingReservation = null
        if (pending != null) sendConfirmation(pending)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupSearch()
        setupCategoryButtons()
        setupBottomNav()
        FirestoreSeeder.seedIfEmpty { loadEvents() }
    }

    private fun setupRecyclerView() {
        adapter = EventAdapter(emptyList()) { event -> bookEvent(event) }
        binding.rvEvents.layoutManager = LinearLayoutManager(this)
        binding.rvEvents.adapter = adapter
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener { text ->
            searchQuery = text.toString()
            applyFilters()
        }
    }

    private fun setupCategoryButtons() {
        val buttons = listOf(
            binding.r6f8umn2i5l6 to "",
            binding.rm2oltgkirt to "Movie",
            binding.rsw2srfk1a0o to "Concert",
            binding.r97kqkl7tb4k to "Travel",
            binding.r03y1ckq5t7pp to "Sports"
        )

        buttons.forEach { (button, category) ->
            button.setOnClickListener {
                selectedCategory = category
                buttons.forEach { (b, _) ->
                    b.setBackgroundResource(R.drawable.bg_chip_inactive)
                    (b.getChildAt(0) as? android.widget.TextView)
                        ?.setTextColor(android.graphics.Color.parseColor("#5c8aa6"))
                }
                button.setBackgroundResource(R.drawable.bg_chip_active)
                (button.getChildAt(0) as? android.widget.TextView)
                    ?.setTextColor(android.graphics.Color.WHITE)
                applyFilters()
            }
        }
    }

    private fun setupBottomNav() {
        setNavActive(binding.navHome, binding.tvNavHome)

        binding.navHome.setOnClickListener {
            setNavActive(binding.navHome, binding.tvNavHome)
            binding.etSearch.setText("")
            binding.etSearch.clearFocus()
        }

        binding.navSearch.setOnClickListener {
            setNavActive(binding.navSearch, binding.tvNavSearch)
            binding.etSearch.requestFocus()
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.etSearch, InputMethodManager.SHOW_IMPLICIT)
        }

        binding.navMyTickets.setOnClickListener {
            startActivity(Intent(this, MyTicketsActivity::class.java))
        }

        binding.navProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    private fun setNavActive(
        activeContainer: android.view.View,
        activeLabel: android.widget.TextView
    ) {
        val navItems = listOf(
            binding.navHome to binding.tvNavHome,
            binding.navSearch to binding.tvNavSearch,
            binding.navMyTickets to binding.tvNavMyTickets,
            binding.navProfile to binding.tvNavProfile
        )
        navItems.forEach { (_, label) -> label.setTextColor(Color.parseColor("#77b1d4")) }
        activeLabel.setTextColor(Color.parseColor("#42657a"))
    }

    private fun loadEvents() {
        binding.progressBar.visibility = View.VISIBLE
        binding.tvEmpty.visibility = View.GONE

        repository.fetchActiveEvents { events, error ->
            binding.progressBar.visibility = View.GONE
            if (error != null) {
                Log.e("EventListActivity", "fetchActiveEvents failed: $error")
                binding.tvEmpty.text = "Failed to load events."
                binding.tvEmpty.visibility = View.VISIBLE
                return@fetchActiveEvents
            }
            allEvents = events
            applyFilters()
        }
    }

    private fun applyFilters() {
        val filtered = EventFilter.filter(allEvents, searchQuery, selectedCategory, "", "")
        adapter.updateEvents(filtered)
        binding.tvEmpty.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun bookEvent(event: Event) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            android.widget.Toast.makeText(this, "Please log in to book events.", android.widget.Toast.LENGTH_SHORT).show()
            return
        }

        if (event.isSoldOut) {
            android.widget.Toast.makeText(this, "This event is sold out.", android.widget.Toast.LENGTH_SHORT).show()
            return
        }

        reservationRepository.bookEvent(userId, event) { success, error ->
            if (success) {
                val reservationId = userId + "_" + event.eventID
                val reservation = Reservation(
                    reservationID = reservationId,
                    userId = userId,
                    reservationDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
                    totalAmount = event.price,
                    status = "active",
                    eventTitle = event.title,
                    eventCategory = event.category,
                    eventDate = event.date,
                    eventLocation = event.location,
                    ticketCount = 1
                )
                requestSmsThenConfirm(reservation)
                showBookingConfirmation(event)
                loadEvents()
            } else {
                val message = when {
                    error?.contains("already booked", ignoreCase = true) == true -> "You have already booked this event."
                    error?.contains("sold out", ignoreCase = true) == true -> "This event is sold out."
                    else -> "Booking failed. Please try again."
                }
                android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun requestSmsThenConfirm(reservation: Reservation) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            sendConfirmation(reservation)
            return
        }
        FirebaseFirestore.getInstance().collection("users").document(userId).get()
            .addOnSuccessListener { doc ->
                val phone = doc.getString("phone") ?: ""
                val granted = ContextCompat.checkSelfPermission(
                    this, Manifest.permission.SEND_SMS
                ) == PackageManager.PERMISSION_GRANTED
                if (phone.isNotEmpty() && !granted) {
                    pendingReservation = reservation
                    smsPermissionLauncher.launch(Manifest.permission.SEND_SMS)
                } else {
                    sendConfirmation(reservation)
                }
            }
            .addOnFailureListener { sendConfirmation(reservation) }
    }

    private fun sendConfirmation(reservation: Reservation) {
        ConfirmationManager.notify(this, reservation) { success, error ->
            runOnUiThread {
                val msg = when {
                    success && error == null -> "Confirmation sent."
                    success -> "Confirmation partially sent: $error"
                    else -> "Confirmation failed: $error"
                }
                if (!success || error != null) Log.e("ConfirmationDebug", msg)
                android.widget.Toast.makeText(
                    this, msg, android.widget.Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun showBookingConfirmation(event: Event) {
        AlertDialog.Builder(this)
            .setTitle("Booking Confirmed!")
            .setMessage("You have successfully booked:\n\n${event.title}\n${event.date}\n${event.location}\n\nView your tickets in My Tickets.")
            .setPositiveButton("My Tickets") { _, _ ->
                startActivity(Intent(this, MyTicketsActivity::class.java))
            }
            .setNegativeButton("Close", null)
            .show()
    }
}
