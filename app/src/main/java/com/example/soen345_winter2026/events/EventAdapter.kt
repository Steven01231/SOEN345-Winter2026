package com.example.soen345_winter2026.events

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.soen345_winter2026.R
import com.example.soen345_winter2026.databinding.ItemEventBinding

class EventAdapter(private var events: List<Event>,  private val onBookClick: (Event) -> Unit) :
    RecyclerView.Adapter<EventAdapter.EventViewHolder>() {


    inner class EventViewHolder(private val binding: ItemEventBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(event: Event) {
            binding.tvTitle.text = event.title
            binding.tvCategory.text = event.category
            binding.tvDate.text = event.date
            binding.tvLocation.text = event.location
            binding.tvPrice.text = "$${event.price.toInt()}"


            binding.btnBook.setOnClickListener {
                onBookClick(event)
            }

            if (event.isSoldOut) {
                binding.tvSeats.text = "Sold Out"
                binding.tvSeats.setTextColor(Color.parseColor("#E53935"))
            } else {
                binding.tvSeats.text = "Available: ${event.availableSeats} seats"
                binding.tvSeats.setTextColor(Color.parseColor("#4CAF50"))
            }

            binding.llCategoryBadge.setBackgroundResource(R.drawable.bg_badge_blue)

            if (event.imageUrl.isNotBlank()) {
                Glide.with(binding.root.context)
                    .load(event.imageUrl)
                    .centerCrop()
                    .into(binding.ivEventImage)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(events[position])
    }

    override fun getItemCount(): Int = events.size

    fun updateEvents(newEvents: List<Event>) {
        events = newEvents
        notifyDataSetChanged()
    }
    // To this:
    fun updateData(newEvents: List<Event>) {
        this.events = newEvents
        notifyDataSetChanged()
    }
}
