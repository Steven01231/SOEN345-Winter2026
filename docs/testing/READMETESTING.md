# Testing Documentation

This directory contains all testing-related documentation for the SOEN345 Ticket Booking System project.

## Contents

### [TESTING_CONVENTIONS.md](./TESTING_CONVENTIONS.md)
Comprehensive guide to testing standards, best practices, and conventions for the project.

**Topics covered:**
- Testing framework stack (JUnit 5, MockK, Truth, etc.)
- Unit testing best practices
- Test naming conventions
- AAA pattern (Arrange-Act-Assert)
- Mocking with MockK
- Testing coroutines
- Code coverage requirements
- Running tests locally and in CI/CD

---

## Quick Start

### Running Tests

```bash
# Run all unit tests
./gradlew test

# Run unit tests with coverage
./gradlew testDebugUnitTest jacocoTestReport

# Run instrumented tests (requires emulator/device)
./gradlew connectedAndroidTest
```

### Test Reports

After running tests, view reports:
- **Unit Test Results**: `app/build/reports/tests/testDebugUnitTest/index.html`
- **Coverage Report**: `app/build/reports/jacoco/jacocoTestReport/html/index.html`

### Example Tests

Check these files for examples:
- **Basic Unit Tests**: `app/src/test/.../utils/ValidatorTest.kt`
- **Mocking & Coroutines**: `app/src/test/.../data/UserRepositoryTest.kt`

---

## Testing Standards

✅ **Minimum 80% code coverage** for all features
✅ All tests must pass before merging to master
✅ Use descriptive test names (`` `function should expected behavior when condition` ``)
✅ Follow AAA pattern (Arrange-Act-Assert)
✅ Mock external dependencies with MockK
✅ Use Google Truth for assertions
✅ Group related tests with `@Nested`

---

## Directory Structure

```
docs/testing/
  ├── README.md                  # This file
  ├── TESTING_CONVENTIONS.md     # Detailed testing guidelines
  ├── test-plan.md               # Test plan (to be created)
  ├── test-cases.md              # Test case documentation (to be created)
  ├── coverage/                  # Coverage reports
  └── screenshots/               # Test execution screenshots
```

---

## CI/CD Integration

Tests run automatically on GitHub Actions for:
- Every push to any branch
- Every pull request
- Before merging to master

**Quality Gates:**
- ✅ All unit tests pass
- ✅ All instrumented tests pass
- ✅ Code coverage ≥80%
- ✅ SonarQube quality gate passes

---

## Need Help?

1. Check [TESTING_CONVENTIONS.md](./TESTING_CONVENTIONS.md) for detailed guidance
2. Review example tests in the codebase
3. Ask the team in meetings
4. Consult official documentation:
   - [JUnit 5](https://junit.org/junit5/docs/current/user-guide/)
   - [MockK](https://mockk.io/)
   - [Google Truth](https://truth.dev/)

---

**Last Updated**: 2026-02-22
