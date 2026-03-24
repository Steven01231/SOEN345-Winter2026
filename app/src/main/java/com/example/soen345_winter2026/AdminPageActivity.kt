package com.example.soen345_winter2026

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity // Better for UI components
import com.example.soen345_winter2026.databinding.ActivityAdminPageBinding

class AdminPageActivity : AppCompatActivity() {

    // 1. Declare the binding variable
    private lateinit var binding: ActivityAdminPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 2. Initialize binding
        binding = ActivityAdminPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 3. Handle Bottom Navigation Clicks
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> {
                    // Current view
                    true
                }
                R.id.nav_events -> {
                    // Example: Redirect to an Event management page
                    // val intent = Intent(this, EventListActivity::class.java)
                    // startActivity(intent)
                    true
                }
                R.id.nav_analytics -> {
                    Toast.makeText(this, "Analytics coming soon!", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_profile -> {
                    Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }

        // 4. Floating Action Button logic
        /*binding.fabAddEvent.setOnClickListener {
            Toast.makeText(this, "Opening Event Creator...", Toast.LENGTH_SHORT).show()
            // val intent = Intent(this, CreateEventActivity::class.java)
            // startActivity(intent)
        }*/
    }
}