package com.example.soen345_winter2026.events;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReservationRepository {

    private static final String TAG = "ReservationRepository";
    private static final String RESERVATIONS = "reservations";
    private static final String EVENTS = "events";

    public interface BookingCallback {
        void onResult(boolean success, String error);
    }

    public interface ReservationsCallback {
        void onResult(List<Reservation> reservations, String error);
    }

    private final FirebaseFirestore db;

    public ReservationRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    public ReservationRepository(FirebaseFirestore db) {
        this.db = db;
    }

    /**
     * Books an event for a user.
     * Uses a Firestore transaction to atomically:
     * 1. Check for existing active reservation (prevents double booking)
     * 2. Verify seats are available
     * 3. Decrement availableSeats on the event
     * 4. Create the reservation document
     *
     * Reservation ID is deterministic: "{userId}_{eventId}" — allows a transactional read.
     */
    public void bookEvent(String userId, Event event, BookingCallback callback) {
        String reservationId = userId + "_" + event.getEventID();
        DocumentReference reservationRef = db.collection(RESERVATIONS).document(reservationId);
        DocumentReference eventRef = db.collection(EVENTS).document(event.getEventID());

        db.runTransaction(transaction -> {
            DocumentSnapshot reservationSnap = transaction.get(reservationRef);
            DocumentSnapshot eventSnap = transaction.get(eventRef);

            String existingStatus = reservationSnap.getString("status");
            if (reservationSnap.exists() && existingStatus != null && existingStatus.equalsIgnoreCase("active")) {
                throw new FirebaseFirestoreException(
                        "You have already booked this event.",
                        FirebaseFirestoreException.Code.ALREADY_EXISTS
                );
            }

            Long seats = eventSnap.getLong("availableSeats");
            if (seats == null || seats <= 0) {
                throw new FirebaseFirestoreException(
                        "This event is sold out.",
                        FirebaseFirestoreException.Code.FAILED_PRECONDITION
                );
            }

            transaction.update(eventRef, "availableSeats", seats - 1);

            Map<String, Object> data = new HashMap<>();
            data.put("reservationId", reservationId);
            data.put("userId", userId);
            data.put("eventId", event.getEventID());
            data.put("eventTitle", event.getTitle());
            data.put("eventDate", event.getDate());
            data.put("eventLocation", event.getLocation());
            data.put("status", "active");
            data.put("createdAt", System.currentTimeMillis());
            transaction.set(reservationRef, data);

            return null;
        })
        .addOnSuccessListener(result -> callback.onResult(true, null))
        .addOnFailureListener(e -> {
            Log.e(TAG, "bookEvent failed", e);
            callback.onResult(false, e.getMessage());
        });
    }

    /**
     * Cancels a reservation and restores the seat to the event.
     */
    public void cancelReservation(String reservationId, String eventId, BookingCallback callback) {
        DocumentReference reservationRef = db.collection(RESERVATIONS).document(reservationId);
        DocumentReference eventRef = db.collection(EVENTS).document(eventId);

        db.runTransaction(transaction -> {
            DocumentSnapshot reservationSnap = transaction.get(reservationRef);

            String cancelStatus = reservationSnap.getString("status");
            if (!reservationSnap.exists() || cancelStatus == null || !cancelStatus.equalsIgnoreCase("active")) {
                throw new FirebaseFirestoreException(
                        "Reservation is not active.",
                        FirebaseFirestoreException.Code.NOT_FOUND
                );
            }

            DocumentSnapshot eventSnap = transaction.get(eventRef);
            Long seats = eventSnap.getLong("availableSeats");
            long restored = (seats != null ? seats : 0) + 1;

            transaction.update(reservationRef, "status", "cancelled");
            transaction.update(eventRef, "availableSeats", restored);

            return null;
        })
        .addOnSuccessListener(result -> callback.onResult(true, null))
        .addOnFailureListener(e -> {
            Log.e(TAG, "cancelReservation failed", e);
            callback.onResult(false, e.getMessage());
        });
    }

    /**
     * Fetches all reservations for a given user.
     */
    public void getUserReservations(String userId, ReservationsCallback callback) {
        db.collection(RESERVATIONS)
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(result -> {
                    List<Reservation> reservations = new ArrayList<>();
                    for (DocumentSnapshot doc : result.getDocuments()) {
                        try {
                            Reservation r = new Reservation();
                            r.setReservationId(doc.getId());
                            r.setUserId(doc.getString("userId"));
                            r.setEventId(doc.getString("eventId"));
                            r.setEventTitle(doc.getString("eventTitle"));
                            r.setEventDate(doc.getString("eventDate"));
                            r.setEventLocation(doc.getString("eventLocation"));
                            r.setStatus(doc.getString("status"));
                            Long createdAt = doc.getLong("createdAt");
                            r.setCreatedAt(createdAt != null ? createdAt : 0L);
                            reservations.add(r);
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing reservation " + doc.getId(), e);
                        }
                    }
                    callback.onResult(reservations, null);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "getUserReservations failed", e);
                    callback.onResult(new ArrayList<>(), e.getMessage());
                });
    }

    /**
     * Cancels all active reservations for an event.
     * Called when an admin cancels an event.
     */
    public void cancelAllReservationsForEvent(String eventId, BookingCallback callback) {
        db.collection(RESERVATIONS)
                .whereEqualTo("eventId", eventId)
                .whereEqualTo("status", "active")
                .get()
                .addOnSuccessListener(result -> {
                    if (result.isEmpty()) {
                        callback.onResult(true, null);
                        return;
                    }
                    WriteBatch batch = db.batch();
                    for (DocumentSnapshot doc : result.getDocuments()) {
                        batch.update(doc.getReference(), "status", "cancelled");
                    }
                    batch.commit()
                            .addOnSuccessListener(v -> callback.onResult(true, null))
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "cancelAllReservationsForEvent batch failed", e);
                                callback.onResult(false, e.getMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "cancelAllReservationsForEvent query failed", e);
                    callback.onResult(false, e.getMessage());
                });
    }
}
