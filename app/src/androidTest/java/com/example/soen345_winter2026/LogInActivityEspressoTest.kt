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
class LogInActivityEspressoTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LogInActivity::class.java)

    @Test
    fun loginScreen_displaysAllUIElements() {
        onView(withId(R.id.etEmailPhone)).check(matches(isDisplayed()))
        onView(withId(R.id.etPassword)).check(matches(isDisplayed()))
        onView(withId(R.id.btnLogin)).check(matches(isDisplayed()))
        onView(withId(R.id.adminBtnLogin)).check(matches(isDisplayed()))
        onView(withId(R.id.btnSignUp)).check(matches(isDisplayed()))
    }

    @Test
    fun loginScreen_emailFieldHasCorrectHint() {
        onView(withId(R.id.etEmailPhone)).check(matches(withHint("Email or phone (if phone, numbers only)")))
    }

    @Test
    fun loginScreen_passwordFieldHasCorrectHint() {
        onView(withId(R.id.etPassword)).check(matches(withHint("Password")))
    }

    @Test
    fun loginScreen_loginButtonHasCorrectText() {
        onView(withId(R.id.btnLogin)).check(matches(withText("Login")))
    }

    @Test
    fun loginScreen_adminLoginButtonHasCorrectText() {
        onView(withId(R.id.adminBtnLogin)).check(matches(withText("Admin Login")))
    }

    @Test
    fun loginScreen_signUpLinkHasCorrectText() {
        onView(withId(R.id.btnSignUp)).check(matches(withText("Don't have an account? Sign Up")))
    }

    @Test
    fun loginScreen_canTypeInEmailField() {
        onView(withId(R.id.etEmailPhone))
            .perform(typeText("test@test.com"), closeSoftKeyboard())
        onView(withId(R.id.etEmailPhone)).check(matches(withText("test@test.com")))
    }

    @Test
    fun loginScreen_canTypePhoneInEmailField() {
        onView(withId(R.id.etEmailPhone))
            .perform(typeText("15143334444"), closeSoftKeyboard())
        onView(withId(R.id.etEmailPhone)).check(matches(withText("15143334444")))
    }

    @Test
    fun loginScreen_canTypeInPasswordField() {
        onView(withId(R.id.etPassword))
            .perform(typeText("password123"), closeSoftKeyboard())
        onView(withId(R.id.etPassword)).check(matches(withText("password123")))
    }

    @Test
    fun loginScreen_clickSignUpNavigatesToSignUpScreen() {
        onView(withId(R.id.btnSignUp)).perform(click())
        onView(withId(R.id.etFullName)).check(matches(isDisplayed()))
    }

    @Test
    fun loginScreen_loginButtonIsClickable() {
        onView(withId(R.id.btnLogin)).check(matches(isClickable()))
    }

    @Test
    fun loginScreen_adminLoginButtonIsClickable() {
        onView(withId(R.id.adminBtnLogin)).check(matches(isClickable()))
    }

    @Test
    fun loginScreen_emailFieldIsEnabled() {
        onView(withId(R.id.etEmailPhone)).check(matches(isEnabled()))
    }

    @Test
    fun loginScreen_passwordFieldIsEnabled() {
        onView(withId(R.id.etPassword)).check(matches(isEnabled()))
    }

    @Test
    fun loginScreen_forgotPasswordLinkIsDisplayed() {
        onView(withId(R.id.tvForgotPassword)).check(matches(isDisplayed()))
    }

    @Test
    fun loginScreen_forgotPasswordLinkHasCorrectText() {
        onView(withId(R.id.tvForgotPassword)).check(matches(withText("Forgot password?")))
    }

    @Test
    fun loginScreen_forgotPasswordLinkIsClickable() {
        onView(withId(R.id.tvForgotPassword)).check(matches(isClickable()))
    }
}
