package com.example.soen345_winter2026.events;

public class Reservation {

    private String reservationId;
    private String userId;
    private String eventId;
    private String eventTitle;
    private String eventDate;
    private String eventLocation;
    private String status;
    private long createdAt;

    public Reservation() {}

    public Reservation(String reservationId, String userId, String eventId,
                       String eventTitle, String eventDate, String eventLocation,
                       String status, long createdAt) {
        this.reservationId = reservationId;
        this.userId = userId;
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.eventDate = eventDate;
        this.eventLocation = eventLocation;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getReservationId() { return reservationId; }
    public String getUserId() { return userId; }
    public String getEventId() { return eventId; }
    public String getEventTitle() { return eventTitle; }
    public String getEventDate() { return eventDate; }
    public String getEventLocation() { return eventLocation; }
    public String getStatus() { return status; }
    public long getCreatedAt() { return createdAt; }
    public boolean isActive() { return "active".equals(status); }

    public void setReservationId(String reservationId) { this.reservationId = reservationId; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public void setEventTitle(String eventTitle) { this.eventTitle = eventTitle; }
    public void setEventDate(String eventDate) { this.eventDate = eventDate; }
    public void setEventLocation(String eventLocation) { this.eventLocation = eventLocation; }
    public void setStatus(String status) { this.status = status; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
