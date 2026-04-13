package com.example.soen345_winter2026

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AdminPageActivityEspressoTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(AdminPageActivity::class.java)

    @Test
    fun fabAddEvent_isDisplayed() {
        onView(withId(R.id.fabAddEvent)).check(matches(isDisplayed()))
    }

    @Test
    fun bottomNavigation_isDisplayed() {
        onView(withId(R.id.bottomNavigation)).check(matches(isDisplayed()))
    }

    @Test
    fun customerViewButton_isDisplayed() {
        onView(withId(R.id.tvCustomerView)).check(matches(isDisplayed()))
    }

    @Test
    fun clickFabAddEvent_opensAddEventActivity() {
        onView(withId(R.id.fabAddEvent)).perform(click())
    }

    @Test
    fun dashboardTab_selectedByDefault() {
        activityRule.scenario.onActivity { activity ->
            val nav = activity.findViewById<BottomNavigationView>(R.id.bottomNavigation)
            assertEquals(R.id.nav_dashboard, nav.selectedItemId)
        }
    }
}
