package com.example.soen345_winter2026

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.soen345_winter2026.databinding.ItemTicketBinding
import com.example.soen345_winter2026.events.Reservation

class MyTicketsAdapter(
    private var tickets: List<Reservation>,
    private val onCancelClick: (Reservation) -> Unit
) : RecyclerView.Adapter<MyTicketsAdapter.TicketViewHolder>() {

    inner class TicketViewHolder(private val binding: ItemTicketBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(reservation: Reservation) {
            binding.tvTicketTitle.text = reservation.eventTitle
            binding.tvTicketDate.text = reservation.eventDate
            binding.tvTicketLocation.text = reservation.eventLocation

            if (reservation.isActive) {
                binding.tvTicketStatus.text = "Active"
                binding.tvTicketStatus.setBackgroundResource(R.drawable.cr19370800b00c950)
                binding.btnCancelTicket.visibility = View.VISIBLE
                binding.btnCancelTicket.setOnClickListener { onCancelClick(reservation) }
            } else {
                binding.tvTicketStatus.text = "Cancelled"
                binding.tvTicketStatus.setBackgroundColor(Color.parseColor("#9E9E9E"))
                binding.btnCancelTicket.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        val binding = ItemTicketBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TicketViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        holder.bind(tickets[position])
    }

    override fun getItemCount(): Int = tickets.size

    fun updateTickets(newTickets: List<Reservation>) {
        tickets = newTickets
        notifyDataSetChanged()
    }
}
