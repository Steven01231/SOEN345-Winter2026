package com.example.soen345_winter2026

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MyTicketsActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MyTicketsActivity::class.java)

    @Test
    fun myTicketsScreen_titleIsDisplayed() {
        onView(withText("My Tickets")).check(matches(isDisplayed()))
    }

    @Test
    fun myTicketsScreen_backButtonIsDisplayed() {
        onView(withId(R.id.ibtnBack)).check(matches(isDisplayed()))
    }

    @Test
    fun myTicketsScreen_backButtonIsClickable() {
        onView(withId(R.id.ibtnBack)).check(matches(isClickable()))
    }

    @Test
    fun myTicketsScreen_recyclerViewIsDisplayed() {
        onView(withId(R.id.rvTickets)).check(matches(isDisplayed()))
    }

    @Test
    fun myTicketsScreen_emptyStateExistsInLayout() {
        onView(withId(R.id.tvEmpty)).check(matches(withText("You have no tickets yet.")))
    }
}
