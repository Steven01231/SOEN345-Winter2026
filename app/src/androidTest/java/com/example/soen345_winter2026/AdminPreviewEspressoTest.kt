package com.example.soen345_winter2026

import android.content.Intent
import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.soen345_winter2026.events.EventListActivity
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AdminPreviewEspressoTest {

    @Test
    fun backToAdminButton_visible_whenLaunchedAsAdminPreview() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val intent = Intent(context, EventListActivity::class.java)
            .putExtra("isAdminPreview", true)

        ActivityScenario.launch<EventListActivity>(intent).use {
            onView(withId(R.id.tvBackToAdmin)).check(matches(isDisplayed()))
            onView(withId(R.id.tvBackToAdmin)).check(matches(withText("Admin Panel")))
        }
    }

    @Test
    fun backToAdminButton_hidden_whenLaunchedNormally() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val intent = Intent(context, EventListActivity::class.java)

        ActivityScenario.launch<EventListActivity>(intent).use { scenario ->
            scenario.onActivity { activity ->
                val btn = activity.findViewById<View>(R.id.tvBackToAdmin)
                assertEquals(View.GONE, btn.visibility)
            }
        }
    }
}
