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
    private int availableSeats = 0;
    private String description = "";
    private EventStatus status = null;
    private LocalDateTime startTime = null;
    private LocalDateTime endTime = null;
    private String imageUrl = "";
    private String creatorEmail = "";

    // Required by Firestore
    public Event() {}

    public Event(String eventID, String title, String category, String date,
                 String location, int availableSeats, String description, EventStatus status,
                 LocalDateTime startTime, LocalDateTime endTime, String imageUrl) {
        this.eventID = eventID;
        this.title = title;
        this.category = category;
        this.description = description;
        this.location = location;
        this.availableSeats = availableSeats;
        this.status = status;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.imageUrl = imageUrl;
    }

    // Getters
    public String getEventID() { return eventID; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public int getAvailableSeats() { return availableSeats; }
    public EventStatus getStatus() { return status; }
    public String getDate() { return date; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public String getImageUrl() { return imageUrl; }
    public boolean isSoldOut() { return availableSeats == 0; }

    // Setters (required by Firestore deserialization)
    public void setEventID(String eventID) { this.eventID = eventID; }
    public void setTitle(String title) { this.title = title; }
    public void setCategory(String category) { this.category = category; }
    public void setDescription(String description) { this.description = description; }
    public void setLocation(String location) { this.location = location; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }
    public void setStatus(EventStatus status) { this.status = status; }
    public void setDate(String date) { this.date = date; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getCreatorEmail() { return creatorEmail; }

    public void setCreatorEmail(String creatorEmail) { this.creatorEmail = creatorEmail; }
}
