package com.example.soen345_winter2026.confirmation

import android.content.Context
import com.example.soen345_winter2026.reservation.Reservation
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.common.truth.Truth.assertThat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("ConfirmationManager Tests")
class ConfirmationManagerTest {

    private lateinit var mockContext: Context
    private lateinit var mockAuth: FirebaseAuth
    private lateinit var mockDb: FirebaseFirestore
    private lateinit var mockUser: FirebaseUser
    private lateinit var mockDocRef: DocumentReference
    private lateinit var mockGetTask: Task<DocumentSnapshot>
    private lateinit var mockSnapshot: DocumentSnapshot
    private lateinit var fakeEmailService: FakeEmailService

    @BeforeEach
    fun setUp() {
        mockContext = mockk(relaxed = true)
        mockAuth = mockk(relaxed = true)
        mockDb = mockk(relaxed = true)
        mockUser = mockk(relaxed = true)
        mockDocRef = mockk(relaxed = true)
        mockGetTask = mockk(relaxed = true)
        mockSnapshot = mockk(relaxed = true)

        mockkStatic(FirebaseAuth::class)
        mockkStatic(FirebaseFirestore::class)
        mockkObject(SmsNotify)

        every { FirebaseAuth.getInstance() } returns mockAuth
        every { FirebaseFirestore.getInstance() } returns mockDb

        fakeEmailService = FakeEmailService()
        ConfirmationManager.emailService = fakeEmailService
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    private fun makeReservation() = Reservation(
        reservationID = "res-001",
        eventTitle = "Jazz Night",
        eventDate = "2026-08-10",
        eventLocation = "Place des Arts",
        ticketCount = 2
    )

    /** Captures and returns the OnSuccessListener slot so tests can fire it manually. */
    private fun setupFirestoreSuccess(
        email: String,
        phone: String
    ): OnSuccessListener<DocumentSnapshot> {
        every { mockAuth.currentUser } returns mockUser
        every { mockUser.uid } returns "uid-abc"

        every { mockDb.collection("users").document("uid-abc") } returns mockDocRef
        every { mockDocRef.get() } returns mockGetTask

        every { mockSnapshot.exists() } returns true
        every { mockSnapshot.getString("email") } returns email
        every { mockSnapshot.getString("phone") } returns phone

        val slot = slot<OnSuccessListener<DocumentSnapshot>>()
        every { mockGetTask.addOnSuccessListener(capture(slot)) } returns mockGetTask

        return OnSuccessListener<DocumentSnapshot> { slot.captured.onSuccess(mockSnapshot) }
    }

    // temporary email interface since can't use the actual one
    private class FakeEmailService(
        private val shouldSucceed: Boolean = true,
        private val errorMessage: String? = null
    ) : EmailService {
        var sendCalled = false

        override fun send(message: ConfirmationMessage, callback: (Boolean, String?) -> Unit) {
            sendCalled = true
            callback(shouldSucceed, errorMessage)
        }
    }

    @Nested
    @DisplayName("notify auth and Firestore guards")
    inner class NotifyGuardTests {

        @Test
        @DisplayName("Should fail when user not logged in")
        fun `returns failure when user is not logged in`() {
            every { mockAuth.currentUser } returns null
            var success: Boolean? = null
            var error: String? = null

            ConfirmationManager.notify(mockContext, makeReservation()) { s, e ->
                success = s; error = e
            }

            assertThat(success).isFalse()
            assertThat(error).isEqualTo("User not logged in")
        }

        @Test
        @DisplayName("Should fail when firestore user document doesn't exist")
        fun `returns failure when Firestore document does not exist`() {
            every { mockAuth.currentUser } returns mockUser
            every { mockUser.uid } returns "uid-abc"
            every { mockDb.collection("users").document("uid-abc") } returns mockDocRef
            every { mockDocRef.get() } returns mockGetTask
            every { mockSnapshot.exists() } returns false

            val slot = slot<OnSuccessListener<DocumentSnapshot>>()
            every { mockGetTask.addOnSuccessListener(capture(slot)) } returns mockGetTask

            var success: Boolean? = null
            var error: String? = null

            ConfirmationManager.notify(mockContext, makeReservation()) { s, e ->
                success = s; error = e
            }
            slot.captured.onSuccess(mockSnapshot)

            assertThat(success).isFalse()
            assertThat(error).isEqualTo("User data not found")
        }

        @Test
        @DisplayName("Should fail when contact information is invalid")
        fun `returns failure when no valid contact info on file`() {
            val trigger = setupFirestoreSuccess(email = "", phone = "")
            var success: Boolean? = null
            var error: String? = null

            ConfirmationManager.notify(mockContext, makeReservation()) { s, e ->
                success = s; error = e
            }
            trigger.onSuccess(mockSnapshot)

            assertThat(success).isFalse()
            assertThat(error).isEqualTo("No valid contact information on file")
        }

        @Test
        @DisplayName("Should not treat phone pseudo email as real email")
        fun `treats @phone mock email address as no real email`() {
            // phone.com address + no phone → no valid contact info
            val trigger = setupFirestoreSuccess(email = "15141234567@phone.com", phone = "")
            var success: Boolean? = null

            ConfirmationManager.notify(mockContext, makeReservation()) { s, _ -> success = s }
            trigger.onSuccess(mockSnapshot)

            assertThat(success).isFalse()
        }

        @Test
        @DisplayName("Should fail if firestore fetch fails")
        fun `returns failure when Firestore fetch fails`() {
            every { mockAuth.currentUser } returns mockUser
            every { mockUser.uid } returns "uid-abc"
            every { mockDb.collection("users").document("uid-abc") } returns mockDocRef
            every { mockDocRef.get() } returns mockGetTask

            val successSlot = slot<OnSuccessListener<DocumentSnapshot>>()
            val failureSlot = slot<OnFailureListener>()
            every { mockGetTask.addOnSuccessListener(capture(successSlot)) } returns mockGetTask
            every { mockGetTask.addOnFailureListener(capture(failureSlot)) } returns mockGetTask

            var success: Boolean? = null
            var error: String? = null

            ConfirmationManager.notify(mockContext, makeReservation()) { s, e ->
                success = s; error = e
            }
            failureSlot.captured.onFailure(Exception("Firestore unavailable"))

            assertThat(success).isFalse()
            assertThat(error).isEqualTo("Firestore unavailable")
        }
    }

    @Nested
    @DisplayName("sendNotifications routing")
    inner class SendNotificationsTests {

        @Test
        @DisplayName("Should only send email when sole info")
        fun `sends email only when real email is set and no phone`() {
            val trigger = setupFirestoreSuccess(email = "user@example.com", phone = "")

            ConfirmationManager.notify(mockContext, makeReservation()) { _, _ -> }
            trigger.onSuccess(mockSnapshot)

            assertThat(fakeEmailService.sendCalled).isTrue()
            verify(exactly = 0) { SmsNotify.send(any(), any(), any()) }
        }

        @Test
        @DisplayName("Should only send SMS when sole info")
        fun `sends SMS only when phone is set and email is phone-com address`() {
            val trigger = setupFirestoreSuccess(
                email = "15141234567@phone.com",
                phone = "15141234567"
            )
            every { SmsNotify.send(any(), any(), any()) } just Runs

            ConfirmationManager.notify(mockContext, makeReservation()) { _, _ -> }
            trigger.onSuccess(mockSnapshot)

            assertThat(fakeEmailService.sendCalled).isFalse()
            verify(exactly = 1) { SmsNotify.send(mockContext, any(), any()) }
        }

        @Test
        @DisplayName("Should?")
        fun `sends both when real email and phone are set`() {
            val trigger = setupFirestoreSuccess(
                email = "user@example.com",
                phone = "5141234567"
            )
            every { SmsNotify.send(any(), any(), any()) } just Runs

            ConfirmationManager.notify(mockContext, makeReservation()) { _, _ -> }
            trigger.onSuccess(mockSnapshot)

            assertThat(fakeEmailService.sendCalled).isTrue()
            verify(exactly = 1) { SmsNotify.send(mockContext, any(), any()) }
        }
    }

    @Nested
    @DisplayName("sendBoth outcome combinations")
    inner class SendBothTests {

        private fun setupBoth(
            emailSuccess: Boolean,
            emailError: String?,
            smsSuccess: Boolean,
            smsError: String?
        ): Pair<Boolean?, String?> {
            ConfirmationManager.emailService = FakeEmailService(emailSuccess, emailError)

            every { SmsNotify.send(any(), any(), any()) } answers {
                val cb = thirdArg<(Boolean, String?) -> Unit>()
                cb(smsSuccess, smsError)
            }

            val trigger = setupFirestoreSuccess(
                email = "user@example.com",
                phone = "5141234567"
            )

            var resultSuccess: Boolean? = null
            var resultError: String? = null

            ConfirmationManager.notify(mockContext, makeReservation()) { s, e ->
                resultSuccess = s; resultError = e
            }
            trigger.onSuccess(mockSnapshot)

            return resultSuccess to resultError
        }

        @Test
        @DisplayName("Should send email and SMS successfully")
        fun `both succeed, returns true with null error`() {
            val (success, error) = setupBoth(
                emailSuccess = true, emailError = null,
                smsSuccess = true, smsError = null
            )
            assertThat(success).isTrue()
            assertThat(error).isNull()
        }

        @Test
        @DisplayName("Should warn SMS failed when sending both")
        fun `only email succeeds, returns true with SMS failure message`() {
            val (success, error) = setupBoth(
                emailSuccess = true, emailError = null,
                smsSuccess = false, smsError = "No signal"
            )
            assertThat(success).isTrue()
            assertThat(error).contains("SMS failed")
            assertThat(error).contains("No signal")
        }

        @Test
        @DisplayName("Should warn email failed when sending both")
        fun `only SMS succeeds, returns true with email failure message`() {
            val (success, error) = setupBoth(
                emailSuccess = false, emailError = "SMTP timeout",
                smsSuccess = true, smsError = null
            )
            assertThat(success).isTrue()
            assertThat(error).contains("email failed")
            assertThat(error).contains("SMTP timeout")
        }

        @Test
        @DisplayName("Should warn email and SMS failed if both fail")
        fun `both fail, returns false with combined error message`() {
            val (success, error) = setupBoth(
                emailSuccess = false, emailError = "SMTP error",
                smsSuccess = false, smsError = "No signal"
            )
            assertThat(success).isFalse()
            assertThat(error).contains("Both failed")
            assertThat(error).contains("SMTP error")
            assertThat(error).contains("No signal")
        }

        @Test
        @DisplayName("Should have failure information if email and SMS both fail")
        fun `both fail, error message contains Email and SMS labels`() {
            val (_, error) = setupBoth(
                emailSuccess = false, emailError = "err1",
                smsSuccess = false, smsError = "err2"
            )
            assertThat(error).contains("Email")
            assertThat(error).contains("SMS")
        }
    }
}