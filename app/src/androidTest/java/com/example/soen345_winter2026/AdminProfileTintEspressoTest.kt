package com.example.soen345_winter2026

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AdminProfileTintEspressoTest {

    @Test
    fun toolbar_tintedWithAdminColor_whenIsAdminExtraPassed() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val intent = Intent(context, ProfileActivity::class.java)
            .putExtra("isAdmin", true)

        ActivityScenario.launch<ProfileActivity>(intent).use { scenario ->
            scenario.onActivity { activity ->
                val toolbar = activity.findViewById<android.view.View>(R.id.toolbar)
                val expected = ContextCompat.getColor(activity, R.color.color_admin)
                val actual = (toolbar.background as ColorDrawable).color
                assertEquals(expected, actual)
            }
        }
    }

    @Test
    fun avatarBanner_tintedWithAdminColor_whenIsAdminExtraPassed() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val intent = Intent(context, ProfileActivity::class.java)
            .putExtra("isAdmin", true)

        ActivityScenario.launch<ProfileActivity>(intent).use { scenario ->
            scenario.onActivity { activity ->
                val banner = activity.findViewById<android.view.View>(R.id.avatarBanner)
                val expected = ContextCompat.getColor(activity, R.color.color_admin)
                val actual = (banner.background as ColorDrawable).color
                assertEquals(expected, actual)
            }
        }
    }

    @Test
    fun toolbar_keepsPrimaryColor_whenNoAdminExtra() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val intent = Intent(context, ProfileActivity::class.java)

        ActivityScenario.launch<ProfileActivity>(intent).use { scenario ->
            scenario.onActivity { activity ->
                val toolbar = activity.findViewById<android.view.View>(R.id.toolbar)
                val admin = ContextCompat.getColor(activity, R.color.color_admin)
                val actual = (toolbar.background as ColorDrawable).color
                org.junit.Assert.assertNotEquals(admin, actual)
            }
        }
    }
}
