package com.example.soen345_winter2026.events

import android.view.View
import android.widget.HorizontalScrollView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.soen345_winter2026.R
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
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
    fun activityLaunches_categoryButtonsDisplayed() {
        onView(withId(R.id.r6f8umn2i5l6)).check(matches(isDisplayed()))
        onView(withId(R.id.rm2oltgkirt)).check(matches(isDisplayed()))
        onView(withId(R.id.rsw2srfk1a0o)).check(matches(isDisplayed()))
        onView(withId(R.id.r97kqkl7tb4k)).check(matches(isDisplayed()))
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
        // scrollTo() only works with vertical ScrollView, so use a custom action
        // that programmatically scrolls the HorizontalScrollView to reveal the button
        onView(withId(R.id.r03y1ckq5t7pp))
            .perform(scrollHorizontallyTo(), click())
    }

    /** Scrolls the nearest HorizontalScrollView ancestor so the target view is visible. */
    private fun scrollHorizontallyTo(): ViewAction = object : ViewAction {
        override fun getConstraints(): Matcher<View> =
            allOf(isDescendantOfA(isAssignableFrom(HorizontalScrollView::class.java)))

        override fun getDescription(): String = "scroll HorizontalScrollView to view"

        override fun perform(uiController: UiController, view: View) {
            var parent = view.parent
            while (parent != null && parent !is HorizontalScrollView) {
                parent = parent.parent
            }
            (parent as? HorizontalScrollView)?.let {
                val location = IntArray(2)
                view.getLocationInWindow(location)
                it.smoothScrollTo(location[0], 0)
                uiController.loopMainThreadUntilIdle()
            }
        }
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
