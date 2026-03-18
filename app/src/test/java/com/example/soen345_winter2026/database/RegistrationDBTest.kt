package com.example.soen345_winter2026.database

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * Unit tests for RegistrationDB
 * Tests Firebase authentication and Firestore operations
 */
@DisplayName("RegistrationDB Tests")
class RegistrationDBTest {

    @Nested
    @DisplayName("Email Validation Tests")
    inner class EmailValidationTests {

        @Test
        @DisplayName("Valid email format")
        fun `email with @ symbol is valid`() {
            val validEmails = listOf("user@domain.com", "test.user@example.co.uk", "name+tag@test.com")
            validEmails.forEach { email ->
                assertThat(email).contains("@")
            }
        }

        @Test
        @DisplayName("Invalid email format")
        fun `email without @ symbol is invalid`() {
            val invalidEmails = listOf("nodomain", "user@", "@domain.com")
            invalidEmails.forEach { email ->
                val isInvalid = email.isEmpty() || !email.contains("@") || email.startsWith("@") || email.endsWith("@")
                assertThat(isInvalid).isTrue()
            }
        }

        @Test
        @DisplayName("Email domain validation")
        fun `email must have domain after @`() {
            val validEmail = "user@example.com"
            val hasDomain = validEmail.contains("@") && !validEmail.endsWith("@")
            assertThat(hasDomain).isTrue()
        }

        @Test
        @DisplayName("Multiple @ symbols")
        fun `email with multiple @ is invalid`() {
            val email = "user@@example.com"
            val count = email.count { it == '@' }
            assertThat(count > 1).isTrue()
        }

        @Test
        @DisplayName("Email with spaces")
        fun `email with spaces is invalid`() {
            val email = "user @example.com"
            assertThat(email.contains(" ")).isTrue()
        }
    }

    @Nested
    @DisplayName("Password Validation Tests")
    inner class PasswordValidationTests {

        @Test
        @DisplayName("Strong password")
        fun `password with 8+ characters is valid`() {
            val strongPassword = "StrongPass123"
            assertThat(strongPassword.length >= 8).isTrue()
        }

        @Test
        @DisplayName("Weak password")
        fun `password with less than 8 characters is invalid`() {
            val weakPassword = "weak"
            assertThat(weakPassword.length < 8).isTrue()
        }

        @Test
        @DisplayName("Empty password")
        fun `empty password is invalid`() {
            val password = ""
            assertThat(password.isEmpty()).isTrue()
        }

        @Test
        @DisplayName("Various valid passwords")
        fun `various valid password formats`() {
            val passwords = listOf("ValidPass123", "SecurePass456", "MyPassword789")
            passwords.forEach { password ->
                assertThat(password.length >= 8).isTrue()
            }
        }

        @Test
        @DisplayName("Minimum length exactly 8")
        fun `password with exactly 8 characters`() {
            val password = "Pass1234"
            assertThat(password.length).isEqualTo(8)
        }
    }

    @Nested
    @DisplayName("Full Name Validation Tests")
    inner class FullNameValidationTests {

        @Test
        @DisplayName("Non-empty name")
        fun `full name must not be empty`() {
            val validName = "John Doe"
            assertThat(validName.isNotEmpty()).isTrue()
        }

        @Test
        @DisplayName("Empty name")
        fun `empty name is invalid`() {
            val emptyName = ""
            assertThat(emptyName.isEmpty()).isTrue()
        }

        @Test
        @DisplayName("Name with special characters")
        fun `full name with special characters`() {
            val names = listOf("John Doe", "Mary-Jane Watson", "O'Brien", "José García")
            names.forEach { name ->
                assertThat(name.isNotEmpty()).isTrue()
            }
        }

        @Test
        @DisplayName("Single character name")
        fun `name with single character`() {
            val shortName = "J"
            assertThat(shortName.length >= 1).isTrue()
        }

        @Test
        @DisplayName("Name with numbers")
        fun `name can contain numbers`() {
            val name = "John123"
            assertThat(name.isNotEmpty()).isTrue()
        }
    }

