package com.example.soen345_winter2026.confirmation

import com.example.soen345_winter2026.reservation.Reservation

data class ConfirmationMessage(
    val subject: String,
    val body: String,
    val recipientEmail: String,
    val recipientPhone: String
) {
    companion object {
        fun fromReservation(
            reservation: Reservation,
            recipientEmail: String,
            recipientPhone: String
        ): ConfirmationMessage {
            return ConfirmationMessage(
                subject = "Reservation confirmation for ${reservation.eventTitle}",
                body = """
                    Reservation receipt:
                    Event: ${reservation.eventTitle}
                    Date: ${reservation.eventDate}
                    Location: ${reservation.eventLocation}
                    Tickets: ${reservation.ticketCount}
                    Reservation ID: ${reservation.reservationID}
                """.trimIndent(),
                recipientEmail = recipientEmail,
                recipientPhone = recipientPhone
            )
        }
    }
}