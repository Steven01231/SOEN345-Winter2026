package com.example.soen345_winter2026.events

data class Event(
    val id: String = "",
    val title: String = "",
    val category: String = "",
    val date: String = "",
    val location: String = "",
    val availableSeats: Int = 0,
    val status: String = "active",
    val imageUrl: String = ""
) {
    val isSoldOut: Boolean get() = availableSeats == 0
}
