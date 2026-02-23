# Testing Conventions and Guidelines

## Overview
This document outlines the testing standards and conventions for the SOEN345 Ticket Booking System project. Following these guidelines ensures consistent, maintainable, and high-quality test coverage.

---

## Testing Framework Stack

### Unit Testing
- **JUnit 5 (Jupiter)**: Primary testing framework for unit tests
- **JUnit 4**: Included for compatibility with Android libraries
- **MockK**: Primary mocking library for Kotlin (preferred)
- **Mockito**: Alternative mocking library (Java compatibility)
- **Google Truth**: Assertion library for cleaner, more readable assertions
- **Kotlin Coroutines Test**: Testing utilities for coroutines
- **Turbine**: Testing library for Kotlin Flows

### Instrumented/UI Testing
- **Espresso**: UI testing framework
- **AndroidX Test**: Android testing utilities
- **Compose Testing**: Jetpack Compose UI testing

---

## Directory Structure

```
app/src/
  ├── main/java/               # Production code
  ├── test/java/               # Unit tests (JVM, no Android dependencies)
  └── androidTest/java/        # Instrumented tests (Android device/emulator)
```

**Rule**: Unit tests go in `test/`, instrumented tests go in `androidTest/`

---

## Unit Testing Best Practices

### 1. Test Naming Convention

Use descriptive test names that explain WHAT is being tested and WHAT the expected outcome is.

**Format**: `` `function name should expected behavior when condition` ``

**Examples**:
```kotlin
@Test
fun `isValidEmail should return true for valid email`()

@Test
fun `getUser should return null when user does not exist`()

@Test
fun `createUser should return false when email is blank`()
```

**Alternative Format** (for compatibility):
```kotlin
@Test
fun isValidEmail_returnsTrue_forValidEmail()
```

### 2. Test Structure - AAA Pattern

Structure all tests using the **Arrange-Act-Assert** pattern:

```kotlin
@Test
fun `example test`() {
    // Arrange (Given) - Set up test data and conditions
    val input = "test@example.com"

    // Act (When) - Execute the function/method being tested
    val result = Validator.isValidEmail(input)

    // Assert (Then) - Verify the outcome
    assertThat(result).isTrue()
}
```

### 3. Use @Nested for Grouping

Group related tests using `@Nested` inner classes:

```kotlin
@DisplayName("Validator Tests")
class ValidatorTest {

    @Nested
    @DisplayName("Email Validation Tests")
    inner class EmailValidationTests {
        @Test
        fun `valid email returns true`() { ... }

        @Test
        fun `invalid email returns false`() { ... }
    }

    @Nested
    @DisplayName("Password Validation Tests")
    inner class PasswordValidationTests {
        @Test
        fun `valid password returns true`() { ... }
    }
}
```

### 4. Parameterized Tests

Use `@ParameterizedTest` for testing multiple inputs:

```kotlin
@ParameterizedTest(name = "Email: {0} should be valid")
@ValueSource(strings = [
    "user@example.com",
    "test.user@example.com",
    "user+tag@example.co.uk"
])
fun `valid email formats should return true`(email: String) {
    assertThat(Validator.isValidEmail(email)).isTrue()
}
```

### 5. Mocking with MockK

Use MockK for mocking dependencies:

```kotlin
class RepositoryTest {
    private lateinit var mockDataSource: DataSource
    private lateinit var repository: Repository

    @BeforeEach
    fun setup() {
        mockDataSource = mockk()
        repository = Repository(mockDataSource)
    }

    @Test
    fun `test with mock`() = runTest {
        // Given
        coEvery { mockDataSource.getData() } returns expectedData

        // When
        val result = repository.getData()

        // Then
        assertThat(result).isEqualTo(expectedData)
        coVerify(exactly = 1) { mockDataSource.getData() }
    }
}
```

### 6. Testing Coroutines

Use `runTest` from `kotlinx-coroutines-test`:

```kotlin
@Test
fun `test suspend function`() = runTest {
    val result = repository.fetchData()
    assertThat(result).isNotNull()
}
```

