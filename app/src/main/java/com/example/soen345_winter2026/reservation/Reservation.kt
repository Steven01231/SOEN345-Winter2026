package com.example.soen345_winter2026.reservation

data class Reservation(
    val reservationID: String = "",
    val reservationDate: String = "",
    val totalAmount: Double = 0.0,
    val status: String = "",
    val eventTitle: String = "",
    val eventCategory: String = "",
    val eventDate: String = "",
    val eventLocation: String = "",
    val ticketCount: Int = 0
)