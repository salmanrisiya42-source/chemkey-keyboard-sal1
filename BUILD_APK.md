# Chemistry Keyboard – Pro: Android Build & APK Instructions

## Project Overview
- **App Name:** Chemistry Keyboard – Pro
- **Application ID:** `com.aistudio.chemkeyboard.kprxqt`
- **Minimum SDK:** API 24 (Android 7.0+; fully tested for Android 10 / API 29 through Android 16 / API 36)
- **Architecture:** Kotlin + Jetpack Compose + Material 3 + Android `InputMethodService` (IME)
- **Permissions:** Zero unnecessary permissions (no `INTERNET`, no broad storage permissions; PNG export uses Android 10+ scoped `MediaStore` and `FileProvider`).

## Building the APK in Android Studio
1. Open the project root directory in **Android Studio**.
2. Wait for Gradle Sync to complete.
3. To build a **Debug APK**:
   - Menu: **Build → Build Bundle(s) / APK(s) → Build APK(s)**
   - Output path: `app/build/outputs/apk/debug/app-debug.apk`
4. To build a **Release APK**:
   - Menu: **Build → Generate Signed Bundle / APK... → APK**
   - Select or create your upload keystore and choose `release` build variant.
   - Output path: `app/build/outputs/apk/release/app-release.apk`

## Building via Command Line (Gradle)
```bash
# Build Debug APK
gradle :app:assembleDebug

# Run Unit & Robolectric Tests
gradle :app:testDebugUnitTest

# Build Release APK (requires KEYSTORE_PATH, STORE_PASSWORD, KEY_PASSWORD env vars)
gradle :app:assembleRelease
```

## Enabling the System Chemistry Keyboard on Android
1. Install and open **Chemistry Keyboard – Pro**.
2. Tap **Enable** on the System Keyboard card (or go to **Settings → System → Languages & input → On-screen keyboard → Manage on-screen keyboards**).
3. Toggle **Chemistry Keyboard – Pro** ON.
4. Tap **Switch** (or tap the keyboard switcher icon in any app's navigation bar) and select **Chemistry Keyboard – Pro**.
