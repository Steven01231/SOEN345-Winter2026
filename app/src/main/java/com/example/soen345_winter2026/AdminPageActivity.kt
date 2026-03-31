package com.example.soen345_winter2026

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.soen345_winter2026.admin.EditEventActivity
import com.example.soen345_winter2026.databinding.ActivityAdminPageBinding
import com.example.soen345_winter2026.events.AdminEventAdapter
import com.example.soen345_winter2026.events.Event
import com.example.soen345_winter2026.events.EventAdapter
import com.google.firebase.firestore.FirebaseFirestore

class AdminPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminPageBinding

    // Adapter for the event list
    private lateinit var adminEventAdapter: AdminEventAdapter
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdminPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupClickListeners()
        listenForEvents()
    }

    private fun setupRecyclerView() {
        adminEventAdapter = AdminEventAdapter(
            mutableListOf(),
            { event ->
                val intent = Intent(this, EditEventActivity::class.java).apply {
                    putExtra("eventId", event.eventID)
                    putExtra("title", event.title)
                    putExtra("category", event.category)
                    putExtra("location", event.location)
                    putExtra("date", event.date)
                    putExtra("availableSeats", event.availableSeats)
                    putExtra("imageUrl", event.imageUrl ?: "")
                }
                startActivity(intent)  },
            { event ->
                showCancelConfirmationDialog(event)
            }
        )

        binding.rvEvents.apply {
            layoutManager = LinearLayoutManager(this@AdminPageActivity)
            adapter = adminEventAdapter
            setHasFixedSize(true)
        }
    }

    private fun listenForEvents() {
        val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
        val currentAdminEmail = auth.currentUser?.email ?: ""

        db.collection("events")
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                val eventList = mutableListOf<Event>()

                snapshots?.forEach { doc ->
                    try {
                        val event = doc.toObject(Event::class.java)

                        // Only add the event to the list if the creator matches the logged-in admin
                        if (event.creatorEmail == currentAdminEmail) {
                            eventList.add(event)
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("Firestore", "Mapping error: ${e.message}")
                    }
                }

                // Update the adapter with only the admin's filtered events
                adminEventAdapter.updateEvents(eventList)
            }
    }

    private fun setupClickListeners() {
        // Switch to Customer View (Placeholder for testing)
        binding.tvCustomerView?.setOnClickListener {
            Toast.makeText(this, "Switching to User View...", Toast.LENGTH_SHORT).show()
            // val intent = Intent(this, UserActivity::class.java)
            // startActivity(intent)
        }

        // Floating Action Button
        binding.fabAddEvent.setOnClickListener {
            val intent = Intent(this, AddEventActivity::class.java)
            startActivity(intent)
        }

        // Bottom Navigation Logic
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> true // Already here
                R.id.nav_events -> {
                    Toast.makeText(this, "Events Management View", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_analytics -> {
                    Toast.makeText(this, "Analytics coming soon!", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_profile -> {
                    Toast.makeText(this, "Profile settings", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }

    private fun showCancelConfirmationDialog(event: Event) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Cancel Event")
            .setMessage("Are you sure you want to cancel \"${event.title}\"?")
            .setPositiveButton("Yes") { _, _ ->
                // 👉 User confirmed cancel
                cancelEvent(event)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun cancelEvent(event: Event) {
        db.collection("events")
            .whereEqualTo("title", event.title) // ⚠️ ideally use ID instead
            .get()
            .addOnSuccessListener { result ->
                for (doc in result) {
                    db.collection("events").document(doc.id).delete()
                }
                Toast.makeText(this, "Event cancelled", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to cancel event", Toast.LENGTH_SHORT).show()
            }
    }
}