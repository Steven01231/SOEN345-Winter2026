package com.example.soen345_winter2026

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.soen345_winter2026.databinding.ActivityAdminPageBinding
import com.example.soen345_winter2026.events.Event
import com.example.soen345_winter2026.events.EventAdapter
import com.google.firebase.firestore.FirebaseFirestore

class AdminPageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminPageBinding

    // Adapter for the event list
    private lateinit var eventAdapter: EventAdapter
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
        // Initialize adapter with an empty list
        eventAdapter = EventAdapter(mutableListOf())

        binding.rvEvents.apply {
            layoutManager = LinearLayoutManager(this@AdminPageActivity)
            adapter = eventAdapter
            // Optional: optimization if you know layout size won't change
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
                eventAdapter.updateEvents(eventList)
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
}