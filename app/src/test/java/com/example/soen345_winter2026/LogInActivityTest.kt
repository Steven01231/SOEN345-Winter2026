package com.example.soen345_winter2026

import android.content.Intent
import com.example.soen345_winter2026.database.RegistrationDB
import io.mockk.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowToast

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
class LogInActivityTest {

    private lateinit var mockDb: RegistrationDB

    @Before
    fun setup() {
        mockDb = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }


    @Test
    fun `should show error when fields are empty`() {
        val controller = Robolectric.buildActivity(LogInActivity::class.java)
        val activity = controller.get()

        activity.registrationDB = mockDb
        controller.setup()

        activity.binding.btnLogin.performClick()

        assertEquals(
            "Fields cannot be empty",
            ShadowToast.getTextOfLatestToast()?.toString()
        )
    }

    @Test
    fun `should login successfully and navigate`() {
        val controller = Robolectric.buildActivity(LogInActivity::class.java)
        val activity = controller.get()

        activity.registrationDB = mockDb
        controller.setup()

        activity.binding.etEmail.setText("test@test.com")
        activity.binding.etPassword.setText("123456")

        val callbackSlot = slot<(Boolean, String?) -> Unit>()

        every {
            mockDb.logIn(any(), any(), capture(callbackSlot))
        } answers {
            callbackSlot.captured(true, null)
        }

        activity.binding.btnLogin.performClick()

        verify {
            mockDb.logIn("test@test.com", "123456", any())
        }

        assertEquals(
            "Login successful",
            ShadowToast.getTextOfLatestToast()?.toString()
        )

        val startedIntent = shadowOf(activity).nextStartedActivity
        assertEquals(MainPageActivity::class.java.name, startedIntent.component?.className)
    }

    @Test
    fun `should show error when login fails`() {
        val controller = Robolectric.buildActivity(LogInActivity::class.java)
        val activity = controller.get()

        activity.registrationDB = mockDb
        controller.setup()

        activity.binding.etEmail.setText("test@test.com")
        activity.binding.etPassword.setText("wrong")

        val errorMsg = "Invalid credentials"
        val callbackSlot = slot<(Boolean, String?) -> Unit>()

        every {
            mockDb.logIn(any(), any(), capture(callbackSlot))
        } answers {
            callbackSlot.captured(false, errorMsg)
        }

        activity.binding.btnLogin.performClick()

        verify {
            mockDb.logIn("test@test.com", "wrong", any())
        }

        assertEquals(
            "Login failed: $errorMsg",
            ShadowToast.getTextOfLatestToast()?.toString()
        )
    }

    @Test
    fun `should navigate to SignUpActivity`() {
        val controller = Robolectric.buildActivity(LogInActivity::class.java)
        val activity = controller.get()

        activity.registrationDB = mockDb
        controller.setup()

        activity.binding.btnSignUp.performClick()

        val intent = shadowOf(activity).nextStartedActivity
        assertEquals(SignUpActivity::class.java.name, intent.component?.className)
    }
}