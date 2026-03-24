package com.example.soen345_winter2026.events;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EventTest {

    // --- No-arg constructor (Firestore deserialization) ---

    @Test
    void noArgConstructor_setsDefaults() {
        Event event = new Event();
        assertEquals("", event.getId());
        assertEquals("", event.getTitle());
        assertEquals("", event.getCategory());
        assertEquals("", event.getDate());
        assertEquals("", event.getLocation());
        assertEquals(0, event.getAvailableSeats());
        assertEquals("active", event.getStatus());
        assertEquals("", event.getImageUrl());
    }

    // --- Full constructor ---

    @Test
    void fullConstructor_setsAllFields() {
        Event event = new Event("id1", "Jazz Night", "Concert", "2026-04-10",
                "Montreal", 200, "active", "http://img.png");
        assertEquals("id1", event.getId());
        assertEquals("Jazz Night", event.getTitle());
        assertEquals("Concert", event.getCategory());
        assertEquals("2026-04-10", event.getDate());
        assertEquals("Montreal", event.getLocation());
        assertEquals(200, event.getAvailableSeats());
        assertEquals("active", event.getStatus());
        assertEquals("http://img.png", event.getImageUrl());
    }

    // --- Setters ---

    @Test
    void setId_updatesId() {
        Event event = new Event();
        event.setId("abc");
        assertEquals("abc", event.getId());
    }

    @Test
    void setTitle_updatesTitle() {
        Event event = new Event();
        event.setTitle("New Title");
        assertEquals("New Title", event.getTitle());
    }

    @Test
    void setCategory_updatesCategory() {
        Event event = new Event();
        event.setCategory("Movie");
        assertEquals("Movie", event.getCategory());
    }

    @Test
    void setDate_updatesDate() {
        Event event = new Event();
        event.setDate("2026-05-01");
        assertEquals("2026-05-01", event.getDate());
    }

    @Test
    void setLocation_updatesLocation() {
        Event event = new Event();
        event.setLocation("Toronto");
        assertEquals("Toronto", event.getLocation());
    }

    @Test
    void setAvailableSeats_updatesSeats() {
        Event event = new Event();
        event.setAvailableSeats(50);
        assertEquals(50, event.getAvailableSeats());
    }

    @Test
    void setStatus_updatesStatus() {
        Event event = new Event();
        event.setStatus("cancelled");
        assertEquals("cancelled", event.getStatus());
    }

    @Test
    void setImageUrl_updatesImageUrl() {
        Event event = new Event();
        event.setImageUrl("http://example.com/img.jpg");
        assertEquals("http://example.com/img.jpg", event.getImageUrl());
    }

    // --- isSoldOut ---

    @Test
    void isSoldOut_zeroSeats_returnsTrue() {
        Event event = new Event();
        event.setAvailableSeats(0);
        assertTrue(event.isSoldOut());
    }

    @Test
    void isSoldOut_positiveSeats_returnsFalse() {
        Event event = new Event();
        event.setAvailableSeats(1);
        assertFalse(event.isSoldOut());
    }

    @Test
    void isSoldOut_defaultConstructor_returnsTrue() {
        Event event = new Event();
        assertTrue(event.isSoldOut());
    }
}