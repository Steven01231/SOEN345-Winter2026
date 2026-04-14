package com.example.soen345_winter2026.acceptance

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.example.soen345_winter2026.R
import com.google.firebase.auth.FirebaseAuth
import org.hamcrest.Matcher

/**
 * Shared helpers for acceptance Espresso tests.
 *
 * These tests are designed to be **recorded as GIFs**, so each user-visible action
 * is followed by [pace] to make the recording readable. They hit the real Firebase
 * project — make sure the device has Wi-Fi and the test credentials are valid.
 */
object AcceptanceCreds {
    const val USER_EMAIL = "loco.hlm2008@gmail.com"
    const val USER_PASSWORD = "Soen345@"
    const val ADMIN_EMAIL = "admin1@gmail.com"
    const val ADMIN_PASSWORD = "Soen345@"
}

/** Sleep so the GIF viewer can follow each step. */
fun pace(ms: Long = 500L) {
    try {
        Thread.sleep(ms)
    } catch (_: InterruptedException) { /* ignore */ }
}

/** Wait for an async Firebase operation to settle. Generous so tests don't flake on slow networks. */
fun waitForNetwork(ms: Long = 3500L) = pace(ms)

/** Sign out so each test starts from a clean Auth state. */
fun signOut() {
    FirebaseAuth.getInstance().signOut()
}

/** Fill the login form on `LogInActivity` and tap the customer Sign In button. */
fun loginAsCustomer() {
    onView(withId(R.id.etEmailPhone))
        .perform(scrollTo(), clearText(), typeText(AcceptanceCreds.USER_EMAIL), closeSoftKeyboard())
    pace()
    onView(withId(R.id.etPassword))
        .perform(scrollTo(), clearText(), typeText(AcceptanceCreds.USER_PASSWORD), closeSoftKeyboard())
    pace()
    onView(withId(R.id.btnLogin)).perform(scrollTo(), click())
    waitForNetwork()
}

/** Fill the login form on `LogInActivity` and tap the **Admin Sign In** button. */
fun loginAsAdmin() {
    onView(withId(R.id.etEmailPhone))
        .perform(scrollTo(), clearText(), typeText(AcceptanceCreds.ADMIN_EMAIL), closeSoftKeyboard())
    pace()
    onView(withId(R.id.etPassword))
        .perform(scrollTo(), clearText(), typeText(AcceptanceCreds.ADMIN_PASSWORD), closeSoftKeyboard())
    pace()
    onView(withId(R.id.adminBtnLogin)).perform(scrollTo(), click())
    waitForNetwork()
}

/**
 * Open the date picker on `etDate` and accept today's default date.
 *
 * The `etDate` field intercepts clicks to show a `DatePickerDialog`, so we cannot type
 * into it. This helper performs the user-visible flow: tap the field, then tap **OK**.
 */
fun pickTodayInDatePicker() {
    onView(withId(R.id.etDate)).perform(scrollTo(), click())
    pace(800)
    onView(withId(android.R.id.button1)).perform(click()) // "OK"
    pace()
}

/**
 * ViewAction that scrolls a RecyclerView to [position] and clicks the descendant
 * with id [childId] on that item. Avoids the espresso-contrib dependency.
 */
fun clickRecyclerChild(position: Int, childId: Int): ViewAction = object : ViewAction {
    override fun getConstraints(): Matcher<View> = isAssignableFrom(RecyclerView::class.java)
    override fun getDescription(): String = "click child id=$childId on recycler position $position"
    override fun perform(uiController: UiController, view: View) {
        val recycler = view as RecyclerView
        recycler.scrollToPosition(position)
        uiController.loopMainThreadUntilIdle()
        val holder = recycler.findViewHolderForAdapterPosition(position)
            ?: error("No view holder at position $position")
        val child = holder.itemView.findViewById<View>(childId)
            ?: error("No child with id=$childId in view holder at position $position")
        child.performClick()
        uiController.loopMainThreadUntilIdle()
    }
}
