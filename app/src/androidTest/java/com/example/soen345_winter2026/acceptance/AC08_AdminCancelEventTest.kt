package com.example.soen345_winter2026.acceptance

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.soen345_winter2026.LogInActivity
import com.example.soen345_winter2026.R
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * AC08 — Admin can cancel an event.
 *
 * Issue: #128
 * GIF wiki page: Acceptance Tests › AC08
 *
 * Creates a throwaway event first so the cancel step always has a target, then taps
 * Cancel on the first card and confirms in the dialog.
 */
@RunWith(AndroidJUnit4::class)
class AC08_AdminCancelEventTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LogInActivity::class.java)

    @Before
    fun setUp() {
        signOut()
        loginAsAdmin()
    }

    @Test
    fun admin_canCancelEvent() {
        // --- 1. Create an event we can cancel. ---
        onView(withId(R.id.fabAddEvent)).perform(click())
        pace(800)

        val unique = System.currentTimeMillis()
        onView(withId(R.id.etEventTitle))
            .perform(scrollTo(), typeText("Cancellable Event $unique"), closeSoftKeyboard())
        pace()
        onView(withId(R.id.etCategory))
            .perform(scrollTo(), typeText("Sports"), closeSoftKeyboard())
        pace()
        onView(withId(R.id.etLocation))
            .perform(scrollTo(), typeText("Toronto"), closeSoftKeyboard())
        pace()
        pickTodayInDatePicker()
        onView(withId(R.id.etCapacity))
            .perform(scrollTo(), typeText("80"), closeSoftKeyboard())
        pace()
        onView(withId(R.id.etPrice))
            .perform(scrollTo(), typeText("12.50"), closeSoftKeyboard())
        pace()
        onView(withId(R.id.btnCreateEvent)).perform(scrollTo(), click())
        waitForNetwork(5000)

        // --- 2. Cancel the first event in the admin list. ---
        onView(withId(R.id.rvEvents)).perform(clickRecyclerChild(0, R.id.btnCancel))
        pace()
        onView(withText("Yes")).perform(click())
        waitForNetwork(3000)
    }
}
