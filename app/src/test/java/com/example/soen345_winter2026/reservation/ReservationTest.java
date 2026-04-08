package com.example.soen345_winter2026.reservation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ReservationTest {

    @Test
    void reservation_shouldStorePassedValues() {
        Reservation reservation = new Reservation(
                "res123",
                "user456",
                "event789",
                "2026-04-10 18:30:00",
                90.0,
                "ACTIVE",
                "Jazz Night at Place des Arts",
                "Concert",
                "2026-04-10",
                "Montreal, QC",
                2
        );

        assertEquals("res123", reservation.getReservationID());
        assertEquals("user456", reservation.getUserId());
        assertEquals("event789", reservation.getEventId());
        assertEquals("2026-04-10 18:30:00", reservation.getReservationDate());
        assertEquals(90.0, reservation.getTotalAmount(), 0.001);
        assertEquals("ACTIVE", reservation.getStatus());
        assertEquals("Jazz Night at Place des Arts", reservation.getEventTitle());
        assertEquals("Concert", reservation.getEventCategory());
        assertEquals("2026-04-10", reservation.getEventDate());
        assertEquals("Montreal, QC", reservation.getEventLocation());
        assertEquals(2, reservation.getTicketCount());
    }

    @Test
    void reservation_shouldUseDefaultValues() {
        Reservation reservation = new Reservation();

        assertEquals("", reservation.getReservationID());
        assertEquals("", reservation.getUserId());
        assertEquals("", reservation.getEventId());
        assertEquals("", reservation.getReservationDate());
        assertEquals(0.0, reservation.getTotalAmount(), 0.001);
        assertEquals("", reservation.getStatus());
        assertEquals("", reservation.getEventTitle());
        assertEquals("", reservation.getEventCategory());
        assertEquals("", reservation.getEventDate());
        assertEquals("", reservation.getEventLocation());
        assertEquals(0, reservation.getTicketCount());
    }

    @Test
    void reservation_copy_shouldCreateModifiedObject() {
        Reservation original = new Reservation(
                "res123",
                "user456",
                "event789",
                "2026-04-10 18:30:00",
                90.0,
                "ACTIVE",
                "Jazz Night at Place des Arts",
                "Concert",
                "2026-04-10",
                "Montreal, QC",
                2
        );

        Reservation updated = original.copy(
                "res123",
                "user456",
                "event789",
                "2026-04-10 18:30:00",
                90.0,
                "CANCELLED",
                "Jazz Night at Place des Arts",
                "Concert",
                "2026-04-10",
                "Montreal, QC",
                2
        );

        assertEquals("ACTIVE", original.getStatus());
        assertEquals("CANCELLED", updated.getStatus());
    }

    @Test
    void reservation_objectsWithSameValues_shouldBeEqual() {
        Reservation r1 = new Reservation(
                "res123",
                "user456",
                "event789",
                "2026-04-10 18:30:00",
                90.0,
                "ACTIVE",
                "Jazz Night at Place des Arts",
                "Concert",
                "2026-04-10",
                "Montreal, QC",
                2
        );

        Reservation r2 = new Reservation(
                "res123",
                "user456",
                "event789",
                "2026-04-10 18:30:00",
                90.0,
                "ACTIVE",
                "Jazz Night at Place des Arts",
                "Concert",
                "2026-04-10",
                "Montreal, QC",
                2
        );

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void reservation_differentEventIds_shouldNotBeEqual() {
        Reservation r1 = new Reservation(
                "res123", "user456", "event-A",
                "2026-04-10 18:30:00", 90.0, "ACTIVE",
                "Jazz Night", "Concert", "2026-04-10", "Montreal, QC", 2
        );

        Reservation r2 = new Reservation(
                "res123", "user456", "event-B",
                "2026-04-10 18:30:00", 90.0, "ACTIVE",
                "Jazz Night", "Concert", "2026-04-10", "Montreal, QC", 2
        );

        assertNotEquals(r1, r2);
    }
}
