package com.example.soen345_winter2026.data

/**
 * Sample data models and repository
 * Used to demonstrate mocking and testing with dependencies
 */

data class User(
    val id: String,
    val email: String,
    val name: String
)

/**
 * Interface for user data source
 * This will be mocked in tests
 */
interface UserDataSource {
    suspend fun getUserById(id: String): User?
    suspend fun saveUser(user: User): Boolean
    suspend fun deleteUser(id: String): Boolean
}

/**
 * Repository for managing user data
 * Demonstrates dependency injection for testability
 */
class UserRepository(
    private val dataSource: UserDataSource
) {

    /**
     * Get user by ID
     * @param id The user ID
     * @return User if found, null otherwise
     */
    suspend fun getUser(id: String): User? {
        if (id.isBlank()) return null
        return dataSource.getUserById(id)
    }

    /**
     * Save a new user
     * @param user The user to save
     * @return true if saved successfully, false otherwise
     */
    suspend fun createUser(user: User): Boolean {
        // Validation
        if (user.id.isBlank() || user.email.isBlank() || user.name.isBlank()) {
            return false
        }

        return dataSource.saveUser(user)
    }

    /**
     * Delete a user
     * @param id The user ID to delete
     * @return true if deleted successfully, false otherwise
     */
    suspend fun removeUser(id: String): Boolean {
        if (id.isBlank()) return false
        return dataSource.deleteUser(id)
    }
}
