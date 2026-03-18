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
}