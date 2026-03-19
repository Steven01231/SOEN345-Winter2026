package com.example.soen345_winter2026

import com.example.soen345_winter2026.database.RegistrationDB
import io.mockk.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowToast

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
class SignUpActivityTest {

    private lateinit var mockDb: RegistrationDB

    @Before
    fun setup() {
        mockDb = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // ✅ No DB needed → inject mock to prevent Firebase initialization
    @Test
    fun `should show error when fields are empty`() {
        val controller = Robolectric.buildActivity(SignUpActivity::class.java)
        val activity = controller.get()

        activity.registrationDB = mockDb
        controller.setup()

        activity.binding.btnSignUp.performClick()

        assertEquals(
            "Fields cannot be empty",
            ShadowToast.getTextOfLatestToast()?.toString()
        )
    }

    // ✅ No DB needed → inject mock to prevent Firebase initialization
    @Test
    fun `should show error when passwords do not match`() {
        val controller = Robolectric.buildActivity(SignUpActivity::class.java)
        val activity = controller.get()

        activity.registrationDB = mockDb
        controller.setup()

        activity.binding.etFullName.setText("Steven")
        activity.binding.etEmail.setText("test@test.com")
        activity.binding.etPassword.setText("123")
        activity.binding.etConfirmPassword.setText("456")

        activity.binding.btnSignUp.performClick()

        assertEquals(
            "Passwords do not match",
            ShadowToast.getTextOfLatestToast()?.toString()
        )
    }

    // ✅ FIXED: inject BEFORE setup
    @Test
    fun `should call registrationDB and show success toast on success`() {
        val controller = Robolectric.buildActivity(SignUpActivity::class.java)

        val activity = controller.get()
        activity.registrationDB = mockDb   // ✅ BEFORE setup

        controller.setup()                // now onCreate runs

        activity.binding.etFullName.setText("Steven")
        activity.binding.etEmail.setText("test@test.com")
        activity.binding.etPassword.setText("123456")
        activity.binding.etConfirmPassword.setText("123456")

        val callbackSlot = slot<(Boolean, String?) -> Unit>()

        every {
            mockDb.signUp(any(), any(), any(), capture(callbackSlot))
        } answers {
            callbackSlot.captured(true, null)
        }

        activity.binding.btnSignUp.performClick()

        verify {
            mockDb.signUp("test@test.com", "123456", "Steven", any())
        }

        assertEquals(
            "Account Created",
            ShadowToast.getTextOfLatestToast()?.toString()
        )

        assertTrue(activity.isFinishing)
    }

    // ✅ FIXED: inject BEFORE setup
    @Test
    fun `should show error toast when signUp fails`() {
        val controller = Robolectric.buildActivity(SignUpActivity::class.java)

        val activity = controller.get()
        activity.registrationDB = mockDb   // ✅ BEFORE setup

        controller.setup()                // now onCreate runs

        activity.binding.etFullName.setText("Steven")
        activity.binding.etEmail.setText("test@test.com")
        activity.binding.etPassword.setText("123456")
        activity.binding.etConfirmPassword.setText("123456")

        val errorMsg = "Email already in use"
        val callbackSlot = slot<(Boolean, String?) -> Unit>()

        every {
            mockDb.signUp(any(), any(), any(), capture(callbackSlot))
        } answers {
            callbackSlot.captured(false, errorMsg)
        }

        activity.binding.btnSignUp.performClick()

        verify {
            mockDb.signUp("test@test.com", "123456", "Steven", any())
        }

        assertEquals(
            errorMsg,
            ShadowToast.getTextOfLatestToast()?.toString()
        )
    }

    @Test
    fun `clicking login should finish activity`() {
        val controller = Robolectric.buildActivity(SignUpActivity::class.java)
        val activity = controller.get()

        activity.registrationDB = mockDb
        controller.setup()

        activity.binding.tvLogin.performClick()

        assertTrue(activity.isFinishing)
    }

    @Test
    fun `should show error when password is empty`() {
        val controller = Robolectric.buildActivity(SignUpActivity::class.java)
        val activity = controller.get()

        activity.registrationDB = mockDb
        controller.setup()

        activity.binding.etFullName.setText("Steven")
        activity.binding.etEmail.setText("test@test.com")
        // password left empty

        activity.binding.btnSignUp.performClick()

        assertEquals(
            "Fields cannot be empty",
            ShadowToast.getTextOfLatestToast()?.toString()
        )
    }

    @Test
    fun `should show error when full name is empty`() {
        val controller = Robolectric.buildActivity(SignUpActivity::class.java)
        val activity = controller.get()

        activity.registrationDB = mockDb
        controller.setup()

        activity.binding.etEmail.setText("test@test.com")
        activity.binding.etPassword.setText("123456")
        activity.binding.etConfirmPassword.setText("123456")
        // fullName left empty

        activity.binding.btnSignUp.performClick()

        assertEquals(
            "Fields cannot be empty",
            ShadowToast.getTextOfLatestToast()?.toString()
        )
    }
}