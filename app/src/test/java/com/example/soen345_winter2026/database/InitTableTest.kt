package com.example.soen345_winter2026.database

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * Unit tests for InitTable
 * Tests the empty Activity class
 */
@DisplayName("InitTable Tests")
class InitTableTest {

    @Test
    @DisplayName("Should have InitTable class")
    fun `InitTable class exists`() {
        assertThat(InitTable::class).isNotNull()
    }

    @Test
    @DisplayName("Should be a ComponentActivity subclass")
    fun `InitTable is a ComponentActivity`() {
        val isComponentActivity = androidx.activity.ComponentActivity::class.java.isAssignableFrom(InitTable::class.java)
        assertThat(isComponentActivity).isTrue()
    }
}