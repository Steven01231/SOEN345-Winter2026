package com.example.soen345_winter2026.confirmation

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("EmailService")
class EmailServiceTest {

    private class FakeEmailService(
        private val shouldSucceed: Boolean = true,
        private val errorMessage: String? = null
    ) : EmailService {
        var lastMessage: ConfirmationMessage? = null
        var sendCalled = false

        override fun send(message: ConfirmationMessage, callback: (Boolean, String?) -> Unit) {
            sendCalled = true
            lastMessage = message
            callback(shouldSucceed, errorMessage)
        }
    }

    private fun createMessage(
        email: String = "user@example.com",
        phone: String = "5141234567"
    ) = ConfirmationMessage(
        subject = "Reservation confirmation for Concert",
        body = "Event: Concert\nDate: 2026-05-01\nLocation: Montreal\nTickets: 2\nReservation ID: abc123",
        recipientEmail = email,
        recipientPhone = phone
    )

    @Nested
    @DisplayName("FakeEmailService for mocking")
    inner class FakeServiceTests {

        @Test
        fun `send calls callback with success when service succeeds`() {
            val fakeService = FakeEmailService(shouldSucceed = true)
            var result: Boolean? = null
            var error: String? = null

            fakeService.send(createMessage()) { success, err ->
                result = success
                error = err
            }

            assertThat(fakeService.sendCalled).isTrue()
            assertThat(result).isTrue()
            assertThat(error).isNull()
        }

        @Test
        fun `send calls callback with failure when service fails`() {
            val fakeService = FakeEmailService(shouldSucceed = false, errorMessage = "SMTP error")
            var result: Boolean? = null
            var error: String? = null

            fakeService.send(createMessage()) { success, err ->
                result = success
                error = err
            }

            assertThat(result).isFalse()
            assertThat(error).isEqualTo("SMTP error")
        }
    }
}
