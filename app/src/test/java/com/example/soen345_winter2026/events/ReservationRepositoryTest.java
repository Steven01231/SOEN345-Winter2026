package com.example.soen345_winter2026.events;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ReservationRepositoryTest {

    private FirebaseFirestore mockDb;
    private CollectionReference mockCollection;
    private Query mockQuery;
    private Task<QuerySnapshot> mockQueryTask;
    private ReservationRepository repository;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        mockDb = mock(FirebaseFirestore.class);
        mockCollection = mock(CollectionReference.class);
        mockQuery = mock(Query.class);
        mockQueryTask = mock(Task.class);

        when(mockDb.collection("reservations")).thenReturn(mockCollection);
        when(mockQueryTask.addOnSuccessListener(any())).thenReturn(mockQueryTask);
        when(mockQueryTask.addOnFailureListener(any())).thenReturn(mockQueryTask);

        repository = new ReservationRepository(mockDb);
    }

    @Nested
    @DisplayName("getUserReservations")
    class GetUserReservations {

        @SuppressWarnings("unchecked")
        @Test
        @DisplayName("should query reservations collection by userId")
        void queriesByUserId() {
            when(mockCollection.whereEqualTo(eq("userId"), eq("user1"))).thenReturn(mockQuery);
            when(mockQuery.get()).thenReturn(mockQueryTask);

            repository.getUserReservations("user1", (reservations, error) -> {});

            verify(mockCollection).whereEqualTo("userId", "user1");
            verify(mockQuery).get();
        }

        @SuppressWarnings("unchecked")
        @Test
        @DisplayName("should return parsed reservations on success")
        void returnsParsedReservationsOnSuccess() {
            DocumentSnapshot doc = mock(DocumentSnapshot.class);
            when(doc.getId()).thenReturn("user1_event1");
            when(doc.getString("userId")).thenReturn("user1");
            when(doc.getString("eventId")).thenReturn("event1");
            when(doc.getString("eventTitle")).thenReturn("Jazz Night");
            when(doc.getString("eventDate")).thenReturn("June 10");
            when(doc.getString("eventLocation")).thenReturn("Montreal");
            when(doc.getString("status")).thenReturn("active");
            when(doc.getLong("createdAt")).thenReturn(1000L);

            QuerySnapshot snapshot = mock(QuerySnapshot.class);
            when(snapshot.getDocuments()).thenReturn(Collections.singletonList(doc));

            when(mockCollection.whereEqualTo(eq("userId"), eq("user1"))).thenReturn(mockQuery);
            when(mockQuery.get()).thenReturn(mockQueryTask);

            ArgumentCaptor<OnSuccessListener<QuerySnapshot>> captor =
                    ArgumentCaptor.forClass(OnSuccessListener.class);

            AtomicReference<List<Reservation>> result = new AtomicReference<>();
            repository.getUserReservations("user1", (reservations, error) -> {
                result.set(reservations);
                assertNull(error);
            });

            verify(mockQueryTask).addOnSuccessListener(captor.capture());
            captor.getValue().onSuccess(snapshot);

            assertNotNull(result.get());
            assertEquals(1, result.get().size());
            assertEquals("Jazz Night", result.get().get(0).getEventTitle());
            assertEquals("active", result.get().get(0).getStatus());
            assertTrue(result.get().get(0).isActive());
        }

        @SuppressWarnings("unchecked")
        @Test
        @DisplayName("should return empty list when no reservations exist")
        void returnsEmptyListWhenNoReservations() {
            QuerySnapshot snapshot = mock(QuerySnapshot.class);
            when(snapshot.getDocuments()).thenReturn(Collections.emptyList());

            when(mockCollection.whereEqualTo(eq("userId"), eq("user1"))).thenReturn(mockQuery);
            when(mockQuery.get()).thenReturn(mockQueryTask);

            ArgumentCaptor<OnSuccessListener<QuerySnapshot>> captor =
                    ArgumentCaptor.forClass(OnSuccessListener.class);

            AtomicBoolean called = new AtomicBoolean(false);
            repository.getUserReservations("user1", (reservations, error) -> {
                called.set(true);
                assertTrue(reservations.isEmpty());
                assertNull(error);
            });

            verify(mockQueryTask).addOnSuccessListener(captor.capture());
            captor.getValue().onSuccess(snapshot);
            assertTrue(called.get());
        }

        @SuppressWarnings("unchecked")
        @Test
        @DisplayName("should return error message on failure")
        void returnsErrorOnFailure() {
            when(mockCollection.whereEqualTo(eq("userId"), eq("user1"))).thenReturn(mockQuery);
            when(mockQuery.get()).thenReturn(mockQueryTask);

            ArgumentCaptor<OnFailureListener> captor =
                    ArgumentCaptor.forClass(OnFailureListener.class);

            AtomicReference<String> errorResult = new AtomicReference<>();
            repository.getUserReservations("user1", (reservations, error) -> {
                errorResult.set(error);
                assertTrue(reservations.isEmpty());
            });

            verify(mockQueryTask).addOnFailureListener(captor.capture());
            captor.getValue().onFailure(new Exception("Network error"));

            assertEquals("Network error", errorResult.get());
        }

        @SuppressWarnings("unchecked")
        @Test
        @DisplayName("should correctly identify cancelled reservations as not active")
        void identifiesCancelledReservations() {
            DocumentSnapshot doc = mock(DocumentSnapshot.class);
            when(doc.getId()).thenReturn("user1_event2");
            when(doc.getString("userId")).thenReturn("user1");
            when(doc.getString("eventId")).thenReturn("event2");
            when(doc.getString("eventTitle")).thenReturn("Cancelled Show");
            when(doc.getString("eventDate")).thenReturn("July 1");
            when(doc.getString("eventLocation")).thenReturn("Montreal");
            when(doc.getString("status")).thenReturn("cancelled");
            when(doc.getLong("createdAt")).thenReturn(2000L);

            QuerySnapshot snapshot = mock(QuerySnapshot.class);
            when(snapshot.getDocuments()).thenReturn(Collections.singletonList(doc));

            when(mockCollection.whereEqualTo(eq("userId"), eq("user1"))).thenReturn(mockQuery);
            when(mockQuery.get()).thenReturn(mockQueryTask);

            ArgumentCaptor<OnSuccessListener<QuerySnapshot>> captor =
                    ArgumentCaptor.forClass(OnSuccessListener.class);

            AtomicReference<List<Reservation>> result = new AtomicReference<>();
            repository.getUserReservations("user1", (reservations, error) -> result.set(reservations));

            verify(mockQueryTask).addOnSuccessListener(captor.capture());
            captor.getValue().onSuccess(snapshot);

            assertFalse(result.get().get(0).isActive());
            assertEquals("cancelled", result.get().get(0).getStatus());
        }
    }

    @Nested
    @DisplayName("cancelAllReservationsForEvent")
    class CancelAllReservationsForEvent {

        @SuppressWarnings("unchecked")
        @Test
        @DisplayName("should query active reservations by eventId")
        void queriesActiveReservationsByEventId() {
            Query mockQuery2 = mock(Query.class);
            when(mockCollection.whereEqualTo(eq("eventId"), eq("event1"))).thenReturn(mockQuery);
            when(mockQuery.whereEqualTo(eq("status"), eq("active"))).thenReturn(mockQuery2);
            when(mockQuery2.get()).thenReturn(mockQueryTask);

            repository.cancelAllReservationsForEvent("event1", (success, error) -> {});

            verify(mockCollection).whereEqualTo("eventId", "event1");
            verify(mockQuery).whereEqualTo("status", "active");
        }

        @SuppressWarnings("unchecked")
        @Test
        @DisplayName("should succeed immediately when no active reservations exist")
        void succeedsImmediatelyWhenNoActiveReservations() {
            Query mockQuery2 = mock(Query.class);
            when(mockCollection.whereEqualTo(eq("eventId"), eq("event1"))).thenReturn(mockQuery);
            when(mockQuery.whereEqualTo(eq("status"), eq("active"))).thenReturn(mockQuery2);
            when(mockQuery2.get()).thenReturn(mockQueryTask);

            QuerySnapshot emptySnapshot = mock(QuerySnapshot.class);
            when(emptySnapshot.isEmpty()).thenReturn(true);
            when(emptySnapshot.getDocuments()).thenReturn(Collections.emptyList());

            ArgumentCaptor<OnSuccessListener<QuerySnapshot>> captor =
                    ArgumentCaptor.forClass(OnSuccessListener.class);

            AtomicBoolean successCalled = new AtomicBoolean(false);
            repository.cancelAllReservationsForEvent("event1", (success, error) -> {
                successCalled.set(true);
                assertTrue(success);
                assertNull(error);
            });

            verify(mockQueryTask).addOnSuccessListener(captor.capture());
            captor.getValue().onSuccess(emptySnapshot);
            assertTrue(successCalled.get());
        }

        @SuppressWarnings("unchecked")
        @Test
        @DisplayName("should return error on query failure")
        void returnsErrorOnQueryFailure() {
            Query mockQuery2 = mock(Query.class);
            when(mockCollection.whereEqualTo(eq("eventId"), eq("event1"))).thenReturn(mockQuery);
            when(mockQuery.whereEqualTo(eq("status"), eq("active"))).thenReturn(mockQuery2);
            when(mockQuery2.get()).thenReturn(mockQueryTask);

            ArgumentCaptor<OnFailureListener> captor =
                    ArgumentCaptor.forClass(OnFailureListener.class);

            AtomicReference<String> errorResult = new AtomicReference<>();
            repository.cancelAllReservationsForEvent("event1", (success, error) -> {
                assertFalse(success);
                errorResult.set(error);
            });

            verify(mockQueryTask).addOnFailureListener(captor.capture());
            captor.getValue().onFailure(new Exception("Query failed"));

            assertEquals("Query failed", errorResult.get());
        }
    }

    @Nested
    @DisplayName("Reservation model")
    class ReservationModelTests {

        @Test
        @DisplayName("isActive should return true for active status")
        void isActiveReturnsTrueForActiveStatus() {
            Reservation r = new Reservation("id", "user1", "event1",
                    "Jazz Night", "June 10", "Montreal", "active", 1000L);
            assertTrue(r.isActive());
        }

        @Test
        @DisplayName("isActive should return false for cancelled status")
        void isActiveReturnsFalseForCancelledStatus() {
            Reservation r = new Reservation("id", "user1", "event1",
                    "Jazz Night", "June 10", "Montreal", "cancelled", 1000L);
            assertFalse(r.isActive());
        }

        @Test
        @DisplayName("default constructor should create reservation with null fields")
        void defaultConstructorCreatesNullFields() {
            Reservation r = new Reservation();
            assertNull(r.getReservationId());
            assertNull(r.getStatus());
        }

        @Test
        @DisplayName("setters should update all fields correctly")
        void settersUpdateAllFields() {
            Reservation r = new Reservation();
            r.setReservationId("res1");
            r.setUserId("user1");
            r.setEventId("event1");
            r.setEventTitle("Concert");
            r.setEventDate("June 1");
            r.setEventLocation("Montreal");
            r.setStatus("active");
            r.setCreatedAt(999L);

            assertEquals("res1", r.getReservationId());
            assertEquals("user1", r.getUserId());
            assertEquals("event1", r.getEventId());
            assertEquals("Concert", r.getEventTitle());
            assertEquals("June 1", r.getEventDate());
            assertEquals("Montreal", r.getEventLocation());
            assertEquals("active", r.getStatus());
            assertEquals(999L, r.getCreatedAt());
        }
    }
}
