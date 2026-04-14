package com.example.soen345_winter2026.acceptance

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
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
 * AC03 — Customer can search and filter events.
 *
 * Issue: #123
 * GIF wiki page: Acceptance Tests › AC03
 *
 * One end-to-end flow exercises every filtering behaviour the app exposes so the GIF
 * tells a single coherent story:
 *
 *   1. Filter by category          — tap the *Movies* chip
 *   2. Filter by another category  — tap *Sports*
 *   3. Reset to All
 *   4. Search by title keyword     — "Jazz"  (matches "Jazz Night at Place des Arts")
 *   5. Search by location keyword  — "Tokyo" (matches "Japan Cherry Blossom Trip"
 *                                             whose location is Tokyo, Japan)
 *   6. Combined filter             — Concerts chip + keyword "Indie"
 *   7. Empty state                 — no-match query
 *
 * Notes on filter dimensions:
 *   - The underlying `EventFilter` supports query, category, date, and location.
 *   - The UI currently exposes only the **category** chips and a single **keyword**
 *     bar. The keyword matches against both title and location, which is why step (5)
 *     demonstrates location-based filtering through the same input.
 *   - Date filtering is supported by `EventFilter` but has no UI widget yet, so it is
 *     not exercised here.
 */
@RunWith(AndroidJUnit4::class)
class AC03_CustomerSearchFilterTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LogInActivity::class.java)

    @Before
    fun setUp() {
        signOut()
        loginAsCustomer()
    }

    @Test
    fun customer_canFilterAndSearchEvents() {
        // 1. Filter by category — tap Movies.
        onView(withId(R.id.rm2oltgkirt)).perform(click())
        pace(1200)
        onView(withId(R.id.rvEvents)).check(matches(isDisplayed()))

        // 2. Switch to a different category — Concerts.
        onView(withId(R.id.rsw2srfk1a0o)).perform(click())
        pace(1200)
        onView(withId(R.id.rvEvents)).check(matches(isDisplayed()))

        // 3. Reset to All.
        onView(withId(R.id.r6f8umn2i5l6)).perform(click())
        pace(1000)

        // 4. Keyword search by title — "Jazz" should match "Jazz Night at Place des Arts".
        onView(withId(R.id.etSearch))
            .perform(click(), typeText("Jazz"), closeSoftKeyboard())
        pace(1500)
        onView(withId(R.id.rvEvents)).check(matches(isDisplayed()))

        // 5. Keyword search by location — "Tokyo" should match "Japan Cherry Blossom Trip".
        onView(withId(R.id.etSearch))
            .perform(clearText(), typeText("Tokyo"), closeSoftKeyboard())
        pace(1500)
        onView(withId(R.id.rvEvents)).check(matches(isDisplayed()))

        // 6. Combined filter — Concerts category + keyword "Indie".
        onView(withId(R.id.etSearch)).perform(clearText(), closeSoftKeyboard())
        pace(600)
        onView(withId(R.id.rsw2srfk1a0o)).perform(click())
        pace(800)
        onView(withId(R.id.etSearch))
            .perform(click(), typeText("Indie"), closeSoftKeyboard())
        pace(1500)
        onView(withId(R.id.rvEvents)).check(matches(isDisplayed()))

        // 7. Empty state — nothing matches.
        onView(withId(R.id.r6f8umn2i5l6)).perform(click())   // back to All
        pace(600)
        onView(withId(R.id.etSearch))
            .perform(clearText(), typeText("zzzzz_no_event_matches_this_query"), closeSoftKeyboard())
        pace(1500)
        onView(withId(R.id.tvEmpty)).check(matches(isDisplayed()))
        pace()
    }
}