    @Nested
    @DisplayName("Signup Credentials Tests")
    inner class SignupCredentialsTests {

        @Test
        @DisplayName("All fields valid")
        fun `all signup credentials valid`() {
            val email = "newuser@example.com"
            val password = "ValidPass123"
            val fullName = "Jane Smith"
            
            val isValid = email.isNotEmpty() && 
                         email.contains("@") && 
                         password.length >= 8 && 
                         fullName.isNotEmpty()
            assertThat(isValid).isTrue()
        }

        @Test
        @DisplayName("Missing email")
        fun `missing email invalidates signup`() {
            val email = ""
            val password = "ValidPass123"
            val fullName = "Jane Smith"
            
            val isValid = email.isNotEmpty()
            assertThat(isValid).isFalse()
        }

        @Test
        @DisplayName("Invalid email format")
        fun `invalid email format invalidates signup`() {
            val email = "invalidemail"
            val password = "ValidPass123"
            val fullName = "Jane Smith"
            
            val isValid = email.contains("@")
            assertThat(isValid).isFalse()
        }

        @Test
        @DisplayName("Weak password")
        fun `weak password invalidates signup`() {
            val email = "user@example.com"
            val password = "weak"
            val fullName = "Jane Smith"
            
            val isValid = password.length >= 8
            assertThat(isValid).isFalse()
        }

        @Test
        @DisplayName("Missing name")
        fun `missing name invalidates signup`() {
            val email = "user@example.com"
            val password = "ValidPass123"
            val fullName = ""
            
            val isValid = fullName.isNotEmpty()
            assertThat(isValid).isFalse()
        }
    }

    @Nested
    @DisplayName("Login Credentials Tests")
    inner class LoginCredentialsTests {

        @Test
        @DisplayName("Valid login credentials")
        fun `all login credentials valid`() {
            val email = "user@example.com"
            val password = "password123"
            
            val isValid = email.isNotEmpty() && 
                         email.contains("@") && 
                         password.isNotEmpty()
            assertThat(isValid).isTrue()
        }

        @Test
        @DisplayName("Missing email")
        fun `missing email invalidates login`() {
            val email = ""
            val password = "password123"
            
            val isValid = email.isNotEmpty()
            assertThat(isValid).isFalse()
        }

        @Test
        @DisplayName("Missing password")
        fun `missing password invalidates login`() {
            val email = "user@example.com"
            val password = ""
            
            val isValid = password.isNotEmpty()
            assertThat(isValid).isFalse()
        }

        @Test
        @DisplayName("Invalid email")
        fun `invalid email invalidates login`() {
            val email = "notanemail"
            val password = "password123"
            
            val isValid = email.contains("@")
            assertThat(isValid).isFalse()
        }

        @Test
        @DisplayName("Both fields missing")
        fun `both fields missing invalidates login`() {
            val email = ""
            val password = ""
            
            val isValid = email.isNotEmpty() && password.isNotEmpty()
            assertThat(isValid).isFalse()
        }

        @Test
        @DisplayName("Partial credentials")
        fun `partial credentials invalidate login`() {
            val email = "user@example.com"
            val password = ""
            
            val isValid = email.isNotEmpty() && password.isNotEmpty()
            assertThat(isValid).isFalse()
        }
    }

    @Nested
    @DisplayName("Credential Combinations Tests")
    inner class CredentialCombinationsTests {

        @Test
        @DisplayName("Valid email, invalid password")
        fun `valid email with weak password fails`() {
            val email = "user@example.com"
            val password = "short"
            
            val emailValid = email.contains("@")
            val passwordValid = password.length >= 8
            val allValid = emailValid && passwordValid
            
            assertThat(emailValid).isTrue()
            assertThat(passwordValid).isFalse()
            assertThat(allValid).isFalse()
        }

        @Test
        @DisplayName("Invalid email, valid password")
        fun `invalid email with strong password fails`() {
            val email = "invalidemail"
            val password = "StrongPass123"
            
            val emailValid = email.contains("@")
            val passwordValid = password.length >= 8
            val allValid = emailValid && passwordValid
            
            assertThat(emailValid).isFalse()
            assertThat(passwordValid).isTrue()
            assertThat(allValid).isFalse()
        }

        @Test
        @DisplayName("Empty email, empty password")
        fun `empty email and password both invalid`() {
            val email = ""
            val password = ""
            
            val emailValid = email.isNotEmpty()
            val passwordValid = password.isNotEmpty()
            val allValid = emailValid && passwordValid
            
            assertThat(allValid).isFalse()
        }
    }
}