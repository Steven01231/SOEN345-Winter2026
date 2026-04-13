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
class SignUpActivityEspressoTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(SignUpActivity::class.java)

    @Test
    fun signUpScreen_displaysAllUIElements() {
        onView(withId(R.id.etFullName)).check(matches(isDisplayed()))
        onView(withId(R.id.etEmail)).check(matches(isDisplayed()))
        onView(withId(R.id.etPhone)).check(matches(isDisplayed()))
        onView(withId(R.id.etPassword)).check(matches(isDisplayed()))
        onView(withId(R.id.etConfirmPassword)).check(matches(isDisplayed()))
        onView(withId(R.id.swIsAdmin)).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withId(R.id.btnSignUp)).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withId(R.id.tvLogin)).perform(scrollTo()).check(matches(isDisplayed()))
    }

    @Test
    fun signUpScreen_fullNameFieldHasCorrectHint() {
        onView(withId(R.id.etFullName)).check(matches(withHint("Your full name")))
    }

    @Test
    fun signUpScreen_emailFieldHasCorrectHint() {
        onView(withId(R.id.etEmail)).check(matches(withHint("your@email.com")))
    }

    @Test
    fun signUpScreen_phoneFieldHasCorrectHint() {
        onView(withId(R.id.etPhone)).perform(scrollTo()).check(matches(withHint("Numbers only")))
    }

    @Test
    fun signUpScreen_passwordFieldHasCorrectHint() {
        onView(withId(R.id.etPassword)).perform(scrollTo()).check(matches(withHint("Min. 6 characters")))
    }

    @Test
    fun signUpScreen_confirmPasswordFieldHasCorrectHint() {
        onView(withId(R.id.etConfirmPassword)).perform(scrollTo()).check(matches(withHint("Re-enter your password")))
    }

    @Test
    fun signUpScreen_signUpButtonHasCorrectText() {
        onView(withId(R.id.btnSignUp)).perform(scrollTo()).check(matches(withText("Create Account")))
    }

    @Test
    fun signUpScreen_adminSwitchIsUncheckedByDefault() {
        onView(withId(R.id.swIsAdmin)).check(matches(isNotChecked()))
    }

    @Test
    fun signUpScreen_loginLinkHasCorrectText() {
        onView(withId(R.id.tvLogin)).perform(scrollTo()).check(matches(withText("Sign In")))
    }

    @Test
    fun signUpScreen_canTypeInAllFields() {
        onView(withId(R.id.etFullName))
            .perform(typeText("John Doe"), closeSoftKeyboard())
        onView(withId(R.id.etEmail))
            .perform(typeText("john@test.com"), closeSoftKeyboard())
        onView(withId(R.id.etPhone))
            .perform(typeText("15143334444"), closeSoftKeyboard())
        onView(withId(R.id.etPassword))
            .perform(typeText("pass123"), closeSoftKeyboard())
        onView(withId(R.id.etConfirmPassword))
            .perform(typeText("pass123"), closeSoftKeyboard())

        onView(withId(R.id.etFullName)).check(matches(withText("John Doe")))
        onView(withId(R.id.etEmail)).check(matches(withText("john@test.com")))
        onView(withId(R.id.etPhone)).check(matches(withText("15143334444")))
    }

    @Test
    fun signUpScreen_signUpButtonIsClickable() {
        onView(withId(R.id.btnSignUp)).check(matches(isClickable()))
    }

    @Test
    fun signUpScreen_allFieldsAreEnabled() {
        onView(withId(R.id.etFullName)).check(matches(isEnabled()))
        onView(withId(R.id.etEmail)).check(matches(isEnabled()))
        onView(withId(R.id.etPhone)).check(matches(isEnabled()))
        onView(withId(R.id.etPassword)).check(matches(isEnabled()))
        onView(withId(R.id.etConfirmPassword)).check(matches(isEnabled()))
    }

    @Test
    fun signUpScreen_loginLinkIsClickable() {
        onView(withId(R.id.tvLogin)).check(matches(isClickable()))
    }
}
