package com.example.soen345_winter2026.events;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class EventRepository {

    public interface Callback {
        void onResult(List<Event> events, String error);
    }

    private final FirebaseFirestore db;

    public EventRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    public EventRepository(FirebaseFirestore db) {
        this.db = db;
    }

    public void fetchActiveEvents(Callback callback) {
        db.collection("events")
                .whereEqualTo("status", "active")
                .get()
                .addOnSuccessListener(result -> {
                    List<Event> events = new ArrayList<>();
                    Log.d("EventRepository", "Documents found: " + result.getDocuments().size());

                    for (DocumentSnapshot doc : result.getDocuments()) {
                        try {
                            Event event = new Event();
                            event.setEventID(doc.getId());
                            event.setTitle(doc.getString("title"));
                            event.setCategory(doc.getString("category"));
                            event.setDate(doc.getString("date"));
                            event.setLocation(doc.getString("location"));
                            event.setDescription(doc.getString("description"));
                            event.setImageUrl(doc.getString("imageUrl"));

                            Long seats = doc.getLong("availableSeats");
                            event.setAvailableSeats(seats != null ? seats.intValue() : 0);

                            String statusString = doc.getString("status");
                            event.setStatus(parseStatus(statusString));

                            events.add(event);
                        } catch (Exception e) {
                            Log.e("EventRepository", "Error parsing document " + doc.getId(), e);
                        }
                    }

                    callback.onResult(events, null);
                })
                .addOnFailureListener(e ->
                        callback.onResult(new ArrayList<>(), e.getMessage())
                );
    }

    private EventStatus parseStatus(String status) {
        if (status == null) return null;

        switch (status.toLowerCase()) {
            case "active":
                return EventStatus.ACTIVE;
            case "past":
                return EventStatus.PAST;
            case "cancelled":
                return EventStatus.CANCELLED;
            default:
                return null;
        }
    }
}