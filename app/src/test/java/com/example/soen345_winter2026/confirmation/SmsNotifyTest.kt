package com.example.soen345_winter2026.confirmation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("SmsNotify Tests")
class SmsNotifyTest {

    private lateinit var mockContext: Context

    @BeforeEach
    fun setUp() {
        mockContext = mockk(relaxed = true)
        mockkStatic(ContextCompat::class)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    } // because of the static mock & function

    private fun makeMessage(phone: String = "15141234567") =
        ConfirmationMessage(
            subject = "Reservation confirmation for Concert",
            body = "Event: Concert\nDate: 2036-04-01\nLocation: Montreal\nTickets: 2\nReservation ID: abc123",
            recipientEmail = "user@example.com",
            recipientPhone = phone
        )

//    private fun grantSmsPermission() {
//        every {
//            ContextCompat.checkSelfPermission(mockContext, Manifest.permission.SEND_SMS)
//        } returns PackageManager.PERMISSION_GRANTED
//    }

    private fun denySmsPermission() {
        every {
            ContextCompat.checkSelfPermission(mockContext, Manifest.permission.SEND_SMS)
        } returns PackageManager.PERMISSION_DENIED
    }

    @Test
    @DisplayName("Should fail when phone number is empty")
    fun `returns failure when phone number is empty`() {
        var success: Boolean? = null
        var error: String? = null

        SmsNotify.send(mockContext, makeMessage(phone = "")) { s, e ->
            success = s
            error = e
        }

        assertThat(success).isFalse()
        assertThat(error).isEqualTo("No phone number on file")
    }

    @Test
    @DisplayName("Should not check SMS perms when no phone")
    fun `does not check SMS permission when phone is empty`() {
        SmsNotify.send(mockContext, makeMessage(phone = "")) { _, _ -> }

        verify(exactly = 0) {
            ContextCompat.checkSelfPermission(any(), any())
        }
    }

    @Test
    @DisplayName("Should fail if no SMS permission")
    fun `returns failure when SMS permission is denied`() {
        denySmsPermission()
        var success: Boolean? = null
        var error: String? = null

        SmsNotify.send(mockContext, makeMessage()) { s, e ->
            success = s
            error = e
        }

        assertThat(success).isFalse()
        assertThat(error).isEqualTo("SMS permission not granted")
    }

    @Test
    @DisplayName("Should look for correct permission")
    // so you don't end up getting camera permission or something
    fun `checks correct permission constant`() {
        denySmsPermission()

        SmsNotify.send(mockContext, makeMessage()) { _, _ -> }

        verify {
            ContextCompat.checkSelfPermission(mockContext, Manifest.permission.SEND_SMS)
        }
    }

    @Test
    @DisplayName("Should callback empty phone")
    fun `callback is always invoked on empty phone`() {
        var callbackInvoked = false

        SmsNotify.send(mockContext, makeMessage(phone = "")) { _, _ ->
            callbackInvoked = true
        }

        assertThat(callbackInvoked).isTrue()
    }

    @Test
    @DisplayName("Should callback permission denied")
    fun `callback is always invoked on permission denied`() {
        denySmsPermission()
        var callbackInvoked = false

        SmsNotify.send(mockContext, makeMessage()) { _, _ ->
            callbackInvoked = true
        }

        assertThat(callbackInvoked).isTrue()
    }

}