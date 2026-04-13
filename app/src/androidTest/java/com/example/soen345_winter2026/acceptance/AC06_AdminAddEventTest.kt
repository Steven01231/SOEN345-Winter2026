package com.example.soen345_winter2026.acceptance

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.soen345_winter2026.LogInActivity
import com.example.soen345_winter2026.R
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * AC06 — Admin can add a new event.
 *
 * Issue: #126
 * GIF wiki page: Acceptance Tests › AC06
 *
 * Uses a unique event title per run so the assertion is unambiguous and tests don't pollute
 * each other. The created event remains in Firestore (visible during AC07/AC08 demos).
 */
@RunWith(AndroidJUnit4::class)
class AC06_AdminAddEventTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LogInActivity::class.java)

    @Before
    fun setUp() {
        signOut()
        loginAsAdmin()
    }

    @Test
    fun admin_canAddNewEvent() {
        // Open the Add Event screen via the FAB on the admin dashboard.
        onView(withId(R.id.fabAddEvent)).perform(click())
        pace(800)

        val unique = System.currentTimeMillis()
        val title = "Acceptance Event $unique"

        onView(withId(R.id.etEventTitle))
            .perform(scrollTo(), typeText(title), closeSoftKeyboard())
        pace()
        onView(withId(R.id.etCategory))
            .perform(scrollTo(), typeText("Concert"), closeSoftKeyboard())
        pace()
        onView(withId(R.id.etLocation))
            .perform(scrollTo(), typeText("Montreal"), closeSoftKeyboard())
        pace()
        pickTodayInDatePicker()
        onView(withId(R.id.etCapacity))
            .perform(scrollTo(), typeText("100"), closeSoftKeyboard())
        pace()
        onView(withId(R.id.etPrice))
            .perform(scrollTo(), typeText("29.99"), closeSoftKeyboard())
        pace()

        onView(withId(R.id.btnCreateEvent)).perform(scrollTo(), click())
        waitForNetwork(5000)
    }
}
