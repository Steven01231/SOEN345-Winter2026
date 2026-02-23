# Firebase Setup Guide

## Overview
This guide explains how to set up Firebase configuration for local development. The `google-services.json` file is required to run the app but is excluded from version control for security reasons.

## Why is google-services.json not in the repository?
The `google-services.json` file contains sensitive Firebase project configuration and API keys. To prevent unauthorized access and protect the project, this file is added to `.gitignore` and must be obtained separately by each team member.

## Prerequisites
- You must have access to the Firebase Console for this project
- Contact the project administrator if you don't have access

## Step-by-Step Setup

### 1. Access Firebase Console
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Sign in with your authorized Google account
3. Select the **SOEN345-Winter2026** project (or the project name provided by your team lead)

### 2. Download google-services.json

**Option A: Download from Project Settings (Recommended)**
1. Click the **gear icon** (⚙️) next to "Project Overview" in the sidebar
2. Select **Project settings**
3. Scroll down to the **Your apps** section
4. Find the Android app with package name: `com.example.soen345_winter2026`
5. Click the **google-services.json** download button
6. Save the file to your computer

**Option B: Download from App Registration**
1. In the Firebase Console, go to **Project Overview**
2. Click on the Android icon or your app name
3. Scroll down and click **Download google-services.json**

### 3. Add File to Project
1. Locate the downloaded `google-services.json` file
2. Copy/move it to the `app/` directory of the project:
   ```
   SOEN345-Winter2026/
   ├── app/
   │   ├── google-services.json    <-- Place file here
   │   ├── build.gradle.kts
   │   └── src/
   ```

3. Verify the file is in the correct location:
   ```bash
   ls app/google-services.json
   ```
   You should see: `app/google-services.json`

### 4. Verify Setup

**Build the Project**
```bash
./gradlew build
```

**Run the App**
1. Open the project in Android Studio
2. Run the app on an emulator or device
3. Open Logcat (View → Tool Windows → Logcat)
4. Filter by "MainActivity"
5. Look for these success messages:
   ```
   MainActivity: Starting Firebase anonymous sign-in...
   MainActivity: Anonymous sign-in successful. UID: [user-id]
   MainActivity: Writing test document to /users/[user-id]/testData
   MainActivity: Test document written successfully. Document ID: [doc-id]
   ```

**Verify in Firebase Console**
1. Go to **Authentication → Users**: You should see your anonymous user
2. Go to **Firestore Database → users → [your-uid] → testData**: You should see the test document

## Troubleshooting

### Error: "google-services.json is missing"
- Ensure the file is in the `app/` directory (not the root directory)
- File name must be exactly `google-services.json` (case-sensitive)
- Rebuild the project: `./gradlew clean build`

### Error: "This operation is restricted to administrators only"
- Anonymous authentication may not be enabled in Firebase
- Go to Firebase Console → Authentication → Sign-in method
- Enable **Anonymous** authentication

### Error: "FirebaseApp initialization failed"
- The `google-services.json` file may be corrupted or incorrect
- Re-download the file from Firebase Console
- Ensure you downloaded the file for the correct Firebase project

### Build fails with "File google-services.json is missing"
- The file is required for the build to succeed
- Follow steps 1-3 above to add the file
- Do not commit this file to Git

## Important Notes

- **Never commit `google-services.json` to version control**
- The file is already added to `.gitignore`
- Each team member must obtain and add this file locally
- If you see this file in Git status, do NOT stage or commit it

## Need Help?
Contact the project administrator or team lead if:
- You don't have access to the Firebase Console
- The download link doesn't work
- You continue to experience setup issues after following this guide

## Firebase Project Access
To request access to the Firebase project, contact the project owner with your Google account email address.
