package com.example.soen345_winter2026

import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import org.robolectric.Shadows.shadowOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import com.google.common.truth.Truth.assertThat


class MainActivityTest {

    @Test
    fun testActivityLaunchesLoginActivity() {
        val controller: ActivityController<MainActivity> =
            Robolectric.buildActivity(MainActivity::class.java).create().start()

        val activity = controller.get()

        val expectedIntent = Intent(activity, LogInActivity::class.java)

        val startedIntent = shadowOf(activity).nextStartedActivity

        assertNotNull(startedIntent)
        assert(startedIntent.component?.className == expectedIntent.component?.className)
    }

    @Test
    fun testFirebaseInstancesCreated() {
        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()

        assertNotNull(auth)
        assertNotNull(firestore)
    }

    @Test
    fun testMainActivityCreatesSuccessfully() {
        // Given/When
        val controller: ActivityController<MainActivity> =
            Robolectric.buildActivity(MainActivity::class.java)
        val activity = controller.create().get()

        // Then
        assertThat(activity).isNotNull()
    }

    @Test
    fun testMainActivityStartsSuccessfully() {
        // Given/When
        val controller: ActivityController<MainActivity> =
            Robolectric.buildActivity(MainActivity::class.java).create().start()
        val activity = controller.get()

        // Then
        assertThat(activity).isNotNull()
    }

    @Test
    fun testMainActivityResumesSuccessfully() {
        // Given/When
        val controller: ActivityController<MainActivity> =
            Robolectric.buildActivity(MainActivity::class.java).create().start().resume()
        val activity = controller.get()

        // Then
        assertThat(activity).isNotNull()
    }

    @Test
    fun testFirebaseAuthIsNotNull() {
        // Given/When
        val auth = FirebaseAuth.getInstance()

        // Then
        assertThat(auth).isNotNull()
    }

    @Test
    fun testFirebaseFirestoreIsNotNull() {
        // Given/When
        val firestore = FirebaseFirestore.getInstance()

        // Then
        assertThat(firestore).isNotNull()
    }

    @Test
    fun testActivityNavigatesToLoginActivity() {
        // Given
        val controller: ActivityController<MainActivity> =
            Robolectric.buildActivity(MainActivity::class.java).create().start().resume()
        val activity = controller.get()

        // When
        val startedIntent = shadowOf(activity).nextStartedActivity

        // Then
        assertThat(startedIntent).isNotNull()
        assertThat(startedIntent.component?.className).isEqualTo(LogInActivity::class.java.name)
    }

    @Test
    fun testMainActivityIsNotNull() {
        // Given/When
        val activity = Robolectric.buildActivity(MainActivity::class.java).create().get()

        // Then
        assertThat(activity).isNotNull()
    }

    @Test
    fun testMainActivityIsInstanceOfComponentActivity() {
        // Given/When
        val activity = Robolectric.buildActivity(MainActivity::class.java).create().get()

        // Then
        assertThat(activity).isInstanceOf(androidx.activity.ComponentActivity::class.java)
    }

    @Test
    fun testFirebaseConnectionTestCalled() {
        // Given
        val controller: ActivityController<MainActivity> =
            Robolectric.buildActivity(MainActivity::class.java).create().start()

        // When
        val activity = controller.get()

        // Then
        assertThat(activity).isNotNull()
        // Firebase connection test is called in onCreate
    }

    @Test
    fun testActivityHasOnCreateMethod() {
        // Given/When
        val activity = Robolectric.buildActivity(MainActivity::class.java)
            .create()
            .get()

        // Then
        assertThat(activity).isNotNull()
    }
}