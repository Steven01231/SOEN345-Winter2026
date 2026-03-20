package com.example.soen345_winter2026.events

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.soen345_winter2026.R
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EventListActivityTest {

    private lateinit var scenario: ActivityScenario<EventListActivity>

    @Before
    fun setUp() {
        scenario = ActivityScenario.launch(EventListActivity::class.java)
    }

    @After
    fun tearDown() {
        scenario.close()
    }

    // --- UI elements present ---

    @Test
    fun activityLaunches_searchBarIsDisplayed() {
        onView(withId(R.id.etSearch))
            .check(matches(isDisplayed()))
    }

    @Test
    fun activityLaunches_allCategoryButtonsDisplayed() {
        onView(withId(R.id.r6f8umn2i5l6)).check(matches(isDisplayed()))
        onView(withId(R.id.rm2oltgkirt)).check(matches(isDisplayed()))
        onView(withId(R.id.rsw2srfk1a0o)).check(matches(isDisplayed()))
        onView(withId(R.id.r97kqkl7tb4k)).check(matches(isDisplayed()))
        onView(withId(R.id.r03y1ckq5t7pp)).check(matches(isDisplayed()))
    }

    @Test
    fun activityLaunches_recyclerViewIsDisplayed() {
        onView(withId(R.id.rvEvents))
            .check(matches(isDisplayed()))
    }

    // --- Search interaction ---

    @Test
    fun searchBar_acceptsTextInput() {
        onView(withId(R.id.etSearch))
            .perform(typeText("Music"))
        onView(withId(R.id.etSearch))
            .check(matches(withText("Music")))
    }

    @Test
    fun searchBar_acceptsEmptyString() {
        onView(withId(R.id.etSearch))
            .perform(typeText(""))
        onView(withId(R.id.etSearch))
            .check(matches(withText("")))
    }

    // --- Category filter interaction ---

    @Test
    fun categoryButton_all_isClickable() {
        onView(withId(R.id.r6f8umn2i5l6))
            .check(matches(isClickable()))
            .perform(click())
    }

    @Test
    fun categoryButton_movies_isClickable() {
        onView(withId(R.id.rm2oltgkirt))
            .check(matches(isClickable()))
            .perform(click())
    }

    @Test
    fun categoryButton_concerts_isClickable() {
        onView(withId(R.id.rsw2srfk1a0o))
            .check(matches(isClickable()))
            .perform(click())
    }

    @Test
    fun categoryButton_travel_isClickable() {
        onView(withId(R.id.r97kqkl7tb4k))
            .check(matches(isClickable()))
            .perform(click())
    }

    @Test
    fun categoryButton_sports_isClickable() {
        onView(withText("Sports"))
            .perform(scrollTo(), click())
    }

    // --- Empty state ---

    @Test
    fun searchBar_withNoMatchingText_showsEmptyState() {
        onView(withId(R.id.etSearch))
            .perform(typeText("xyznotfoundanywhere12345"))
        onView(withId(R.id.tvEmpty))
            .check(matches(isDisplayed()))
    }
}
