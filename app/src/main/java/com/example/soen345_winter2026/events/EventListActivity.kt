package com.example.soen345_winter2026.events

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.soen345_winter2026.R
import com.example.soen345_winter2026.databinding.ActivityEventListBinding

class EventListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEventListBinding
    private lateinit var adapter: EventAdapter
    private val repository = EventRepository()

    private var allEvents: List<Event> = emptyList()
    private var searchQuery = ""
    private var selectedCategory = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupSearch()
        setupCategoryButtons()
        loadEvents()
    }

    private fun setupRecyclerView() {
        adapter = EventAdapter(emptyList())
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
                // Reset all to inactive style
                buttons.forEach { (b, _) ->
                    b.setBackgroundResource(R.drawable.cr19370800bf5f5f5)
                    (b.getChildAt(0) as? android.widget.TextView)
                        ?.setTextColor(android.graphics.Color.parseColor("#757575"))
                }
                // Set active style
                button.setBackgroundResource(R.drawable.cr19370800b2196f3)
                (button.getChildAt(0) as? android.widget.TextView)
                    ?.setTextColor(android.graphics.Color.WHITE)

                applyFilters()
            }
        }
    }

    private fun loadEvents() {
        binding.progressBar.visibility = View.VISIBLE
        binding.tvEmpty.visibility = View.GONE

        repository.fetchActiveEvents { events, error ->
            binding.progressBar.visibility = View.GONE
            if (error != null) {
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
}
