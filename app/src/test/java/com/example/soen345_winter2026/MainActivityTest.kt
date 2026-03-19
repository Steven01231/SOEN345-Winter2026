package com.example.soen345_winter2026

import android.content.Intent
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
class MainActivityTest {

    private lateinit var mockAuth: FirebaseAuth
    private lateinit var mockDb: FirebaseFirestore
    private val mockAuthTask = mockk<Task<AuthResult>>(relaxed = true)
    private val mockFirestoreTask = mockk<Task<DocumentReference>>(relaxed = true)

    @Before
    fun setup() {
        mockkStatic(FirebaseAuth::class)
        mockkStatic(FirebaseFirestore::class)

        mockAuth = mockk(relaxed = true)
        mockDb = mockk(relaxed = true)

        every { FirebaseAuth.getInstance() } returns mockAuth
        every { FirebaseFirestore.getInstance() } returns mockDb
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `onCreate should start LogInActivity and trigger Firebase success path`() {
        // 1. Setup Auth Mocks
        val mockUser = mockk<FirebaseUser> { every { uid } returns "test_uid" }
        val mockAuthResult = mockk<AuthResult> { every { user } returns mockUser }
        every { mockAuth.signInAnonymously() } returns mockAuthTask

        // 2. Setup Firestore Chain Mocks
        // We use DocumentReference for the chain, and a separate one for the 'result'
        val mockDocRef = mockk<DocumentReference>(relaxed = true)
        val mockColRef = mockk<CollectionReference>(relaxed = true)
        val mockResultDoc = mockk<DocumentReference> { every { id } returns "new_doc_123" }

        every { mockDb.collection("users") } returns mockColRef
        every { mockColRef.document("test_uid") } returns mockDocRef
        every { mockDocRef.collection("testData") } returns mockColRef
        every { mockColRef.add(any()) } returns mockFirestoreTask

        // 3. Build Activity and Trigger Lifecycle
        val controller = Robolectric.buildActivity(MainActivity::class.java)
        val activity = controller.get()
        controller.create()

        // Verify LogInActivity intent
        val actualIntent = shadowOf(activity).nextStartedActivity
        assertEquals(LogInActivity::class.java.name, actualIntent.component?.className)

        // 4. Trigger Auth Success
        val authSuccessSlot = slot<OnSuccessListener<AuthResult>>()
        verify { mockAuthTask.addOnSuccessListener(capture(authSuccessSlot)) }
        authSuccessSlot.captured.onSuccess(mockAuthResult)

        // 5. Trigger Firestore Success
        val firestoreSuccessSlot = slot<OnSuccessListener<DocumentReference>>()
        verify { mockFirestoreTask.addOnSuccessListener(capture(firestoreSuccessSlot)) }

        // Pass the mockResultDoc here so documentReference.id doesn't fail
        firestoreSuccessSlot.captured.onSuccess(mockResultDoc)

        // 6. Explicitly verify the calls that caused the previous error
        verify {
            mockDocRef.collection("testData") // From: .document(uid).collection("testData")
            mockResultDoc.id                  // From: Log.d(..., documentReference.id)
        }

        // Optional: Only keep these if you want strict checking
        confirmVerified(mockDocRef)
        confirmVerified(mockResultDoc)
    }

    @Test
    fun `testFirebaseConnection should handle Auth failure`() {
        // 1. Setup Task to support chaining
        every { mockAuthTask.addOnSuccessListener(any()) } returns mockAuthTask
        every { mockAuthTask.addOnFailureListener(any()) } returns mockAuthTask

        // 2. Setup Auth to return that task
        every { mockAuth.signInAnonymously() } returns mockAuthTask

        // 3. Trigger Activity Creation (calls testFirebaseConnection)
        val controller = Robolectric.buildActivity(MainActivity::class.java).create()

        // 4. Capture the failure listener
        val failureSlot = slot<OnFailureListener>()
        verify { mockAuthTask.addOnFailureListener(capture(failureSlot)) }

        // 5. Trigger the failure
        val mockException = Exception("Auth Failed")
        failureSlot.captured.onFailure(mockException)

        // Verification: Assert your error handling logic (e.g., a Toast or Log)
        // verify { ... }
    }

    @Test
    fun `writeTestData should handle Firestore failure`() {
        // 1. Prepare Auth Success
        val mockUser = mockk<FirebaseUser> { every { uid } returns "test_uid" }
        val mockAuthResult = mockk<AuthResult> { every { user } returns mockUser }
        every { mockAuth.signInAnonymously() } returns mockAuthTask

        // 2. Prepare the Firestore Task Mock explicitly
        val mockFirestoreTask = mockk<Task<DocumentReference>>()
        // We need to allow the task to return itself when listeners are added (Fluent API)
        every { mockFirestoreTask.addOnSuccessListener(any()) } returns mockFirestoreTask
        every { mockFirestoreTask.addOnFailureListener(any()) } returns mockFirestoreTask

        // 3. Mock the chain precisely as it appears in MainActivity
        val mockColRef = mockk<CollectionReference>(relaxed = true)
        val mockDocRef = mockk<DocumentReference>(relaxed = true)

        every { mockDb.collection("users") } returns mockColRef
        every { mockColRef.document("test_uid") } returns mockDocRef
        every { mockDocRef.collection("testData") } returns mockColRef
        // This is the critical call:
        every { mockColRef.add(any()) } returns mockFirestoreTask

        // 4. Start Activity
        val controller = Robolectric.buildActivity(MainActivity::class.java).create()

        // 5. Trigger Auth Success to reach writeTestDocument
        val authSuccessSlot = slot<OnSuccessListener<AuthResult>>()
        verify { mockAuthTask.addOnSuccessListener(capture(authSuccessSlot)) }
        authSuccessSlot.captured.onSuccess(mockAuthResult)

        // 6. NOW capture and trigger the Firestore Failure
        val firestoreFailureSlot = slot<OnFailureListener>()
        verify { mockFirestoreTask.addOnFailureListener(capture(firestoreFailureSlot)) }

        val mockException = Exception("Firestore Write Failed")
        firestoreFailureSlot.captured.onFailure(mockException)

        // Verification: If it reached here without crashing, the failure branch is covered
        verify { mockFirestoreTask.addOnFailureListener(any()) }
    }
}