package com.example.soen345_winter2026

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withHint
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddEventActivityEspressoTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(AddEventActivity::class.java)

    @Test
    fun priceField_isDisplayed() {
        onView(withId(R.id.etPrice)).perform(scrollTo()).check(matches(isDisplayed()))
    }

    @Test
    fun priceField_hasHint() {
        onView(withId(R.id.etPrice)).perform(scrollTo())
            .check(matches(withHint("Ticket price (e.g. 25.00)")))
    }

    @Test
    fun priceField_isEnabled() {
        onView(withId(R.id.etPrice)).perform(scrollTo()).check(matches(isEnabled()))
    }

    @Test
    fun priceField_acceptsDecimalInput() {
        onView(withId(R.id.etPrice)).perform(
            scrollTo(), clearText(), typeText("19.99"), closeSoftKeyboard()
        )
        onView(withId(R.id.etPrice)).check(matches(withText("19.99")))
    }

    @Test
    fun saveButton_hasCreateEventLabel() {
        onView(withId(R.id.btnCreateEvent)).perform(scrollTo())
            .check(matches(withText("Create Event")))
    }

    @Test
    fun cancelButton_isDisplayed() {
        onView(withId(R.id.btnCancel)).perform(scrollTo()).check(matches(isDisplayed()))
    }

    @Test
    fun submittingWithoutPrice_doesNotCrash() {
        onView(withId(R.id.etEventTitle)).perform(scrollTo(), typeText("Demo"), closeSoftKeyboard())
        onView(withId(R.id.etCategory)).perform(scrollTo(), typeText("Movie"), closeSoftKeyboard())
        onView(withId(R.id.etLocation)).perform(scrollTo(), typeText("Here"), closeSoftKeyboard())
        onView(withId(R.id.etDate)).perform(scrollTo())
        onView(withId(R.id.etCapacity)).perform(scrollTo(), typeText("10"), closeSoftKeyboard())
        onView(withId(R.id.btnCreateEvent)).perform(scrollTo(), click())
    }
}
