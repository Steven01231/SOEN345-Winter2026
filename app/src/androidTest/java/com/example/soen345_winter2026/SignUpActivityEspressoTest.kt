package com.example.soen345_winter2026

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.soen345_winter2026.R
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
        onView(withId(R.id.etPassword)).check(matches(isDisplayed()))
        onView(withId(R.id.etConfirmPassword)).check(matches(isDisplayed()))
        onView(withId(R.id.btnSignUp)).check(matches(isDisplayed()))
        onView(withId(R.id.tvLogin)).check(matches(isDisplayed()))
    }

    @Test
    fun signUpScreen_fullNameFieldHasCorrectHint() {
        onView(withId(R.id.etFullName)).check(matches(withHint("Full Name")))
    }

    @Test
    fun signUpScreen_emailFieldHasCorrectHint() {
        onView(withId(R.id.etEmail)).check(matches(withHint("Email")))
    }

    @Test
    fun signUpScreen_passwordFieldHasCorrectHint() {
        onView(withId(R.id.etPassword)).check(matches(withHint("Password")))
    }

    @Test
    fun signUpScreen_confirmPasswordFieldHasCorrectHint() {
        onView(withId(R.id.etConfirmPassword)).check(matches(withHint("Confirm Password")))
    }

    @Test
    fun signUpScreen_signUpButtonHasCorrectText() {
        onView(withId(R.id.btnSignUp)).check(matches(withText("Sign Up")))
    }

    @Test
    fun signUpScreen_loginLinkHasCorrectText() {
        onView(withId(R.id.tvLogin)).check(matches(withText("Already have an account? Login")))
    }

    @Test
    fun signUpScreen_canTypeInAllFields() {
        onView(withId(R.id.etFullName))
            .perform(typeText("John Doe"), closeSoftKeyboard())
        onView(withId(R.id.etEmail))
            .perform(typeText("john@test.com"), closeSoftKeyboard())
        onView(withId(R.id.etPassword))
            .perform(typeText("pass123"), closeSoftKeyboard())
        onView(withId(R.id.etConfirmPassword))
            .perform(typeText("pass123"), closeSoftKeyboard())

        onView(withId(R.id.etFullName)).check(matches(withText("John Doe")))
        onView(withId(R.id.etEmail)).check(matches(withText("john@test.com")))
    }

    @Test
    fun signUpScreen_signUpButtonIsClickable() {
        onView(withId(R.id.btnSignUp)).check(matches(isClickable()))
    }

    @Test
    fun signUpScreen_allFieldsAreEnabled() {
        onView(withId(R.id.etFullName)).check(matches(isEnabled()))
        onView(withId(R.id.etEmail)).check(matches(isEnabled()))
        onView(withId(R.id.etPassword)).check(matches(isEnabled()))
        onView(withId(R.id.etConfirmPassword)).check(matches(isEnabled()))
    }

    @Test
    fun signUpScreen_loginLinkIsClickable() {
        onView(withId(R.id.tvLogin)).check(matches(isClickable()))
    }
}
