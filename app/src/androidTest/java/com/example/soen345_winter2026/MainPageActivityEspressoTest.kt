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
class MainPageActivityEspressoTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainPageActivity::class.java)

    @Test
    fun mainPage_displaysMainPageText() {
        onView(withText("Main Page")).check(matches(isDisplayed()))
    }

    @Test
    fun mainPage_activityIsResumed() {
        activityRule.scenario.onActivity { activity ->
            assert(!activity.isFinishing)
        }
    }
}
