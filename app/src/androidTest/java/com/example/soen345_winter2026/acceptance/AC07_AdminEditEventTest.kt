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
 * AC07 — Admin can edit an existing event.
 *
 * Issue: #127
 * GIF wiki page: Acceptance Tests › AC07
 *
 * The test first creates a throwaway event so the edit step always has a target,
 * then opens the first event in the admin's list and updates the location.
 */
@RunWith(AndroidJUnit4::class)
class AC07_AdminEditEventTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LogInActivity::class.java)

    @Before
    fun setUp() {
        signOut()
        loginAsAdmin()
    }

    @Test
    fun admin_canEditExistingEvent() {
        // --- 1. Create an event so we have something to edit. ---
        onView(withId(R.id.fabAddEvent)).perform(click())
        pace(800)

        val unique = System.currentTimeMillis()
        onView(withId(R.id.etEventTitle))
            .perform(scrollTo(), typeText("Editable Event $unique"), closeSoftKeyboard())
        pace()
        onView(withId(R.id.etCategory))
            .perform(scrollTo(), typeText("Movie"), closeSoftKeyboard())
        pace()
        onView(withId(R.id.etLocation))
            .perform(scrollTo(), typeText("Old Location"), closeSoftKeyboard())
        pace()
        pickTodayInDatePicker()
        onView(withId(R.id.etCapacity))
            .perform(scrollTo(), typeText("50"), closeSoftKeyboard())
        pace()
        onView(withId(R.id.etPrice))
            .perform(scrollTo(), typeText("15.00"), closeSoftKeyboard())
        pace()
        onView(withId(R.id.btnCreateEvent)).perform(scrollTo(), click())
        waitForNetwork(5000)

        // --- 2. Edit the first event in the admin list. ---
        onView(withId(R.id.rvEvents)).perform(clickRecyclerChild(0, R.id.btnEdit))
        pace(800)

        onView(withId(R.id.etLocation))
            .perform(scrollTo(), clearText(), typeText("Quebec City"), closeSoftKeyboard())
        pace()
        onView(withId(R.id.btnCreateEvent)).perform(scrollTo(), click()) // labelled "Update Event"
        waitForNetwork(5000)
    }
}
