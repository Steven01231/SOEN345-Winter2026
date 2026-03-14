# SOEN345-Winter2026

[![Android CI](https://github.com/Steven01231/SOEN345-Winter2026/actions/workflows/android-ci.yml/badge.svg)](https://github.com/Steven01231/SOEN345-Winter2026/actions/workflows/android-ci.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Steven01231_SOEN345-Winter2026&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Steven01231_SOEN345-Winter2026)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=Steven01231_SOEN345-Winter2026&metric=coverage)](https://sonarcloud.io/summary/new_code?id=Steven01231_SOEN345-Winter2026)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=Steven01231_SOEN345-Winter2026&metric=bugs)](https://sonarcloud.io/summary/new_code?id=Steven01231_SOEN345-Winter2026)

## Team
**SOEN 345 Project Team**

| Name | Student ID | Github Username |
|------|-------------|----------------|
| Steven Dy | 40283742 | Steven01231 |
| Aidana Abdybaeva | 40281501 | xidxnx |
| Yayi Chen | 40286042 | afkCYa |
| Abdeljalil Sennaoui | 40117162 | abdeljalilsennaoui |

## Setup

### Android Studio Configuration
This project uses Android Studio as a platform to run the project.

To access the project:
1. Clone the repository from GitHub Desktop or GitBash
2. Open Android Studio → Open Project → Enter Project Repository (generally in GitHub folder)

### Firebase Configuration
This project uses Firebase for authentication and database. You need to configure Firebase locally:

1. See [Firebase Setup Guide](docs/FIREBASE_SETUP.md) for detailed instructions
2. Download `google-services.json` from Firebase Console
3. Place it in the `app/` directory
4. The file is gitignored and must be added by each team member

**Note:** The app will not build without `google-services.json`. Follow the setup guide to obtain this file.

## CI/CD

Every push to `master` and every pull request triggers the GitHub Actions pipeline which:
- Builds the debug APK
- Runs all unit tests
- Generates a JaCoCo code coverage report
- Runs SonarCloud quality analysis
- Runs instrumented (UI) tests on an Android emulator

### Running tests locally

**Unit tests:**
```bash
./gradlew testDebugUnitTest
```

**Unit tests + coverage report:**
```bash
./gradlew jacocoTestReport
# Report generated at: app/build/reports/jacoco/jacocoTestReport/html/index.html
```

**Instrumented tests (requires emulator or device):**
```bash
./gradlew connectedDebugAndroidTest
```

**SonarQube analysis locally:**
```bash
./gradlew sonar -Dsonar.token=YOUR_TOKEN
```

## GitHub Secrets Required

| Secret | Description |
|--------|-------------|
| `SONAR_TOKEN` | SonarCloud authentication token |
| `GOOGLE_SERVICES_JSON` | Contents of `google-services.json` (base64 or raw JSON) |
