package com.example.soen345_winter2026.acceptance

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.soen345_winter2026.R
import com.example.soen345_winter2026.SignUpActivity
import com.google.firebase.auth.FirebaseAuth
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * AC01 — Customer can register via email or phone.
 *
 * Issue: #121
 * GIF wiki page: Acceptance Tests › AC01
 */
@RunWith(AndroidJUnit4::class)
class AC01_CustomerRegisterTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(SignUpActivity::class.java)

    @Before
    fun setUp() {
        signOut()
    }

    @Test
    fun customer_canRegisterWithEmailAndPhone() {
        // Generate a fresh email per run so signup never collides with an existing user.
        val unique = System.currentTimeMillis()
        val email = "acceptance.user.$unique@example.com"
        val phone = "1514" + (unique % 10_000_000L).toString().padStart(7, '0')

        onView(withId(R.id.etFullName))
            .perform(scrollTo(), typeText("Acceptance User"), closeSoftKeyboard())
        pace()
        onView(withId(R.id.etEmail))
            .perform(scrollTo(), typeText(email), closeSoftKeyboard())
        pace()
        onView(withId(R.id.etPhone))
            .perform(scrollTo(), typeText(phone), closeSoftKeyboard())
        pace()
        onView(withId(R.id.etPassword))
            .perform(scrollTo(), typeText("Soen345@"), closeSoftKeyboard())
        pace()
        onView(withId(R.id.etConfirmPassword))
            .perform(scrollTo(), typeText("Soen345@"), closeSoftKeyboard())
        pace()

        onView(withId(R.id.btnSignUp)).perform(scrollTo(), click())
        waitForNetwork(6000)

        // SignUpActivity calls finish() on success — verify by checking that Firebase
        // created the user (currentUser is non-null and matches the email we submitted).
        val currentUser = FirebaseAuth.getInstance().currentUser
        assertNotNull("Expected Firebase user to exist after signup", currentUser)
        assertEquals(email, currentUser?.email)
    }
}