### 7. Assertions - Google Truth

Prefer Google Truth for cleaner, more readable assertions:

```kotlin
// Truth (preferred)
assertThat(result).isTrue()
assertThat(user).isNotNull()
assertThat(list).hasSize(3)
assertThat(email).contains("@")

// JUnit (alternative)
assertTrue(result)
assertNotNull(user)
assertEquals(3, list.size)
```

---

## Code Coverage Requirements

- **Minimum Coverage**: 80% for all production code
- **Target Coverage**: 90%+ for critical business logic

### What to Test:
✅ Business logic (validation, calculations, transformations)
✅ Repository methods
✅ ViewModels
✅ Utility functions
✅ Edge cases and error handling

### What NOT to Test:
❌ Framework code (Android SDK, Firebase SDK)
❌ Simple data classes (unless they have business logic)
❌ Trivial getters/setters
❌ UI code (test with instrumented tests instead)

---

## Test Organization

### File Naming
- Test file name = Class name + "Test"
- Example: `Validator.kt` → `ValidatorTest.kt`

### Package Structure
- Mirror production code package structure
- Example:
  - Production: `com.example.app.utils.Validator`
  - Test: `com.example.app.utils.ValidatorTest`

---

## Running Tests

### From Command Line
```bash
# Run all unit tests
./gradlew test

# Run tests for specific module
./gradlew app:test

# Run tests with coverage report
./gradlew testDebugUnitTest jacocoTestReport

# View coverage report
open app/build/reports/jacoco/jacocoTestReport/html/index.html
```

### From Android Studio
- Right-click on test class/package → "Run Tests"
- Right-click on test class/package → "Run with Coverage"

---

## Common Testing Scenarios

### Testing Validation Logic
```kotlin
@Test
fun `should validate email correctly`() {
    // Valid cases
    assertThat(Validator.isValidEmail("user@example.com")).isTrue()

    // Invalid cases
    assertThat(Validator.isValidEmail("")).isFalse()
    assertThat(Validator.isValidEmail("invalid")).isFalse()
}
```

### Testing Repository with Mocks
```kotlin
@Test
fun `should fetch user from data source`() = runTest {
    // Given
    val expectedUser = User("1", "test@example.com", "Test")
    coEvery { dataSource.getUser("1") } returns expectedUser

    // When
    val result = repository.getUser("1")

    // Then
    assertThat(result).isEqualTo(expectedUser)
    coVerify { dataSource.getUser("1") }
}
```

### Testing Error Handling
```kotlin
@Test
fun `should return null when exception occurs`() = runTest {
    // Given
    coEvery { dataSource.getUser(any()) } throws Exception("Network error")

    // When
    val result = repository.getUser("1")

    // Then
    assertThat(result).isNull()
}
```

---

## CI/CD Integration

All tests run automatically on:
- Every push to any branch
- Every pull request
- Before merging to master

**Quality Gates**:
- All tests must pass ✅
- Code coverage must be ≥80% ✅
- SonarQube quality gate must pass ✅

---

## Documentation Requirements

### Test Documentation
Each test class should have:
1. Class-level documentation explaining what is being tested
2. Descriptive test names
3. Comments for complex test scenarios

```kotlin
/**
 * Unit tests for UserRepository
 * Tests user CRUD operations with mocked data source
 */
@DisplayName("UserRepository Tests")
class UserRepositoryTest {
    // Tests here
}
```

---

## Resources

### Official Documentation
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [MockK Documentation](https://mockk.io/)
- [Google Truth](https://truth.dev/)
- [Kotlin Coroutines Test](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-test/)

### Examples
- See `ValidatorTest.kt` for basic unit testing examples
- See `UserRepositoryTest.kt` for mocking and coroutine testing examples

---

## Questions?

If you have questions about testing or need help writing tests:
1. Check the example tests in the project
2. Review this documentation
3. Ask the team in team meetings
4. Refer to official documentation linked above

---

**Last Updated**: 2026-02-22
**Maintained By**: SOEN345 Project Team
