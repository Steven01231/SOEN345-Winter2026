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
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class EventRepositoryTest {

    private FirebaseFirestore mockDb;
    private CollectionReference mockCollection;
    private Query mockQuery;
    private Task<QuerySnapshot> mockTask;
    private EventRepository repository;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        mockDb = mock(FirebaseFirestore.class);
        mockCollection = mock(CollectionReference.class);
        mockQuery = mock(Query.class);
        mockTask = mock(Task.class);

        when(mockDb.collection("events")).thenReturn(mockCollection);
        when(mockCollection.whereEqualTo(eq("status"), eq("active"))).thenReturn(mockQuery);
        when(mockQuery.get()).thenReturn(mockTask);
        when(mockTask.addOnSuccessListener(any())).thenReturn(mockTask);
        when(mockTask.addOnFailureListener(any())).thenReturn(mockTask);

        repository = new EventRepository(mockDb);
    }

    @Test
    void fetchActiveEvents_queriesCorrectCollection() {
        repository.fetchActiveEvents((events, error) -> {});

        verify(mockDb).collection("events");
        verify(mockCollection).whereEqualTo("status", "active");
        verify(mockQuery).get();
    }

    @SuppressWarnings("unchecked")
    @Test
    void fetchActiveEvents_onSuccess_returnsEvents() {
        DocumentSnapshot doc1 = mock(DocumentSnapshot.class);
        when(doc1.getId()).thenReturn("doc1");
        when(doc1.getString("title")).thenReturn("Concert");
        when(doc1.getString("category")).thenReturn("Concert");
        when(doc1.getString("date")).thenReturn("June 1");
        when(doc1.getString("location")).thenReturn("Park");
        when(doc1.getString("description")).thenReturn("description");
        when(doc1.getString("imageUrl")).thenReturn("");
        when(doc1.getLong("availableSeats")).thenReturn(100L);
        when(doc1.getString("status")).thenReturn("active");

        DocumentSnapshot doc2 = mock(DocumentSnapshot.class);
        when(doc2.getId()).thenReturn("doc2");
        when(doc2.getString("title")).thenReturn("Movie Night");
        when(doc2.getString("category")).thenReturn("Movie");
        when(doc2.getString("date")).thenReturn("July 1");
        when(doc2.getString("location")).thenReturn("Cinema");
        when(doc2.getString("description")).thenReturn("description");
        when(doc2.getString("imageUrl")).thenReturn("");
        when(doc2.getLong("availableSeats")).thenReturn(50L);
        when(doc2.getString("status")).thenReturn("active");

        QuerySnapshot mockSnapshot = mock(QuerySnapshot.class);
        when(mockSnapshot.getDocuments()).thenReturn(Arrays.asList(doc1, doc2));

        ArgumentCaptor<OnSuccessListener<QuerySnapshot>> captor =
                ArgumentCaptor.forClass(OnSuccessListener.class);

        repository.fetchActiveEvents((events, error) -> {
            assertNull(error);
            assertEquals(2, events.size());
            assertEquals("doc1", events.get(0).getEventID());
            assertEquals("Concert", events.get(0).getTitle());
            assertEquals(EventStatus.ACTIVE, events.get(0).getStatus());
            assertEquals("doc2", events.get(1).getEventID());
            assertEquals("Movie Night", events.get(1).getTitle());
            assertEquals(EventStatus.ACTIVE, events.get(1).getStatus());
        });

        verify(mockTask).addOnSuccessListener(captor.capture());
        captor.getValue().onSuccess(mockSnapshot);
    }

    @SuppressWarnings("unchecked")
    @Test
    void fetchActiveEvents_onSuccess_parsesStatusCorrectly() {
        DocumentSnapshot doc = mock(DocumentSnapshot.class);
        when(doc.getId()).thenReturn("doc2");
        when(doc.getString("title")).thenReturn("Movie");
        when(doc.getString("category")).thenReturn("Movie");
        when(doc.getString("date")).thenReturn("July 1");
        when(doc.getString("location")).thenReturn("Cinema");
        when(doc.getString("description")).thenReturn("description");
        when(doc.getString("imageUrl")).thenReturn("");
        when(doc.getLong("availableSeats")).thenReturn(50L);
        when(doc.getString("status")).thenReturn("active");

        QuerySnapshot mockSnapshot = mock(QuerySnapshot.class);
        when(mockSnapshot.getDocuments()).thenReturn(Collections.singletonList(doc));

        ArgumentCaptor<OnSuccessListener<QuerySnapshot>> captor =
                ArgumentCaptor.forClass(OnSuccessListener.class);

        repository.fetchActiveEvents((events, error) -> {
            assertNull(error);
            assertEquals(1, events.size());
            assertEquals("doc2", events.get(0).getEventID());
            assertEquals(EventStatus.ACTIVE, events.get(0).getStatus());
        });

        verify(mockTask).addOnSuccessListener(captor.capture());
        captor.getValue().onSuccess(mockSnapshot);
    }

    @SuppressWarnings("unchecked")
    @Test
    void fetchActiveEvents_onSuccess_emptyCollection_returnsEmptyList() {
        QuerySnapshot mockSnapshot = mock(QuerySnapshot.class);
        when(mockSnapshot.getDocuments()).thenReturn(Collections.emptyList());

        ArgumentCaptor<OnSuccessListener<QuerySnapshot>> captor =
                ArgumentCaptor.forClass(OnSuccessListener.class);

        repository.fetchActiveEvents((events, error) -> {
            assertNull(error);
            assertTrue(events.isEmpty());
        });

        verify(mockTask).addOnSuccessListener(captor.capture());
        captor.getValue().onSuccess(mockSnapshot);
    }

    @SuppressWarnings("unchecked")
    @Test
    void fetchActiveEvents_onFailure_returnsError() {
        String errorMessage = "Network unavailable";

        ArgumentCaptor<OnFailureListener> captor =
                ArgumentCaptor.forClass(OnFailureListener.class);

        repository.fetchActiveEvents((events, error) -> {
            assertEquals(errorMessage, error);
            assertTrue(events.isEmpty());
        });

        verify(mockTask).addOnFailureListener(captor.capture());
        captor.getValue().onFailure(new Exception(errorMessage));
    }

    @SuppressWarnings("unchecked")
    @Test
    void fetchActiveEvents_onSuccess_setsDocumentId() {
        DocumentSnapshot doc = mock(DocumentSnapshot.class);
        when(doc.getId()).thenReturn("firestore-id-123");
        when(doc.getString("title")).thenReturn("Test");
        when(doc.getString("category")).thenReturn("Sports");
        when(doc.getString("date")).thenReturn("Jan 1");
        when(doc.getString("location")).thenReturn("Venue");
        when(doc.getString("description")).thenReturn("description");
        when(doc.getString("imageUrl")).thenReturn("");
        when(doc.getLong("availableSeats")).thenReturn(10L);
        when(doc.getString("status")).thenReturn("active");

        QuerySnapshot mockSnapshot = mock(QuerySnapshot.class);
        when(mockSnapshot.getDocuments()).thenReturn(Collections.singletonList(doc));

        ArgumentCaptor<OnSuccessListener<QuerySnapshot>> captor =
                ArgumentCaptor.forClass(OnSuccessListener.class);

        repository.fetchActiveEvents((events, error) -> {
            assertEquals(1, events.size());
            assertEquals("firestore-id-123", events.get(0).getEventID());
        });

        verify(mockTask).addOnSuccessListener(captor.capture());
        captor.getValue().onSuccess(mockSnapshot);
    }

    @SuppressWarnings("unchecked")
    @Test
    void fetchActiveEvents_onSuccess_parsesPastStatusCorrectly() {
        DocumentSnapshot doc = mock(DocumentSnapshot.class);
        when(doc.getId()).thenReturn("doc1");
        when(doc.getString("title")).thenReturn("Past Event");
        when(doc.getString("category")).thenReturn("Movie");
        when(doc.getString("date")).thenReturn("Jan 1");
        when(doc.getString("location")).thenReturn("Cinema");
        when(doc.getString("description")).thenReturn("desc");
        when(doc.getString("imageUrl")).thenReturn("");
        when(doc.getLong("availableSeats")).thenReturn(10L);
        when(doc.getString("status")).thenReturn("past");

        QuerySnapshot mockSnapshot = mock(QuerySnapshot.class);
        when(mockSnapshot.getDocuments()).thenReturn(Collections.singletonList(doc));

        ArgumentCaptor<OnSuccessListener<QuerySnapshot>> captor =
                ArgumentCaptor.forClass(OnSuccessListener.class);

        repository.fetchActiveEvents((events, error) -> {
            assertNull(error);
            assertEquals(1, events.size());
            assertEquals(EventStatus.PAST, events.get(0).getStatus());
        });

        verify(mockTask).addOnSuccessListener(captor.capture());
        captor.getValue().onSuccess(mockSnapshot);
    }

    @SuppressWarnings("unchecked")
    @Test
    void fetchActiveEvents_onSuccess_parsesCancelledStatusCorrectly() {
        DocumentSnapshot doc = mock(DocumentSnapshot.class);
        when(doc.getId()).thenReturn("doc2");
        when(doc.getString("title")).thenReturn("Cancelled Event");
        when(doc.getString("category")).thenReturn("Concert");
        when(doc.getString("date")).thenReturn("Feb 1");
        when(doc.getString("location")).thenReturn("Hall");
        when(doc.getString("description")).thenReturn("desc");
        when(doc.getString("imageUrl")).thenReturn("");
        when(doc.getLong("availableSeats")).thenReturn(0L);
        when(doc.getString("status")).thenReturn("cancelled");

        QuerySnapshot mockSnapshot = mock(QuerySnapshot.class);
        when(mockSnapshot.getDocuments()).thenReturn(Collections.singletonList(doc));

        ArgumentCaptor<OnSuccessListener<QuerySnapshot>> captor =
                ArgumentCaptor.forClass(OnSuccessListener.class);

        repository.fetchActiveEvents((events, error) -> {
            assertNull(error);
            assertEquals(1, events.size());
            assertEquals(EventStatus.CANCELLED, events.get(0).getStatus());
        });

        verify(mockTask).addOnSuccessListener(captor.capture());
        captor.getValue().onSuccess(mockSnapshot);
    }

    @SuppressWarnings("unchecked")
    @Test
    void fetchActiveEvents_onSuccess_nullStatus_returnsNullStatus() {
        DocumentSnapshot doc = mock(DocumentSnapshot.class);
        when(doc.getId()).thenReturn("doc3");
        when(doc.getString("title")).thenReturn("Unknown Status Event");
        when(doc.getString("category")).thenReturn("Travel");
        when(doc.getString("date")).thenReturn("Mar 1");
        when(doc.getString("location")).thenReturn("Tokyo");
        when(doc.getString("description")).thenReturn("desc");
        when(doc.getString("imageUrl")).thenReturn("");
        when(doc.getLong("availableSeats")).thenReturn(5L);
        when(doc.getString("status")).thenReturn(null);

        QuerySnapshot mockSnapshot = mock(QuerySnapshot.class);
        when(mockSnapshot.getDocuments()).thenReturn(Collections.singletonList(doc));

        ArgumentCaptor<OnSuccessListener<QuerySnapshot>> captor =
                ArgumentCaptor.forClass(OnSuccessListener.class);

        repository.fetchActiveEvents((events, error) -> {
            assertNull(error);
            assertEquals(1, events.size());
            assertNull(events.get(0).getStatus());
        });

        verify(mockTask).addOnSuccessListener(captor.capture());
        captor.getValue().onSuccess(mockSnapshot);
    }

    @SuppressWarnings("unchecked")
    @Test
    void fetchActiveEvents_onSuccess_unknownStatus_returnsNullStatus() {
        DocumentSnapshot doc = mock(DocumentSnapshot.class);
        when(doc.getId()).thenReturn("doc4");
        when(doc.getString("title")).thenReturn("Weird Event");
        when(doc.getString("category")).thenReturn("Sports");
        when(doc.getString("date")).thenReturn("Apr 1");
        when(doc.getString("location")).thenReturn("Stadium");
        when(doc.getString("description")).thenReturn("desc");
        when(doc.getString("imageUrl")).thenReturn("");
        when(doc.getLong("availableSeats")).thenReturn(20L);
        when(doc.getString("status")).thenReturn("weird");


        QuerySnapshot mockSnapshot = mock(QuerySnapshot.class);
        when(mockSnapshot.getDocuments()).thenReturn(Collections.singletonList(doc));

        ArgumentCaptor<OnSuccessListener<QuerySnapshot>> captor =
                ArgumentCaptor.forClass(OnSuccessListener.class);

        repository.fetchActiveEvents((events, error) -> {
            assertNull(error);
            assertEquals(1, events.size());
            assertNull(events.get(0).getStatus());
        });

        verify(mockTask).addOnSuccessListener(captor.capture());
        captor.getValue().onSuccess(mockSnapshot);
    }

    @SuppressWarnings("unchecked")
    @Test
    void fetchActiveEvents_onSuccess_parsesPriceCorrectly() {
        DocumentSnapshot doc = mock(DocumentSnapshot.class);
        when(doc.getId()).thenReturn("doc-price");
        when(doc.getString("title")).thenReturn("Japan Cherry Blossom Trip");
        when(doc.getString("category")).thenReturn("Travel");
        when(doc.getString("date")).thenReturn("2026-04-05");
        when(doc.getString("location")).thenReturn("Tokyo, Japan");
        when(doc.getString("description")).thenReturn("desc");
        when(doc.getString("imageUrl")).thenReturn("");
        when(doc.getLong("availableSeats")).thenReturn(25L);
        when(doc.getString("status")).thenReturn("active");
        when(doc.getDouble("price")).thenReturn(1200.0);

        QuerySnapshot mockSnapshot = mock(QuerySnapshot.class);
        when(mockSnapshot.getDocuments()).thenReturn(Collections.singletonList(doc));

        ArgumentCaptor<OnSuccessListener<QuerySnapshot>> captor =
                ArgumentCaptor.forClass(OnSuccessListener.class);

        repository.fetchActiveEvents((events, error) -> {
            assertNull(error);
            assertEquals(1, events.size());
            assertEquals(1200.0, events.get(0).getPrice(), 0.001);
        });

        verify(mockTask).addOnSuccessListener(captor.capture());
        captor.getValue().onSuccess(mockSnapshot);
    }

}