package com.example.soen345_winter2026.data

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * Unit tests for UserRepository
 * Demonstrates MockK for mocking dependencies and coroutine testing
 */
@DisplayName("UserRepository Tests")
class UserRepositoryTest {

    // Mock of the data source
    private lateinit var mockDataSource: UserDataSource

    // System under test
    private lateinit var repository: UserRepository

    @BeforeEach
    fun setup() {
        // Create mock data source
        mockDataSource = mockk()

        // Create repository with mocked dependency
        repository = UserRepository(mockDataSource)
    }

    @Nested
    @DisplayName("Get User Tests")
    inner class GetUserTests {

        @Test
        @DisplayName("Should return user when user exists")
        fun `getUser returns user when user exists`() = runTest {
            // Given
            val userId = "user123"
            val expectedUser = User(
                id = userId,
                email = "test@example.com",
                name = "Test User"
            )

            // Mock the data source to return the user
            coEvery { mockDataSource.getUserById(userId) } returns expectedUser

            // When
            val result = repository.getUser(userId)

            // Then
            assertThat(result).isEqualTo(expectedUser)

            // Verify the data source was called
            coVerify(exactly = 1) { mockDataSource.getUserById(userId) }
        }

        @Test
        @DisplayName("Should return null when user does not exist")
        fun `getUser returns null when user does not exist`() = runTest {
            // Given
            val userId = "nonexistent"

            // Mock the data source to return null
            coEvery { mockDataSource.getUserById(userId) } returns null

            // When
            val result = repository.getUser(userId)

            // Then
            assertThat(result).isNull()
            coVerify(exactly = 1) { mockDataSource.getUserById(userId) }
        }

        @Test
        @DisplayName("Should return null for blank user ID")
        fun `getUser returns null for blank ID`() = runTest {
            // When
            val result = repository.getUser("")

            // Then
            assertThat(result).isNull()

            // Verify data source was never called for invalid input
            coVerify(exactly = 0) { mockDataSource.getUserById(any()) }
        }
    }

    @Nested
    @DisplayName("Create User Tests")
    inner class CreateUserTests {

        @Test
        @DisplayName("Should save user successfully")
        fun `createUser saves user successfully`() = runTest {
            // Given
            val user = User(
                id = "user123",
                email = "test@example.com",
                name = "Test User"
            )

            // Mock successful save
            coEvery { mockDataSource.saveUser(user) } returns true

            // When
            val result = repository.createUser(user)

            // Then
            assertThat(result).isTrue()
            coVerify(exactly = 1) { mockDataSource.saveUser(user) }
        }

        @Test
        @DisplayName("Should return false when save fails")
        fun `createUser returns false when save fails`() = runTest {
            // Given
            val user = User(
                id = "user123",
                email = "test@example.com",
                name = "Test User"
            )

            // Mock failed save
            coEvery { mockDataSource.saveUser(user) } returns false

            // When
            val result = repository.createUser(user)

            // Then
            assertThat(result).isFalse()
            coVerify(exactly = 1) { mockDataSource.saveUser(user) }
        }

        @Test
        @DisplayName("Should return false for user with blank ID")
        fun `createUser returns false for blank ID`() = runTest {
            // Given
            val user = User(id = "", email = "test@example.com", name = "Test")

            // When
            val result = repository.createUser(user)

            // Then
            assertThat(result).isFalse()
            coVerify(exactly = 0) { mockDataSource.saveUser(any()) }
        }

        @Test
        @DisplayName("Should return false for user with blank email")
        fun `createUser returns false for blank email`() = runTest {
            // Given
            val user = User(id = "user123", email = "", name = "Test")

            // When
            val result = repository.createUser(user)

            // Then
            assertThat(result).isFalse()
            coVerify(exactly = 0) { mockDataSource.saveUser(any()) }
        }

        @Test
        @DisplayName("Should return false for user with blank name")
        fun `createUser returns false for blank name`() = runTest {
            // Given
            val user = User(id = "user123", email = "test@example.com", name = "")

            // When
            val result = repository.createUser(user)

            // Then
            assertThat(result).isFalse()
            coVerify(exactly = 0) { mockDataSource.saveUser(any()) }
        }
    }

    @Nested
    @DisplayName("Remove User Tests")
    inner class RemoveUserTests {

        @Test
        @DisplayName("Should delete user successfully")
        fun `removeUser deletes user successfully`() = runTest {
            // Given
            val userId = "user123"

            // Mock successful delete
            coEvery { mockDataSource.deleteUser(userId) } returns true

            // When
            val result = repository.removeUser(userId)

            // Then
            assertThat(result).isTrue()
            coVerify(exactly = 1) { mockDataSource.deleteUser(userId) }
        }

        @Test
        @DisplayName("Should return false when delete fails")
        fun `removeUser returns false when delete fails`() = runTest {
            // Given
            val userId = "user123"

            // Mock failed delete
            coEvery { mockDataSource.deleteUser(userId) } returns false

            // When
            val result = repository.removeUser(userId)

            // Then
            assertThat(result).isFalse()
            coVerify(exactly = 1) { mockDataSource.deleteUser(userId) }
        }

        @Test
        @DisplayName("Should return false for blank user ID")
        fun `removeUser returns false for blank ID`() = runTest {
            // When
            val result = repository.removeUser("")

            // Then
            assertThat(result).isFalse()
            coVerify(exactly = 0) { mockDataSource.deleteUser(any()) }
        }
    }
}
