package com.example.soen345_winter2026.database

import com.google.android.gms.tasks.OnCompleteListener
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
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RegistrationDBTest {

    private lateinit var registrationDB: RegistrationDB
    private lateinit var mockAuth: FirebaseAuth
    private lateinit var mockDb: FirebaseFirestore
    private lateinit var mockUser: FirebaseUser
    private lateinit var mockTaskAuth: Task<AuthResult>
    private lateinit var mockTaskVoid: Task<Void>

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        // Mock Firebase Singletons
        mockkStatic(FirebaseAuth::class)
        mockkStatic(FirebaseFirestore::class)

        mockAuth = mockk(relaxed = true)
        mockDb = mockk(relaxed = true)
        mockUser = mockk(relaxed = true)
        mockTaskAuth = mockk(relaxed = true)
        mockTaskVoid = mockk(relaxed = true)

        every { FirebaseAuth.getInstance() } returns mockAuth
        every { FirebaseFirestore.getInstance() } returns mockDb

        registrationDB = RegistrationDB()
    }

    @Test
    fun `signUp - success when auth and firestore succeed`() {
        val email = "test@test.com"
        val pass = "password123"
        val name = "John Doe"
        val uid = "user_123"

        // 1. Mock Auth Creation
        every { mockAuth.createUserWithEmailAndPassword(email, pass) } returns mockTaskAuth
        every { mockTaskAuth.isSuccessful } returns true
        every { mockAuth.currentUser } returns mockUser
        every { mockUser.uid } returns uid

        // 2. Mock Firestore Storage
        val mockDocRef = mockk<DocumentReference>(relaxed = true)
        val mockColRef = mockk<CollectionReference>(relaxed = true)
        every { mockDb.collection("users") } returns mockColRef
        every { mockColRef.document(uid) } returns mockDocRef
        every { mockDocRef.set(any()) } returns mockTaskVoid

        // Capture the Auth Listener
        val authSlot = slot<OnCompleteListener<AuthResult>>()
        every { mockTaskAuth.addOnCompleteListener(capture(authSlot)) } returns mockTaskAuth

        // Capture the Firestore Success Listener
        val firestoreSlot = slot<OnSuccessListener<Void>>()
        every { mockTaskVoid.addOnSuccessListener(capture(firestoreSlot)) } returns mockTaskVoid

        var resultSuccess = false
        registrationDB.signUp(email, pass, name) { success, _ ->
            resultSuccess = success
        }

        // Trigger callbacks manually
        authSlot.captured.onComplete(mockTaskAuth)
        firestoreSlot.captured.onSuccess(null)

        assertTrue(resultSuccess)
    }

    @Test
    fun `signUp - failure when auth fails`() {
        val errorMsg = "Email already exists"
        every { mockAuth.createUserWithEmailAndPassword(any(), any()) } returns mockTaskAuth
        every { mockTaskAuth.isSuccessful } returns false
        every { mockTaskAuth.exception?.message } returns errorMsg

        val authSlot = slot<OnCompleteListener<AuthResult>>()
        every { mockTaskAuth.addOnCompleteListener(capture(authSlot)) } returns mockTaskAuth

        registrationDB.signUp("a@b.com", "123", "Name") { success, msg ->
            assertFalse(success)
            assertEquals(errorMsg, msg)
        }

        authSlot.captured.onComplete(mockTaskAuth)
    }

    @Test
    fun `logIn - success when credentials correct`() {
        every { mockAuth.signInWithEmailAndPassword(any(), any()) } returns mockTaskAuth
        every { mockTaskAuth.isSuccessful } returns true

        val slot = slot<OnCompleteListener<AuthResult>>()
        every { mockTaskAuth.addOnCompleteListener(capture(slot)) } returns mockTaskAuth

        registrationDB.logIn("a@b.com", "123") { success, _ ->
            assertTrue(success)
        }

        slot.captured.onComplete(mockTaskAuth)
    }

    @Test
    fun `logIn - failure returns error message`() {
        val errorMsg = "Invalid password"
        every { mockAuth.signInWithEmailAndPassword(any(), any()) } returns mockTaskAuth
        every { mockTaskAuth.isSuccessful } returns false
        every { mockTaskAuth.exception?.message } returns errorMsg

        val slot = slot<OnCompleteListener<AuthResult>>()
        every { mockTaskAuth.addOnCompleteListener(capture(slot)) } returns mockTaskAuth

        registrationDB.logIn("a@b.com", "wrong") { success, msg ->
            assertFalse(success)
            assertEquals(errorMsg, msg)
        }

        slot.captured.onComplete(mockTaskAuth)
    }

    @Test
    fun `signUp - failure when firestore fails after auth succeeds`() {
        val email = "test@test.com"
        val pass = "password123"
        val name = "John Doe"
        val uid = "user_123"
        val errorMsg = "Permission Denied"

        // 1. Mock Auth to SUCCEED
        every { mockAuth.createUserWithEmailAndPassword(email, pass) } returns mockTaskAuth
        every { mockTaskAuth.isSuccessful } returns true
        every { mockAuth.currentUser } returns mockUser
        every { mockUser.uid } returns uid

        // 2. Mock Firestore to exist but eventually FAIL
        val mockDocRef = mockk<DocumentReference>(relaxed = true)
        val mockColRef = mockk<CollectionReference>(relaxed = true)
        every { mockDb.collection("users") } returns mockColRef
        every { mockColRef.document(uid) } returns mockDocRef
        every { mockDocRef.set(any()) } returns mockTaskVoid

        // 3. Capture Listeners
        val authSlot = slot<OnCompleteListener<AuthResult>>()
        val failureSlot = slot<OnFailureListener>()

        every { mockTaskAuth.addOnCompleteListener(capture(authSlot)) } returns mockTaskAuth
        every { mockTaskVoid.addOnSuccessListener(any()) } returns mockTaskVoid
        every { mockTaskVoid.addOnFailureListener(capture(failureSlot)) } returns mockTaskVoid

        // 4. Execution
        var resultSuccess: Boolean? = null
        var resultMsg: String? = null

        registrationDB.signUp(email, pass, name) { success, msg ->
            resultSuccess = success
            resultMsg = msg
        }

        // Trigger the Auth success first
        authSlot.captured.onComplete(mockTaskAuth)

        // Trigger the Firestore FAILURE specifically
        val mockException = Exception(errorMsg)
        failureSlot.captured.onFailure(mockException)

        // 5. Verification
        assertEquals(false, resultSuccess)
        assertEquals(errorMsg, resultMsg)
    }
}