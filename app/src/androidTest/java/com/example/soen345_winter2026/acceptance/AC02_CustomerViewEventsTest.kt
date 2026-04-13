package com.example.soen345_winter2026.acceptance

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.soen345_winter2026.LogInActivity
import com.example.soen345_winter2026.R
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * AC02 — Customer can view a list of available events.
 *
 * Issue: #122
 * GIF wiki page: Acceptance Tests › AC02
 */
@RunWith(AndroidJUnit4::class)
class AC02_CustomerViewEventsTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LogInActivity::class.java)

    @Before
    fun setUp() {
        signOut()
    }

    @Test
    fun customer_canSeeListOfAvailableEvents() {
        loginAsCustomer()

        // EventListActivity should now be on screen with the events recycler view.
        onView(withId(R.id.rvEvents)).check(matches(isDisplayed()))
        pace(1500)

        // At least one event card should be present — FirestoreSeeder seeds 8 sample events
        // on first launch and the production project also has live events.
        onView(withId(R.id.rvEvents)).check(hasAtLeastOneItem())
        pace()
    }

    private fun hasAtLeastOneItem() = ViewAssertion { view: View?, noViewException: NoMatchingViewException? ->
        if (noViewException != null) throw noViewException
        val recycler = view as? RecyclerView ?: error("Expected RecyclerView")
        val count = recycler.adapter?.itemCount ?: 0
        assertTrue("Expected at least one event in the list, got $count", count > 0)
    }
}
