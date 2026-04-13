package com.example.soen345_winter2026

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.soen345_winter2026.admin.EditEventActivity
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EditEventActivityEspressoTest {

    private fun launch(price: Double = 42.5): ActivityScenario<EditEventActivity> {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val intent = Intent(context, EditEventActivity::class.java).apply {
            putExtra("eventId", "evt_1")
            putExtra("title", "Demo Event")
            putExtra("category", "Movie")
            putExtra("location", "Montreal")
            putExtra("date", "2026-05-01")
            putExtra("availableSeats", 50)
            putExtra("price", price)
            putExtra("imageUrl", "")
        }
        return ActivityScenario.launch(intent)
    }

    @Test
    fun prefillsPriceFromIntent() {
        launch(19.99).use {
            onView(withId(R.id.etPrice)).perform(scrollTo())
                .check(matches(withText("19.99")))
        }
    }

    @Test
    fun prefillsTitleFromIntent() {
        launch().use {
            onView(withId(R.id.etEventTitle)).perform(scrollTo())
                .check(matches(withText("Demo Event")))
        }
    }

    @Test
    fun updateButton_usesUpdateLabel() {
        launch().use {
            onView(withId(R.id.btnCreateEvent)).perform(scrollTo())
                .check(matches(withText("Update Event")))
        }
    }

    @Test
    fun priceFieldVisibleOnEditScreen() {
        launch().use {
            onView(withId(R.id.etPrice)).perform(scrollTo()).check(matches(isDisplayed()))
        }
    }
}
