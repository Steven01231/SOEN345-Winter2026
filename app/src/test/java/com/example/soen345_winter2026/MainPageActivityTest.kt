package com.example.soen345_winter2026

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * Unit tests for MainPageActivity
 */
@DisplayName("MainPageActivity Tests")
class MainPageActivityTest {

    @Test
    @DisplayName("Should have MainPageActivity class")
    fun `MainPageActivity exists`() {
        assertThat(MainPageActivity::class).isNotNull()
    }

    @Test
    @DisplayName("Should be a ComponentActivity subclass")
    fun `MainPageActivity is ComponentActivity`() {
        val isComponentActivity = androidx.activity.ComponentActivity::class.java.isAssignableFrom(MainPageActivity::class.java)
        assertThat(isComponentActivity).isTrue()
    }

    @Test
    @DisplayName("Should have onCreate method")
    fun `MainPageActivity has onCreate method`() {
        val hasOnCreate = MainPageActivity::class.java.declaredMethods.any { it.name == "onCreate" }
        assertThat(hasOnCreate).isTrue()
    }

    @Test
    @DisplayName("Should have binding")
    fun `MainPageActivity has binding property`() {
        val hasBinding = MainPageActivity::class.java.declaredFields.any { it.name.contains("binding", ignoreCase = true) }
        assertThat(hasBinding).isTrue()
    }
}