package com.example.soen345_winter2026.events;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles all Firestore operations for events.
 * Callbacks are delivered on the main thread (Firestore default behaviour).
 */
public class EventRepository {

    public interface Callback {
        void onResult(List<Event> events, String error);
    }

    private final FirebaseFirestore db;

    public EventRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    // Constructor for testing / dependency injection
    public EventRepository(FirebaseFirestore db) {
        this.db = db;
    }

    public void fetchActiveEvents(Callback callback) {
        db.collection("events")
                .whereEqualTo("status", "active")
                .get()
                .addOnSuccessListener(result -> {
                    List<Event> events = new ArrayList<>();
                    for (DocumentSnapshot doc : result.getDocuments()) {
                        Event event = doc.toObject(Event.class);
                        if (event != null) {
                            event.setEventID(doc.getId());
                            events.add(event);
                        }
                    }
                    callback.onResult(events, null);
                })
                .addOnFailureListener(e ->
                        callback.onResult(new ArrayList<>(), e.getMessage())
                );
    }
}
