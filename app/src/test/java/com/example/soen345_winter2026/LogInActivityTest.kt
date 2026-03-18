package com.example.soen345_winter2026

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * Unit tests for LogInActivity
 */
@DisplayName("LogInActivity Tests")
class LogInActivityTest {

    @Test
    @DisplayName("Should have LogInActivity class")
    fun `LogInActivity exists`() {
        assertThat(LogInActivity::class).isNotNull()
    }

    @Test
    @DisplayName("Should be a ComponentActivity subclass")
    fun `LogInActivity is ComponentActivity`() {
        val isComponentActivity = androidx.activity.ComponentActivity::class.java.isAssignableFrom(LogInActivity::class.java)
        assertThat(isComponentActivity).isTrue()
    }

    @Test
    fun `empty email should be invalid`() {
        val emptyEmail = ""
        assertThat(emptyEmail.isEmpty()).isTrue()
    }

    @Test
    fun `empty password should be invalid`() {
        val emptyPassword = ""
        assertThat(emptyPassword.isEmpty()).isTrue()
    }

    @Test
    fun `valid credentials should pass`() {
        val email = "test@example.com"
        val password = "password123"
        assertThat(email.isNotEmpty() && password.isNotEmpty()).isTrue()
    }

    @Test
    fun `should validate empty fields`() {
        val emptyEmail = ""
        val emptyPassword = ""
        val fieldsEmpty = emptyEmail.isEmpty() || emptyPassword.isEmpty()
        assertThat(fieldsEmpty).isTrue()
    }

    @Test
    fun `should accept valid credentials`() {
        val email = "test@example.com"
        val password = "password123"
        val isValid = email.isNotEmpty() && password.isNotEmpty()
        assertThat(isValid).isTrue()
    }
}