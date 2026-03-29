package com.example.soen345_winter2026

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(ProfileActivity::class.java)

    @Test
    fun profileScreen_displaysAllUIElements() {
        onView(withId(R.id.etFullName)).check(matches(isDisplayed()))
        onView(withId(R.id.tvEmail)).check(matches(isDisplayed()))
        onView(withId(R.id.btnSave)).check(matches(isDisplayed()))
        onView(withId(R.id.btnLogout)).check(matches(isDisplayed()))
    }

    @Test
    fun profileScreen_backButtonIsDisplayed() {
        onView(withId(R.id.ibtnBack)).check(matches(isDisplayed()))
    }

    @Test
    fun profileScreen_backButtonIsClickable() {
        onView(withId(R.id.ibtnBack)).check(matches(isClickable()))
    }

    @Test
    fun profileScreen_avatarInitialIsDisplayed() {
        onView(withId(R.id.tvAvatarInitial)).check(matches(isDisplayed()))
    }

    @Test
    fun profileScreen_saveButtonHasCorrectText() {
        onView(withId(R.id.btnSave)).check(matches(withText("Save Changes")))
    }

    @Test
    fun profileScreen_logoutButtonHasCorrectText() {
        onView(withId(R.id.btnLogout)).check(matches(withText("Logout")))
    }

    @Test
    fun profileScreen_saveButtonIsClickable() {
        onView(withId(R.id.btnSave)).check(matches(isClickable()))
    }

    @Test
    fun profileScreen_logoutButtonIsClickable() {
        onView(withId(R.id.btnLogout)).check(matches(isClickable()))
    }

    @Test
    fun profileScreen_fullNameFieldIsEnabled() {
        onView(withId(R.id.etFullName)).check(matches(isEnabled()))
    }

    @Test
    fun profileScreen_canTypeInFullNameField() {
        onView(withId(R.id.etFullName))
            .perform(clearText(), typeText("John Doe"), closeSoftKeyboard())
        onView(withId(R.id.etFullName)).check(matches(withText("John Doe")))
    }
}
