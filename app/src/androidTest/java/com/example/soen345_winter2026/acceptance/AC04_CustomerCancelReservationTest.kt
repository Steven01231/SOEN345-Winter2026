package com.example.soen345_winter2026.acceptance

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.soen345_winter2026.LogInActivity
import com.example.soen345_winter2026.R
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * AC04 — Customer can cancel a reservation.
 *
 * Issue: #124
 * GIF wiki page: Acceptance Tests › AC04
 *
 * The test books the first available event so the cancel step always has a target.
 */
@RunWith(AndroidJUnit4::class)
class AC04_CustomerCancelReservationTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LogInActivity::class.java)

    @Before
    fun setUp() {
        signOut()
        loginAsCustomer()
    }

    @Test
    fun customer_canCancelExistingReservation() {
        // Book the first event so the user has at least one ticket to cancel.
        onView(withId(R.id.rvEvents)).perform(clickRecyclerChild(0, R.id.btnBook))
        waitForNetwork()

        // Dismiss the booking confirmation dialog if it appears.
        try {
            onView(withText("Close")).perform(click())
            pace()
        } catch (_: Throwable) { /* dialog may not appear if already booked */ }

        // Navigate to My Tickets via the bottom nav.
        onView(withId(R.id.navMyTickets)).perform(click())
        waitForNetwork()

        // Tap Cancel Reservation on the first ticket.
        onView(withId(R.id.rvTickets)).perform(clickRecyclerChild(0, R.id.btnCancelTicket))
        pace()

        // Confirm in the dialog.
        onView(withText("Yes, Cancel")).perform(click())
        waitForNetwork()

        // The tickets recycler view should still be visible.
        onView(withId(R.id.rvTickets)).check(matches(isDisplayed()))
        pace()
    }
}
