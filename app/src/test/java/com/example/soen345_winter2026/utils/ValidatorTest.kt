package com.example.soen345_winter2026.utils

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

/**
 * Unit tests for Validator utility class
 * Demonstrates JUnit 5 features and testing best practices
 */
@DisplayName("Validator Tests")
class ValidatorTest {

    @Nested
    @DisplayName("Email Validation Tests")
    inner class EmailValidationTests {

        @Test
        @DisplayName("Should return true for valid email addresses")
        fun `valid email returns true`() {
            // Given
            val validEmail = "test@example.com"

            // When
            val result = Validator.isValidEmail(validEmail)

            // Then
            assertTrue(result)
            // Alternative using Truth for more readable assertions
            assertThat(result).isTrue()
        }

        @ParameterizedTest(name = "Email: {0} should be valid")
        @ValueSource(strings = [
            "user@example.com",
            "test.user@example.com",
            "user+tag@example.co.uk",
            "user_name@example-domain.com"
        ])
        fun `valid email formats should return true`(email: String) {
            assertThat(Validator.isValidEmail(email)).isTrue()
        }

        @ParameterizedTest(name = "Email: {0} should be invalid")
        @ValueSource(strings = [
            "",
            "   ",
            "invalid",
            "@example.com",
            "user@",
            "user@.com",
            "user @example.com",
            "user@example"
        ])
        fun `invalid email formats should return false`(email: String) {
            assertThat(Validator.isValidEmail(email)).isFalse()
        }

        @Test
        @DisplayName("Should return false for blank email")
        fun `blank email returns false`() {
            assertFalse(Validator.isValidEmail(""))
            assertFalse(Validator.isValidEmail("   "))
        }
    }

    @Nested
    @DisplayName("Password Validation Tests")
    inner class PasswordValidationTests {

        @Test
        @DisplayName("Should return true for valid password")
        fun `valid password returns true`() {
            // Given
            val validPassword = "password123"

            // When
            val result = Validator.isValidPassword(validPassword)

            // Then
            assertThat(result).isTrue()
        }

        @ParameterizedTest(name = "Password: {0} should be valid")
        @ValueSource(strings = [
            "password1",
            "12345678a",
            "Test1234",
            "MySecurePass99"
        ])
        fun `valid password formats should return true`(password: String) {
            assertThat(Validator.isValidPassword(password)).isTrue()
        }

        @ParameterizedTest(name = "Password: {0} should be invalid")
        @ValueSource(strings = [
            "",
            "short1",        // too short
            "12345678",      // no letters
            "password",      // no digits
            "Pass1"          // too short
        ])
        fun `invalid password formats should return false`(password: String) {
            assertThat(Validator.isValidPassword(password)).isFalse()
        }

        @Test
        @DisplayName("Should return false for password with less than 8 characters")
        fun `password with less than 8 characters returns false`() {
            assertFalse(Validator.isValidPassword("Test12"))
        }

        @Test
        @DisplayName("Should return false for password without letters")
        fun `password without letters returns false`() {
            assertFalse(Validator.isValidPassword("12345678"))
        }

        @Test
        @DisplayName("Should return false for password without digits")
        fun `password without digits returns false`() {
            assertFalse(Validator.isValidPassword("password"))
        }
    }

    @Nested
    @DisplayName("Phone Number Validation Tests")
    inner class PhoneValidationTests {

        @Test
        @DisplayName("Should return true for valid phone number")
        fun `valid phone number returns true`() {
            assertThat(Validator.isValidPhoneNumber("1234567890")).isTrue()
        }

        @ParameterizedTest(name = "Phone: {0} should be valid")
        @ValueSource(strings = [
            "1234567890",
            "123-456-7890",
            "(123) 456-7890",
            "123 456 7890",
            "+1234567890",
            "12345678901234"  // 14 digits
        ])
        fun `valid phone formats should return true`(phone: String) {
            assertThat(Validator.isValidPhoneNumber(phone)).isTrue()
        }

        @ParameterizedTest(name = "Phone: {0} should be invalid")
        @ValueSource(strings = [
            "",
            "   ",
            "123",           // too short
            "1234567890123456", // too long
            "abcdefghij"     // not digits
        ])
        fun `invalid phone formats should return false`(phone: String) {
            assertThat(Validator.isValidPhoneNumber(phone)).isFalse()
        }
    }

    @Nested
    @DisplayName("Capacity Validation Tests")
    inner class CapacityValidationTests {

        @Test
        @DisplayName("Should return true for positive capacity")
        fun `positive capacity returns true`() {
            assertThat(Validator.isValidCapacity(100)).isTrue()
            assertThat(Validator.isValidCapacity(1)).isTrue()
        }

        @Test
        @DisplayName("Should return false for zero capacity")
        fun `zero capacity returns false`() {
            assertThat(Validator.isValidCapacity(0)).isFalse()
        }

        @Test
        @DisplayName("Should return false for negative capacity")
        fun `negative capacity returns false`() {
            assertThat(Validator.isValidCapacity(-1)).isFalse()
            assertThat(Validator.isValidCapacity(-100)).isFalse()
        }
    }

    @Nested
    @DisplayName("Empty String Validation Tests")
    inner class EmptyStringValidationTests {

        @Test
        @DisplayName("Should return true for non-empty string")
        fun `non-empty string returns true`() {
            assertThat(Validator.isNotEmpty("Hello")).isTrue()
        }

        @ParameterizedTest(name = "Text: '{0}' should be invalid")
        @ValueSource(strings = ["", "   ", "\t", "\n"])
        fun `blank strings should return false`(text: String) {
            assertThat(Validator.isNotEmpty(text)).isFalse()
        }
    }

    @Nested
    @DisplayName("Price Validation Tests")
    inner class PriceValidationTests {

        @ParameterizedTest(name = "Price '{0}' should be valid")
        @ValueSource(strings = ["0", "0.0", "1", "25", "25.00", "99.99", "1000"])
        fun `valid prices return true`(price: String) {
            assertThat(Validator.isValidPrice(price)).isTrue()
        }

        @ParameterizedTest(name = "Price '{0}' should be invalid")
        @ValueSource(strings = ["", "   ", "abc", "-1", "-0.01", "1.2.3", "$10"])
        fun `invalid prices return false`(price: String) {
            assertThat(Validator.isValidPrice(price)).isFalse()
        }

        @Test
        @DisplayName("Empty price string returns false")
        fun `empty price returns false`() {
            assertThat(Validator.isValidPrice("")).isFalse()
        }

        @Test
        @DisplayName("Zero price is accepted (free events)")
        fun `zero price is valid`() {
            assertThat(Validator.isValidPrice("0")).isTrue()
        }

        @Test
        @DisplayName("Negative price is rejected")
        fun `negative price is invalid`() {
            assertThat(Validator.isValidPrice("-5.00")).isFalse()
        }
    }
}
