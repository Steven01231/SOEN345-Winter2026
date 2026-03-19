package com.example.soen345_winter2026

import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
class MainPageActivityTest {

    @Test
    fun `onCreate should initialize binding and set content view`() {
        val activity = Robolectric.buildActivity(MainPageActivity::class.java)
            .setup()
            .get()

        assertNotNull("Activity should be initialized", activity)

        val content = activity.findViewById<android.view.View>(android.R.id.content)
        assertNotNull("Content view should not be null", content)
    }
}