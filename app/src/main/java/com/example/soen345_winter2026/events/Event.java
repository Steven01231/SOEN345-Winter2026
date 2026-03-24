package com.example.soen345_winter2026.events;

/**
 * Represents an event fetched from Firestore.
 * Firestore requires a no-arg constructor and setters to deserialize documents.
 */
public class Event {

    private String id = "";
    private String title = "";
    private String category = "";
    private String date = "";
    private String location = "";
    private int availableSeats = 0;
    private String status = "active";
    private String imageUrl = "";

    // Required by Firestore
    public Event() {}

    public Event(String id, String title, String category, String date,
                 String location, int availableSeats, String status, String imageUrl) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.date = date;
        this.location = location;
        this.availableSeats = availableSeats;
        this.status = status;
        this.imageUrl = imageUrl;
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getDate() { return date; }
    public String getLocation() { return location; }
    public int getAvailableSeats() { return availableSeats; }
    public String getStatus() { return status; }
    public String getImageUrl() { return imageUrl; }
    public boolean isSoldOut() { return availableSeats == 0; }

    // Setters (required by Firestore deserialization)
    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setCategory(String category) { this.category = category; }
    public void setDate(String date) { this.date = date; }
    public void setLocation(String location) { this.location = location; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }
    public void setStatus(String status) { this.status = status; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
