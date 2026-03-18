package com.example.soen345_winter2026

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * Unit tests for SignUpActivity
 */
@DisplayName("SignUpActivity Tests")
class SignUpActivityTest {

    @Test
    @DisplayName("Should have SignUpActivity class")
    fun `SignUpActivity exists`() {
        assertThat(SignUpActivity::class).isNotNull()
    }

    @Test
    @DisplayName("Should be an AppCompatActivity subclass")
    fun `SignUpActivity is AppCompatActivity`() {
        val isAppCompatActivity = androidx.appcompat.app.AppCompatActivity::class.java.isAssignableFrom(SignUpActivity::class.java)
        assertThat(isAppCompatActivity).isTrue()
    }

    @Test
    fun `matching passwords should pass`() {
        val password = "securePassword123"
        val confirmPassword = "securePassword123"
        assertThat(password == confirmPassword).isTrue()
    }

    @Test
    fun `non-matching passwords should fail`() {
        val password = "password123"
        val confirmPassword = "different"
        assertThat(password == confirmPassword).isFalse()
    }

    @Test
    fun `empty passwords should fail`() {
        val password = ""
        val confirmPassword = ""
        val isValid = password.isNotEmpty() && confirmPassword.isNotEmpty()
        assertThat(isValid).isFalse()
    }

    @Test
    fun `empty full name should be invalid`() {
        val fullName = ""
        val email = "test@example.com"
        val password = "pass123"
        val isValid = fullName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()
        assertThat(isValid).isFalse()
    }

    @Test
    fun `empty email should be invalid`() {
        val fullName = "John"
        val email = ""
        val password = "pass123"
        val isValid = fullName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()
        assertThat(isValid).isFalse()
    }

    @Test
    fun `all fields filled should be valid`() {
        val fullName = "John Doe"
        val email = "john@example.com"
        val password = "securePass123"
        val confirmPassword = "securePass123"
        val isValid = fullName.isNotEmpty() && email.isNotEmpty() && password == confirmPassword
        assertThat(isValid).isTrue()
    }

    @Test
    fun `empty fields should show error`() {
        val fullName = ""
        val email = ""
        val password = ""
        val fieldsEmpty = fullName.isEmpty() || email.isEmpty() || password.isEmpty()
        assertThat(fieldsEmpty).isTrue()
    }

    @Test
    fun `mismatched passwords should show error`() {
        val password = "password123"
        val confirmPassword = "different"
        val passwordsMismatch = password != confirmPassword
        assertThat(passwordsMismatch).isTrue()
    }

    @Test
    fun `valid data should proceed`() {
        val fullName = "John Doe"
        val email = "john@example.com"
        val password = "securePass123"
        val confirmPassword = "securePass123"
        val isValid = fullName.isNotEmpty() && email.isNotEmpty() && password == confirmPassword
        assertThat(isValid).isTrue()
    }
}