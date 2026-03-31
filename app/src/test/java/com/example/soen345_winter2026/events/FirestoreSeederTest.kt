package com.example.soen345_winter2026.events

import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull

class FirestoreSeederTest {

    private lateinit var mockDb: FirebaseFirestore
    private lateinit var mockCollection: CollectionReference
    private lateinit var mockQuery: Query
    private lateinit var mockCheckTask: Task<QuerySnapshot>

    @Suppress("UNCHECKED_CAST")
    @BeforeEach
    fun setUp() {
        mockDb = mock(FirebaseFirestore::class.java)
        mockCollection = mock(CollectionReference::class.java)
        mockQuery = mock(Query::class.java)
        mockCheckTask = mock(Task::class.java) as Task<QuerySnapshot>

        `when`(mockDb.collection("events")).thenReturn(mockCollection)
        `when`(mockCollection.limit(1)).thenReturn(mockQuery)
        `when`(mockQuery.get()).thenReturn(mockCheckTask)
        `when`(mockCheckTask.addOnSuccessListener(any<OnSuccessListener<QuerySnapshot>>())).thenReturn(mockCheckTask)
        `when`(mockCheckTask.addOnFailureListener(any<OnFailureListener>())).thenReturn(mockCheckTask)
    }

    @Test
    fun seedIfEmpty_queriesEventsCollectionWithLimit1() {
        FirestoreSeeder.seedIfEmpty(mockDb) {}

        verify(mockDb).collection("events")
        verify(mockCollection).limit(1)
        verify(mockQuery).get()
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun seedIfEmpty_collectionNotEmpty_callsOnReadyWithoutSeeding() {
        val mockSnapshot = mock(QuerySnapshot::class.java)
        `when`(mockSnapshot.isEmpty).thenReturn(false)

        var readyCalled = false
        FirestoreSeeder.seedIfEmpty(mockDb) { readyCalled = true }

        val captor = ArgumentCaptor.forClass(OnSuccessListener::class.java)
                as ArgumentCaptor<OnSuccessListener<QuerySnapshot>>
        verify(mockCheckTask).addOnSuccessListener(captor.capture())
        captor.value.onSuccess(mockSnapshot)

        assertTrue(readyCalled)
        verify(mockCollection, never()).add(any<Map<String, Any>>())
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun seedIfEmpty_collectionEmpty_seedsAllEvents() {
        val mockSnapshot = mock(QuerySnapshot::class.java)
        `when`(mockSnapshot.isEmpty).thenReturn(true)

        val mockAddTask = mock(Task::class.java) as Task<DocumentReference>
        `when`(mockCollection.add(any<Map<String, Any>>())).thenReturn(mockAddTask)
        `when`(mockAddTask.addOnSuccessListener(any<OnSuccessListener<DocumentReference>>())).thenReturn(mockAddTask)
        `when`(mockAddTask.addOnCompleteListener(any<OnCompleteListener<DocumentReference>>())).thenReturn(mockAddTask)

        FirestoreSeeder.seedIfEmpty(mockDb) {}

        val captor = ArgumentCaptor.forClass(OnSuccessListener::class.java)
                as ArgumentCaptor<OnSuccessListener<QuerySnapshot>>
        verify(mockCheckTask).addOnSuccessListener(captor.capture())
        captor.value.onSuccess(mockSnapshot)

        verify(mockCollection, times(8)).add(any<Map<String, Any>>())
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun seedIfEmpty_collectionEmpty_callsOnReadyAfterAllSeedsComplete() {
        val mockSnapshot = mock(QuerySnapshot::class.java)
        `when`(mockSnapshot.isEmpty).thenReturn(true)

        val mockAddTask = mock(Task::class.java) as Task<DocumentReference>
        `when`(mockCollection.add(any<Map<String, Any>>())).thenReturn(mockAddTask)
        `when`(mockAddTask.addOnSuccessListener(any<OnSuccessListener<DocumentReference>>())).thenReturn(mockAddTask)
        `when`(mockAddTask.addOnCompleteListener(any<OnCompleteListener<DocumentReference>>())).thenReturn(mockAddTask)

        var readyCalled = false
        FirestoreSeeder.seedIfEmpty(mockDb) { readyCalled = true }

        // Trigger check success
        val checkCaptor = ArgumentCaptor.forClass(OnSuccessListener::class.java)
                as ArgumentCaptor<OnSuccessListener<QuerySnapshot>>
        verify(mockCheckTask).addOnSuccessListener(checkCaptor.capture())
        checkCaptor.value.onSuccess(mockSnapshot)

        // Capture all onComplete listeners
        val completeCaptor = ArgumentCaptor.forClass(OnCompleteListener::class.java)
                as ArgumentCaptor<OnCompleteListener<DocumentReference>>
        verify(mockAddTask, times(8)).addOnCompleteListener(completeCaptor.capture())

        // Fire first 7 — onReady should NOT be called yet
        for (i in 0 until 7) {
            completeCaptor.allValues[i].onComplete(mockAddTask)
        }
        assertFalse(readyCalled)

        // Fire the 8th — now onReady should be called
        completeCaptor.allValues[7].onComplete(mockAddTask)
        assertTrue(readyCalled)
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun seedIfEmpty_checkFails_callsOnReady() {
        val error = Exception("Network error")

        var readyCalled = false
        FirestoreSeeder.seedIfEmpty(mockDb) { readyCalled = true }

        val captor = ArgumentCaptor.forClass(OnFailureListener::class.java)
        verify(mockCheckTask).addOnFailureListener(captor.capture())
        captor.value.onFailure(error)

        assertTrue(readyCalled)
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun seedIfEmpty_collectionEmpty_eachEventHasRequiredFields() {
        val mockSnapshot = mock(QuerySnapshot::class.java)
        `when`(mockSnapshot.isEmpty).thenReturn(true)

        val mockAddTask = mock(Task::class.java) as Task<DocumentReference>
        `when`(mockCollection.add(any<Map<String, Any>>())).thenReturn(mockAddTask)
        `when`(mockAddTask.addOnSuccessListener(any<OnSuccessListener<DocumentReference>>())).thenReturn(mockAddTask)
        `when`(mockAddTask.addOnCompleteListener(any<OnCompleteListener<DocumentReference>>())).thenReturn(mockAddTask)

        FirestoreSeeder.seedIfEmpty(mockDb) {}

        val checkCaptor = ArgumentCaptor.forClass(OnSuccessListener::class.java)
                as ArgumentCaptor<OnSuccessListener<QuerySnapshot>>
        verify(mockCheckTask).addOnSuccessListener(checkCaptor.capture())
        checkCaptor.value.onSuccess(mockSnapshot)

        val eventCaptor = ArgumentCaptor.forClass(Map::class.java)
                as ArgumentCaptor<Map<String, Any>>
        verify(mockCollection, times(8)).add(eventCaptor.capture())

        for (event in eventCaptor.allValues) {
            assertTrue(event.containsKey("title"), "Event missing title")
            assertTrue(event.containsKey("category"), "Event missing category")
            assertTrue(event.containsKey("date"), "Event missing date")
            assertTrue(event.containsKey("location"), "Event missing location")
            assertTrue(event.containsKey("availableSeats"), "Event missing availableSeats")
            assertTrue(event.containsKey("status"), "Event missing status")
            assertEquals("active", event["status"])
        }
    }
}