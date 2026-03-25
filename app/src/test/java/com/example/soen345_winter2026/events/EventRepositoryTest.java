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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
        Event event1 = new Event("", "Concert", "Concert", "June 1", "Park", 100, "description", EventStatus.ACTIVE, null, null, "");
        when(doc1.toObject(Event.class)).thenReturn(event1);
        when(doc1.getId()).thenReturn("doc1");

        DocumentSnapshot doc2 = mock(DocumentSnapshot.class);
        Event event2 = new Event("", "Movie Night", "Movie", "July 1", "Cinema", 50, "description", EventStatus.ACTIVE, null, null, "");
        when(doc2.toObject(Event.class)).thenReturn(event2);
        when(doc2.getId()).thenReturn("doc2");

        QuerySnapshot mockSnapshot = mock(QuerySnapshot.class);
        when(mockSnapshot.getDocuments()).thenReturn(Arrays.asList(doc1, doc2));

        ArgumentCaptor<OnSuccessListener<QuerySnapshot>> captor =
                ArgumentCaptor.forClass(OnSuccessListener.class);

        repository.fetchActiveEvents((events, error) -> {
            assertNull(error);
            assertEquals(2, events.size());
            assertEquals("doc1", events.get(0).getEventID());
            assertEquals("Concert", events.get(0).getTitle());
            assertEquals("doc2", events.get(1).getEventID());
            assertEquals("Movie Night", events.get(1).getTitle());
        });

        verify(mockTask).addOnSuccessListener(captor.capture());
        captor.getValue().onSuccess(mockSnapshot);
    }

    @SuppressWarnings("unchecked")
    @Test
    void fetchActiveEvents_onSuccess_skipsNullDocuments() {
        DocumentSnapshot doc1 = mock(DocumentSnapshot.class);
        when(doc1.toObject(Event.class)).thenReturn(null);
        when(doc1.getId()).thenReturn("doc1");

        DocumentSnapshot doc2 = mock(DocumentSnapshot.class);
        Event event2 = new Event("", "Movie", "Movie", "July 1", "Cinema", 50, "description", EventStatus.ACTIVE, null, null, "");
        when(doc2.toObject(Event.class)).thenReturn(event2);
        when(doc2.getId()).thenReturn("doc2");

        QuerySnapshot mockSnapshot = mock(QuerySnapshot.class);
        when(mockSnapshot.getDocuments()).thenReturn(Arrays.asList(doc1, doc2));

        ArgumentCaptor<OnSuccessListener<QuerySnapshot>> captor =
                ArgumentCaptor.forClass(OnSuccessListener.class);

        repository.fetchActiveEvents((events, error) -> {
            assertNull(error);
            assertEquals(1, events.size());
            assertEquals("doc2", events.get(0).getEventID());
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
        Event event = new Event("", "Test", "Sports", "Jan 1", "Venue", 10, "description", EventStatus.ACTIVE, null, null, "");
        when(doc.toObject(Event.class)).thenReturn(event);
        when(doc.getId()).thenReturn("firestore-id-123");

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
}
