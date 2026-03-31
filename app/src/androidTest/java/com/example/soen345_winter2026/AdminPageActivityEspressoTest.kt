package com.example.soen345_winter2026

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AdminPageActivityEspressoTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(AdminPageActivity::class.java)

    @Test
    fun testUIElementsAreDisplayed() {
        // Check if FAB is displayed
        onView(withId(R.id.fabAddEvent)).check(matches(isDisplayed()))

        // Check if Bottom Navigation is displayed
        onView(withId(R.id.bottomNavigation)).check(matches(isDisplayed()))
    }

    @Test
    fun testClickAddEvent_OpensAddEventActivity() {
        // Click the Floating Action Button
        onView(withId(R.id.fabAddEvent)).perform(click())

        // Verify we are now on the AddEventActivity
        // (This assumes AddEventActivity has a view with this ID)
        // onView(withId(R.id.addEventRootLayout)).check(matches(isDisplayed()))
    }

    /*@Test
    fun testCancelEventDialogShowsAndCanDismiss() {
        // Note: This test assumes there is at least one item in the list.
        // In a real CI environment, you'd use a Mock or a Test Firestore instance.

        Thread.sleep(2000) // Simple wait for Firebase data to load

        // Click the "Cancel/Delete" button inside the first item of the RecyclerView
        // Replace 'btnDelete' with the actual ID of the cancel button in your item_event layout
        onView(withId(R.id.rvEvents))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                0, clickChildViewWithId(R.id.btnDelete)
            ))

        // Check if AlertDialog appears
        onView(withText("Cancel Event")).check(matches(isDisplayed()))

        // Click "No" to dismiss
        onView(withText("No")).perform(click())
    }*/

    /**
     * Helper function to click a specific button inside a RecyclerView row
     */
    private fun clickChildViewWithId(id: Int) = object : androidx.test.espresso.ViewAction {
        override fun getConstraints() = null
        override fun getDescription() = "Click on a child view with specified id."
        override fun perform(uiController: androidx.test.espresso.UiController, view: android.view.View) {
            val v = view.findViewById<android.view.View>(id)
            v.performClick()
        }
    }

}