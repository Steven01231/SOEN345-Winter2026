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
        registrationDB.signUp(email, "", pass, name, false) { success, _ ->
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

        registrationDB.signUp("a@b.com", "", "123", "Name", false) { success, msg ->
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

        registrationDB.signUp(email, "", pass, name, true) { success, msg ->
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

    @Test
    fun `adminLogIn - success when admin document exists`() {
        val email = "admin@test.com"
        val uid = "admin_123"
        val mockDocSnapshot = mockk<com.google.firebase.firestore.DocumentSnapshot>(relaxed = true)
        val mockTaskGet = mockk<Task<com.google.firebase.firestore.DocumentSnapshot>>(relaxed = true)

        // 1. Mock Auth Success
        every { mockAuth.signInWithEmailAndPassword(any(), any()) } returns mockTaskAuth
        every { mockTaskAuth.isSuccessful } returns true
        every { mockAuth.currentUser } returns mockUser
        every { mockUser.uid } returns uid

        // 2. Mock Firestore Success
        every { mockDb.collection("users").document(uid).get() } returns mockTaskGet
        every { mockDocSnapshot.exists() } returns true
        every { mockDocSnapshot.getBoolean("isAdmin") } returns true

        val authSlot = slot<OnCompleteListener<AuthResult>>()
        val firestoreSlot = slot<OnSuccessListener<com.google.firebase.firestore.DocumentSnapshot>>()

        every { mockTaskAuth.addOnCompleteListener(capture(authSlot)) } returns mockTaskAuth
        every { mockTaskGet.addOnSuccessListener(capture(firestoreSlot)) } returns mockTaskGet

        var successRes = false
        var adminRes = false
        registrationDB.adminLogIn(email, "password") { success, isAdmin, _ ->
            successRes = success
            adminRes = isAdmin
        }

        // Trigger chain
        authSlot.captured.onComplete(mockTaskAuth)
        firestoreSlot.captured.onSuccess(mockDocSnapshot)

        assertTrue(successRes)
        assertTrue(adminRes)
    }

    @Test
    fun `adminLogIn - failure when auth fails`() {
        val errorMsg = "Login failed"
        every { mockAuth.signInWithEmailAndPassword(any(), any()) } returns mockTaskAuth
        every { mockTaskAuth.isSuccessful } returns false
        every { mockTaskAuth.exception?.message } returns errorMsg

        val authSlot = slot<OnCompleteListener<AuthResult>>()
        every { mockTaskAuth.addOnCompleteListener(capture(authSlot)) } returns mockTaskAuth

        registrationDB.adminLogIn("a@b.com", "123") { success, isAdmin, msg ->
            assertFalse(success)
            assertFalse(isAdmin)
            assertEquals(errorMsg, msg)
        }

        authSlot.captured.onComplete(mockTaskAuth)
    }

    @Test
    fun `adminLogIn - failure when user document missing`() {
        val uid = "user_456"
        val mockDocSnapshot = mockk<com.google.firebase.firestore.DocumentSnapshot>(relaxed = true)
        val mockTaskGet = mockk<Task<com.google.firebase.firestore.DocumentSnapshot>>(relaxed = true)

        every { mockAuth.signInWithEmailAndPassword(any(), any()) } returns mockTaskAuth
        every { mockTaskAuth.isSuccessful } returns true
        every { mockAuth.currentUser } returns mockUser
        every { mockUser.uid } returns uid

        every { mockDb.collection("users").document(uid).get() } returns mockTaskGet
        every { mockDocSnapshot.exists() } returns false // Document doesn't exist

        val authSlot = slot<OnCompleteListener<AuthResult>>()
        val firestoreSlot = slot<OnSuccessListener<com.google.firebase.firestore.DocumentSnapshot>>()

        every { mockTaskAuth.addOnCompleteListener(capture(authSlot)) } returns mockTaskAuth
        every { mockTaskGet.addOnSuccessListener(capture(firestoreSlot)) } returns mockTaskGet

        registrationDB.adminLogIn("test@test.com", "pass") { success, _, msg ->
            assertFalse(success)
            assertEquals("User data not found", msg)
        }

        authSlot.captured.onComplete(mockTaskAuth)
        firestoreSlot.captured.onSuccess(mockDocSnapshot)
    }

    @Test
    fun `adminLogIn - failure when firestore fetch fails`() {
        val uid = "user_789"
        val errorMsg = "Firestore Error"
        val mockTaskGet = mockk<Task<com.google.firebase.firestore.DocumentSnapshot>>(relaxed = true)

        every { mockAuth.signInWithEmailAndPassword(any(), any()) } returns mockTaskAuth
        every { mockTaskAuth.isSuccessful } returns true
        every { mockAuth.currentUser } returns mockUser
        every { mockUser.uid } returns uid

        every { mockDb.collection("users").document(uid).get() } returns mockTaskGet

        val authSlot = slot<OnCompleteListener<AuthResult>>()
        val failureSlot = slot<OnFailureListener>()

        every { mockTaskAuth.addOnCompleteListener(capture(authSlot)) } returns mockTaskAuth
        every { mockTaskGet.addOnSuccessListener(any()) } returns mockTaskGet
        every { mockTaskGet.addOnFailureListener(capture(failureSlot)) } returns mockTaskGet

        registrationDB.adminLogIn("test@test.com", "pass") { success, _, msg ->
            assertFalse(success)
            assertEquals(errorMsg, msg)
        }

        authSlot.captured.onComplete(mockTaskAuth)
        failureSlot.captured.onFailure(Exception(errorMsg))
    }

    @Test
    fun `signUp - phone only auth and firestore succeed`() {
        val phone = "15143334444"
        val fakeEmail = "15143334444@phone.com"
        val pass = "password123"
        val name = "John Doe"
        val uid = "user_123"

        // 1. Mock Auth Creation
        every { mockAuth.createUserWithEmailAndPassword(fakeEmail, pass) } returns mockTaskAuth
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
        registrationDB.signUp("", phone, pass, name, false) { success, _ ->
            resultSuccess = success
        }

        // Trigger callbacks manually
        authSlot.captured.onComplete(mockTaskAuth)
        firestoreSlot.captured.onSuccess(null)

        assertTrue(resultSuccess)
    }

    @Test
    fun `signUp - fail when both email and phone missing`() {
        val errorMsg = "Please provide a valid email or phone number"

        registrationDB.signUp("", "", "12345678", "Name", false) { success, msg ->
            assertFalse(success)
            assertEquals(errorMsg, msg)
        }
    }

    @Test
    fun `logIn - fail when cannot resolve input to phone or email`() {
        val errorMsg = "Invalid email or phone number"

        registrationDB.logIn("whatever", "12345678") { success, msg ->
            assertFalse(success)
            assertEquals(errorMsg, msg)
        }
    }

    @Test
    fun `logIn - success with valid phone input`() {
        val phone = "15143334444"
        val fakeEmail = "15143334444@phone.com"

        every { mockAuth.signInWithEmailAndPassword(fakeEmail, any()) } returns mockTaskAuth
        every { mockTaskAuth.isSuccessful } returns true

        val slot = slot<OnCompleteListener<AuthResult>>()
        every { mockTaskAuth.addOnCompleteListener(capture(slot)) } returns mockTaskAuth

        registrationDB.logIn(phone, "password123") { success, _ ->
            assertTrue(success)
        }

        slot.captured.onComplete(mockTaskAuth)
    }

    @Test
    fun `logIn - success with valid phone input for admin`() {
        val phone = "15143334444"
        val fakeEmail = "15143334444@phone.com"
        val uid = "admin_123"
        val mockDocSnapshot = mockk<com.google.firebase.firestore.DocumentSnapshot>(relaxed = true)
        val mockTaskGet = mockk<Task<com.google.firebase.firestore.DocumentSnapshot>>(relaxed = true)

        // 1. Mock Auth Success
        every { mockAuth.signInWithEmailAndPassword(fakeEmail, any()) } returns mockTaskAuth
        every { mockTaskAuth.isSuccessful } returns true
        every { mockAuth.currentUser } returns mockUser
        every { mockUser.uid } returns uid

        // 2. Mock Firestore Success
        every { mockDb.collection("users").document(uid).get() } returns mockTaskGet
        every { mockDocSnapshot.exists() } returns true
        every { mockDocSnapshot.getBoolean("isAdmin") } returns true

        val authSlot = slot<OnCompleteListener<AuthResult>>()
        val firestoreSlot = slot<OnSuccessListener<com.google.firebase.firestore.DocumentSnapshot>>()

        every { mockTaskAuth.addOnCompleteListener(capture(authSlot)) } returns mockTaskAuth
        every { mockTaskGet.addOnSuccessListener(capture(firestoreSlot)) } returns mockTaskGet

        var successRes = false
        var adminRes = false
        registrationDB.adminLogIn(phone, "password") { success, isAdmin, _ ->
            successRes = success
            adminRes = isAdmin
        }

        // Trigger chain
        authSlot.captured.onComplete(mockTaskAuth)
        firestoreSlot.captured.onSuccess(mockDocSnapshot)

        assertTrue(successRes)
        assertTrue(adminRes)
    }
    
    @Test
    fun `logIn - phone login fails when account has email`() {
        val phone = "15143334444"
        val fakeEmail = "15143334444@phone.com"

        every { mockAuth.signInWithEmailAndPassword(fakeEmail, any()) } returns mockTaskAuth
        every { mockTaskAuth.isSuccessful } returns false

        val slot = slot<OnCompleteListener<AuthResult>>()
        every { mockTaskAuth.addOnCompleteListener(capture(slot)) } returns mockTaskAuth

        registrationDB.logIn(phone, "password123") { success, _ ->
            assertFalse(success)
        }

        slot.captured.onComplete(mockTaskAuth)
    }

}