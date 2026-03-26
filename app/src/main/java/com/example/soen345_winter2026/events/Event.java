package com.example.soen345_winter2026.events;

import java.time.LocalDateTime;
/**
 * Represents an event fetched from Firestore.
 * Firestore requires a no-arg constructor and setters to deserialize documents.
 */
public class Event {

    private String eventID = ""; // preferably email of user who made the event & number
    private String title = "";
    private String category = "";
    private String date = "";
    private String location = "";
    private int totalSeats = 0;
    private int availableSeats = 0;
    private String description = "";
    private EventStatus status = null;
    private String startTime = "";
    private String endTime = "";
    private String imageUrl = "";
    private String creatorEmail = "";

    // Required by Firestore
    public Event() {}

    public Event(String eventID, String title, 
        String category, String date, String location, 
        int totalSeats, int availableSeats, 
        String description, EventStatus status,
                 LocalDateTime startTime, LocalDateTime endTime, String imageUrl, String creatorEmail) {
        this.eventID = eventID;
        this.title = title;
        this.category = category;
        this.description = description;
        this.location = location;
        this.totalSeats = totalSeats;
        this.availableSeats = availableSeats;
        this.status = status;
        this.date = date;
        this.startTime = startTime.toString();
        this.endTime = endTime.toString();
        this.imageUrl = imageUrl;
        this.creatorEmail = creatorEmail;
    }

    // Getters
    public String getEventID() { return eventID; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public int getTotalSeats() { return totalSeats; }
    public int getAvailableSeats() { return availableSeats; }
    public EventStatus getStatus() { return status; }
    public String getDate() { return date; }
    public LocalDateTime getStartDateTime() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return startTime != null ? LocalDateTime.parse(startTime) : null;
        }
        return null;
    }
    public LocalDateTime getEndDateTime() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return endTime != null ? LocalDateTime.parse(endTime) : null;
        }
        return null;
    }
    public String getImageUrl() { return imageUrl; }
    public boolean isSoldOut() { return availableSeats == 0; }

    // Setters (required by Firestore deserialization)
    public void setEventID(String eventID) { this.eventID = eventID; }
    public void setTitle(String title) { this.title = title; }
    public void setCategory(String category) { this.category = category; }
    public void setDescription(String description) { this.description = description; }
    public void setLocation(String location) { this.location = location; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }
    public void setStatus(EventStatus status) { this.status = status; }
    public void setDate(String date) { this.date = date; }
    public void setStartDateTime(LocalDateTime startTime) {
        this.startTime = startTime != null ? startTime.toString() : "";
    }
    public void setEndDateTime(LocalDateTime endTime) {
        this.endTime = endTime != null ? endTime.toString() : "";
    }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getCreatorEmail() { return creatorEmail; }

    public void setCreatorEmail(String creatorEmail) { this.creatorEmail = creatorEmail; }
}
