package com.example.soen345_winter2026.utils

/**
 * Sample utility class for validation
 * Used to demonstrate unit testing
 */
object Validator {

    /**
     * Validates email format
     * @param email The email string to validate
     * @return true if email is valid, false otherwise
     */
    fun isValidEmail(email: String): Boolean {
        if (email.isBlank()) return false

        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        return email.matches(emailRegex.toRegex())
    }

    /**
     * Validates password strength
     * Password must be at least 8 characters with at least one letter and one number
     * @param password The password string to validate
     * @return true if password is valid, false otherwise
     */
    fun isValidPassword(password: String): Boolean {
        if (password.length < 8) return false

        val hasLetter = password.any { it.isLetter() }
        val hasDigit = password.any { it.isDigit() }

        return hasLetter && hasDigit
    }

    /**
     * Validates phone number format (simple validation)
     * @param phone The phone number string to validate
     * @return true if phone is valid, false otherwise
     */
    fun isValidPhoneNumber(phone: String): Boolean {
        if (phone.isBlank()) return false

        // Simple validation: 10-15 digits, can have spaces, dashes, parentheses, or plus sign
        val cleanPhone = phone.replace(Regex("[\\s\\-()+ ]"), "")
        return cleanPhone.length in 10..15 && cleanPhone.all { it.isDigit() }
    }

    /**
     * Validates event capacity
     * @param capacity The capacity value to validate
     * @return true if capacity is valid (positive number), false otherwise
     */
    fun isValidCapacity(capacity: Int): Boolean {
        return capacity > 0
    }

    /**
     * Validates that a string is not empty
     * @param text The string to validate
     * @return true if text is not blank, false otherwise
     */
    fun isNotEmpty(text: String): Boolean {
        return text.isNotBlank()
    }
}
