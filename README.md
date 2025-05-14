## Donate to BTC Address: 
`36QW21MQQxUK7ohBQnvP5itipe4bFx91Bo`

---
# Dash (Updated README.md in Year 2025)

![Dash Banner](https://1.bp.blogspot.com/-FgzZ6s4snE0/YLonted4bRI/AAAAAAAAA7M/btcFeIotLw42dSwU9AioHomX_GcG-xmogCLcBGAsYHQ/s1429/68747470733a2f2f312e62702e626c6f6773706f742e636f6d2f2d71374d454847336e4851342f583979763436467a3543492f41414141414141414152342f574a46387a3046766b6251325968725f6c6356686a6375473371306b5030587077434c63424741735948512f7.png)

**PURPOSE: STRICTLY EDUCATIONAL & RESEARCH ONLY.**
This project serves as an advanced case study into Android application development, exploring a wide array of system interactions, data synchronization techniques, and background processing. It simulates a comprehensive device analysis framework for academic exploration and understanding of mobile OS capabilities.

---

## 🚨 **WARNING: ETHICAL USE & LEGAL COMPLIANCE IS YOUR SOLE RESPONSIBILITY** 🚨

**THIS SOFTWARE IS AN EXPLORATORY TOOL FOR ACADEMIC AND RESEARCH PURPOSES ONLY. THE FUNCTIONALITIES DESCRIBED ARE POWERFUL AND CARRY A SIGNIFICANT POTENTIAL FOR MISUSE IF APPLIED OUTSIDE A STRICTLY CONTROLLED, CONSENSUAL, AND LEGAL RESEARCH CONTEXT.**

*   🌐 **ABSOLUTE CONSENT REQUIRED:** Unwavering, explicit, and continuous informed consent MUST be obtained from any individual whose device interacts with this software (or any derivative). The individual must be fully cognizant of its capabilities and the nature of data involved.
*   ⚖️ **ADHERE TO ALL LAWS:** You are solely responsible for ensuring that any use, study, or modification of this software complies with all applicable local, state, national, and international laws concerning privacy, data protection, and electronic surveillance. Unauthorized monitoring is illegal and unethical.
*   🛡️ **NO ENDORSEMENT OF MISUSE:** The creators and contributors disclaim all liability for any unauthorized or unethical application of this software or the knowledge gained from it. This project is NOT intended for covert surveillance or any activity that infringes on privacy rights.
*   🕶️ **"STEALTH" FOR TECHNICAL INSIGHT ONLY:** Features described as "hidden" or "stealth" are included *purely* for the academic study of application behavior, OS countermeasures, and security vulnerabilities. **NEVER deploy such features in a real-world scenario without full, ongoing, and informed consent and complete transparency with the device owner.** Modern operating systems are designed to detect and prevent such behaviors.

**By interacting with any part of this codebase, you affirm your understanding of these warnings and accept full accountability for your actions and any consequences thereof.**

---

## Project Vision: Unveiling Android's Potential

Dash aims to be a comprehensive research platform for understanding how Android applications can interact with the operating system at a deep level. This conceptual, modernized build explores techniques for data collection, background processing using `WorkManager`, real-time communication via Firebase, and advanced permission handling, all while navigating the evolving landscape of Android security and privacy.

The framework simulates a "target" device, from which various data points are gathered (for analytical research or *consensual* parental insight), and a "controller" application, which provides a dashboard for viewing and interpreting this data.

## Core Capabilities & Research Areas

This modernized educational build of Dash explores the following Android capabilities:

### Ⅰ. Communication & Notification Insights
*   **Call Dynamics Analysis:**
    *   Log comprehensive details of incoming, outgoing, and missed calls (contact, number, duration, timestamp).
    *   Capture and archive call audio for voice interaction analysis.
*   **SMS Message Flow:**
    *   Archive incoming and outgoing SMS messages, including sender/receiver, content, and timing.
*   **Notification Ecosystem Study (via Notification Listener Service):**
    *   Intercept and analyze notification content from major messaging platforms (e.g., WhatsApp, Instagram, Messenger), including sender and message snippets.

### Ⅱ. Location & Environment Awareness
*   **Real-time Geospatial Tracking:**
    *   Periodically acquire and transmit GPS coordinates.
    *   Visualize location data on an interactive map within the controller interface.
*   **Ambient Audio Capture:**
    *   Programmatically initiate recording of the device's surrounding audio environment for a defined duration.
*   **Geofencing & Contextual Alerts:**
    *   Define virtual perimeters and trigger notifications upon the device entering or exiting these zones, for research into location-based event handling.

### Ⅲ. Device Interaction & Media Capture
*   **Remote Imaging (Camera API Research):**
    *   Initiate photo capture using front or rear cameras without an immediate on-device preview (for studying camera hardware access and image processing).
    *   Trigger capture of short video clips for motion data analysis.
*   **Keystroke Pattern Analysis (via Accessibility Service):**
    *   Log text input across various applications to understand input methods and user interaction patterns.
*   **Clipboard Content Analysis:**
    *   Monitor and capture text copied to the system clipboard.

### Ⅳ. System & Application Ecosystem Analysis
*   **Application Usage Metrics:**
    *   Track application launch frequency and duration of use, providing insights into device interaction habits.
*   **Web Navigation Patterns (Limited by OS/Browser Security):**
    *   Attempt to log visited website URLs to understand web browsing behavior.
*   **Device & System Profiling:**
    *   Gather and display extensive device information: OS version, battery status, network connectivity (Wi-Fi, mobile data), list of installed applications, hardware model.
*   **Limited File System Exploration:**
    *   Enable browsing of user-accessible directories and listing of file structures.
    *   Facilitate the download of specific, non-system files for off-device analysis.

### Ⅴ. Remote Interaction & Control (Research & Utility)
*   **Experimental Live Screen Relay:**
    *   Investigate low-framerate screen content streaming to the controller application (highly experimental, resource-intensive, and permission-dependent).
*   **Basic Remote Commands:**
    *   Send simple instructions like triggering an audible alert (e.g., "find my device" simulation) or displaying a custom toast message on the target device.

### Ⅵ. Operational Modalities (Research Focus)
*   **Multi-Target Data Aggregation:**
    *   Design the controller application to manage and view data streams from multiple distinct target devices.
*   **App Presence Minimization Study (Research Only):**
    *   Explore techniques to reduce the application's visibility on the target device (e.g., absence of a traditional launcher icon). *This is for academic understanding of OS behavior and not for deceptive deployment.*

### VII. Foundational Enhancements (Modernized Build)
*   **Resilient Background Operations with WorkManager:**
    *   Leverage `WorkManager` for robust, battery-conscious, and reliable execution of background data synchronization and scheduled tasks, adhering to modern Android's Doze mode and App Standby buckets.
*   **Futuristic Controller Interface (UI/UX Overhaul):**
    *   Conceptual redesign of the controller dashboard using Jetpack Compose and Material 3 (Material You) principles for an intuitive, visually appealing, and data-rich experience.
*   **Contextual & Granular Permission Management:**
    *   Implement clear, user-friendly runtime permission requests, thoroughly explaining the necessity of each permission for the intended (research) functionality. This includes careful handling of sensitive permissions like Background Location, Accessibility, and Notification Listener access.

## Envisioned Technology Stack (Modernized Educational Build)

*   **Core Language:** Kotlin (latest stable release, leveraging coroutines and Flow)
*   **Architectural Paradigm:** MVVM (ViewModel, LiveData/StateFlow) or MVI, potentially with Clean Architecture principles (UseCases).
*   **Asynchronous Operations:** Primarily Kotlin Coroutines & Flow; RxJava 3 for specialized reactive scenarios.
*   **Dependency Injection:** Dagger Hilt for streamlined Android DI.
*   **Cloud Backend:** Firebase Suite (latest SDKs) - Authentication, Firestore (preferred over Realtime Database for complex queries and scalability), Cloud Storage, and potentially Cloud Functions for advanced server-side logic.
*   **Robust Background Tasks:** Android Jetpack WorkManager.
*   **User Interface (Controller):**
    *   Jetpack Compose for a fully declarative and modern UI.
    *   Material Components 3 (Material You) for theming and components.
    *   Jetpack Navigation Component for seamless in-app navigation.
*   **Local Data Persistence:** Jetpack Room for structured local caching on controller/target devices.
*   **Image Handling:** Coil (Kotlin-first) or Glide.

## Setup & Configuration

1.  **Prerequisites:**
    *   Latest stable Android Studio (e.g., Iguana, Jellyfish, or newer).
    *   JDK 17+.
2.  **Firebase Project Setup:**
    *   Create a new Firebase project at [https://firebase.google.com/](https://firebase.google.com/).
    *   Add an Android app to your Firebase project for both the "target" and "controller" apps (using distinct package names).
    *   Download the `google-services.json` file for each and place it in the respective `app` module directory.
    *   Enable the following Firebase services:
        *   **Authentication:** Enable Email/Password sign-in provider.
        *   **Realtime Database** (or **Firestore** - recommended for new projects): Set up security rules. For initial research with Firestore:
            ```json
            // Firestore Rules (example)
            rules_version = '2';
            service cloud.firestore {
              match /databases/{database}/documents {
                // User-specific data accessible only by the authenticated owner
                match /users/{userId}/{document=**} {
                  allow read, write: if request.auth != null && request.auth.uid == userId;
                }
              }
            }
            ```
        *   **Cloud Storage:** Set up security rules. For initial research:
            ```
            service firebase.storage {
              match /b/{bucket}/o {
                // User-specific data accessible only by the authenticated owner
                match /user/{userId}/{allPaths=**} {
                  allow read, write: if request.auth != null && request.auth.uid == userId;
                }
              }
            }
            ```
3.  **Google Maps API Key:**
    *   Obtain a Google Maps API Key from the Google Cloud Console.
    *   Enable "Maps SDK for Android".
    *   Store the key securely in your project's `local.properties` file (this file should be added to `.gitignore`):
        *   In `local.properties`: `MAPS_API_KEY=YOUR_API_KEY_HERE`
    *   Reference this key in your `app/build.gradle` file:
        ```gradle
        android {
            defaultConfig {
                // ...
                // Read the API key from local.properties
                Properties properties = new Properties()
                if (rootProject.file("local.properties").exists()) {
                    properties.load(rootProject.file("local.properties").newDataInputStream())
                }
                resValue "string", "APY_KEY_MAPS", properties.getProperty("MAPS_API_KEY", "YOUR_DEFAULT_KEY_IF_ANY_FOR_DEBUG")
            }
            // ...
        }
        ```
    *   And ensure your `AndroidManifest.xml` references it for the Maps SDK:
        ```xml
        <meta-data
            android:name="com.google.android.geo.API_KEY"
            android:value="@string/APY_KEY_MAPS" />
        ```
4.  **Project Synthesis (Conceptual):**
    *   Conceptually, one would clone the original repository (if available) or start fresh based on its structure.
    *   Update the project to target the latest `targetSdkVersion` and `compileSdkVersion`.
    *   Migrate fully from `kotlin-android-extensions` to Jetpack ViewBinding (if using XML layouts) or adopt Jetpack Compose for UI.
5.  **Target Device Configuration (Manual & Consensual for Research):**
    *   **Transparency First:** The user of the target device MUST be fully aware and consent to the enabling of these settings for research.
    *   **Runtime Permissions:** Location, Camera, Microphone, Storage, Phone, SMS, Contacts must be granted at runtime.
    *   **Special Access Permissions (via Settings app):**
        *   Draw Over Other Apps (System Alert Window).
        *   Usage Access (App Usage Stats).
        *   Accessibility Service.
        *   Notification Listener Service.
        *   Battery Optimization Exemption (Disable "Optimize battery usage" for the app).
    *   *Note: Direct programmatic enabling of these sensitive services is heavily restricted on modern, non-rooted Android systems for security and privacy reasons. Users must be guided to settings to enable them manually.*

## Dynamic Configuration Parameters

*   **`PACKAGE_CHECK_SOCIAL`** (originally in `app/build.gradle`'s `ext` block, better managed via build types or dedicated config files in a modern setup): Specifies the target package name for the social media interaction simulation research.
    *   Example in `app/build.gradle`:
        ```gradle
        android {
            // ...
            buildTypes {
                debug {
                    buildConfigField "String", "PACKAGE_CHECK_SOCIAL", "\"com.example.socialapp.debug\""
                }
                release {
                    buildConfigField "String", "PACKAGE_CHECK_SOCIAL", "\"com.example.socialapp\""
                }
            }
        }
        ```
    *   The corresponding XML view for the phishing simulation would need to be adapted if this value changes.

---

## 🗺️ Roadmap to update this code for latest android (Year 2025 - Educational Study) 🗺️

This section outlines potential areas for updating and improving the existing codebase to align with modern Android development practices and address technical debt. These suggestions are for **educational understanding** of app evolution and **do not endorse or aim to improve any problematic surveillance capabilities** of the original application.

**Please Note:** The developer or researcher studying this project would need to apply these changes to the source code themselves.

### 1. Project-Level and App-Level Build Files:

*   **File:** `Dash-main/build.gradle` (Project Level)
    *   **Enhancement:** Update Gradle plugin versions.
        *   `classpath 'com.android.tools.build:gradle:3.2.1'` is very old. Update to the latest stable version (e.g., 7.x or 8.x). This will likely require updating your Android Studio and possibly JDK.
        *   `classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"` (where `kotlin_version = '1.3.41'`) is also very old. Update to a recent Kotlin version (e.g., 1.7.x, 1.8.x, 1.9.x).
        *   `classpath 'com.google.gms:google-services:4.3.2'` can likely be updated.
*   **File:** `Dash-main/app/build.gradle` (App Module)
    *   **Enhancement:** Update SDK Versions and Dependencies.
        *   `compileSdkVersion 28` -> Update to the latest (e.g., 33 or 34).
        *   `minSdkVersion 21` -> Consider if you still need to support API 21 (Lollipop). Raising it can simplify development.
        *   `targetSdkVersion 26` -> **Crucial:** Update to the latest (e.g., 33 or 34). This has major implications for permissions, background work, and privacy features.
        *   **All Dependencies:** Most dependencies listed (Firebase, AndroidX, Glide, Picasso, RxJava, Dagger, etc.) are likely very old versions. Update them to their latest stable versions. This is a significant task and may involve API changes.
            *   Example: `androidx.appcompat:appcompat:1.1.0` -> `androidx.appcompat:appcompat:1.6.1` or newer.
            *   Firebase libraries: `firebase-auth:19.0.0` -> `firebase-auth:22.x.x`.
    *   **Enhancement:** Replace Deprecated `kotlin-android-extensions`.
        *   `apply plugin: 'kotlin-android-extensions'` is deprecated.
        *   **Action:** Remove this plugin. Migrate from synthetic view imports to ViewBinding or DataBinding in all your Activities and Fragments. This is a more robust and type-safe way to access views.
        *   Affected files: All Activities (`LoginActivity`, `MainChildActivity`, `MainParentActivity`, `RegisterActivity`, `SocialActivityM`, `ViewPhoto`, `LockActivity`) and Fragments (`CallsFragment`, `KeysFragment`, `MapsFragment`, `MessageFragment`, `NotifyMessageFragment`, `PhotoFragment`, `RecordingFragment`, `SettingFragment`, `SocialFragment`).
    *   **Enhancement:** Dagger KAPT versions.
        *   `kapt 'com.google.dagger:dagger-compiler:2.14.1'` -> Ensure this matches the updated Dagger core library version.
    *   **Enhancement:** Review `PACKAGE_CHECK_SOCIAL` build config field.
        *   This is used for the phishing feature. If refactoring for ethical purposes, this would be removed. For purely educational understanding of build configs, it's a standard way to inject values (though buildConfigField in `build.gradle` is preferred over `ext` for this purpose).
*   **File:** `Dash-main/gradle.properties`
    *   `android.useAndroidX=true` and `android.enableJetifier=true` are good and necessary for modern development.

### 2. Application Class:

*   **File:** `Dash-main/app/src/main/kotlin/com/github/muneebwanee/dash/app/Dash.kt`
    *   **Enhancement:** `RootManager` initialization (`root = RootManager.getInstance()`).
        *   Consider if root access is genuinely needed and if its use can be minimized or removed due to its unreliability and security implications on modern, non-rooted devices. Many features attempted with root might not work as expected.
    *   **Enhancement:** Firebase Disk Persistence (`database.setPersistenceCacheSizeBytes(SIZE_CACHE_FIREBASE)`).
        *   This is a standard Firebase feature. Ensure `SIZE_CACHE_FIREBASE` is a well-considered value for offline caching needs.

### 3. Data Models (e.g., `Calls.kt`, `Location.kt`, etc.):

*   **Files:** All files in `Dash-main/app/src/main/kotlin/com/github/muneebwanee/dash/data/model/`
    *   **Enhancement:** Leverage Kotlin Data Classes.
        *   Many models are standard classes. If their primary purpose is data holding, convert them to `data class` to benefit from autogenerated `equals()`, `hashCode()`, `toString()`, `copy()`, etc.
        *   Example: `class Calls { ... }` could become `data class Calls(...)` if its role aligns.
    *   **Enhancement:** Maintain Strong Nullability.
        *   Kotlin's null safety (e.g., `var contact: String? = null`) is well-utilized. Continue this meticulous practice.

### 4. SharedPreferences:

*   **File:** `Dash-main/app/src/main/kotlin/com/github/muneebwanee/dash/data/preference/DataSharePreference.kt`
    *   **Enhancement:** Explore Jetpack DataStore.
        *   As a modern alternative to SharedPreferences, DataStore offers better support for asynchronous operations and typed data. For simple flags and strings, SharedPreferences remains acceptable.
        *   The current extension property approach is a convenient syntax.

### 5. RxFirebase Wrappers:

*   **Files:** `DevelopFirebase.kt`, `RxFirebaseAuth.kt`, `RxFirebaseDatabase.kt`, `RxFirebaseStorage.kt`
    *   **Enhancement:** Update to latest Firebase SDKs & Explore Coroutines/Flow.
        *   Modern Firebase SDKs offer improved Kotlin support, including first-class coroutines and Flow integration, which can often lead to simpler and more idiomatic asynchronous code compared to solely relying on RxJava for Firebase operations.
    *   **Enhancement:** Robust Error Handling in Rx Chains.
        *   The `onCancelled` in `RxFirebaseDatabase.kt` includes a `try-catch`. Ensure comprehensive error propagation and handling (e.g., `doOnError`, `onErrorResumeNext`) throughout all RxJava chains interacting with Firebase or other fallible operations.
    *   The pattern of wrapping Firebase tasks in RxJava Observables/Maybes/Singles is a valid and common approach for managing asynchronous Firebase calls.

### 6. Dependency Injection (Dagger):

*   **Files:** All files in `Dash-main/app/src/main/kotlin/com/github/muneebwanee/dash/di/`
    *   **Enhancement:** Synchronize Dagger versions with other updated dependencies.
    *   **Enhancement:** Review Scoping (`@Singleton`, `@PerActivity`, `@PerService`). Ensure scopes are correctly applied to manage the lifecycle and sharing of injected objects effectively.
    *   **Enhancement:** Consider Dagger Hilt for Android.
        *   If undertaking a significant refactor, explore Hilt for a more streamlined and Android-idiomatic Dagger setup, reducing boilerplate.

### 7. Services (e.g., `AccessibilityDataService.kt`, `CallsService.kt`, `MonitorService.kt`):

*   **Files:** All files in `Dash-main/app/src/main/kotlin/com/github/muneebwanee/dash/services/`
    *   **Enhancement (Critical):** Modernize Background Execution with WorkManager.
        *   Services like `AccessibilityDataService` and `MonitorService`, if intended for long-running background operations, will face severe restrictions and termination on modern Android.
        *   **Action:** For tasks that can be deferred and require guaranteed execution (even if the app is closed or device restarts), migrate their logic to `WorkManager`. This is a fundamental architectural shift.
            *   `AccessibilityDataService`: Data upload logic or periodic checks could use WorkManager. The core accessibility event listening must remain within an AccessibilityService.
            *   `MonitorService`: Its application checking logic is a prime candidate for periodic execution via `WorkManager`.
    *   **Enhancement:** Foreground Services for User-Aware Tasks.
        *   If any service performs tasks the user *is actively aware of and has explicitly initiated* (e.g., a consensual recording session with a clear UI indicator), it *must* be implemented as a Foreground Service displaying a persistent notification. This contrasts with the original "hidden" intent.
    *   **`AccessibilityDataService.kt` Specifics:**
        *   Coupling with `MonitorService` (`interactor.getSocialStatus()`): Evaluate if this direct start is optimal or if an event bus or other decoupled communication mechanism would be better.
        *   `getLocation()`: Background location access is heavily restricted. Requires careful implementation with appropriate permissions (foreground service for continuous tracking is mandatory) and clear user notification.
    *   **`CallsService.kt`, `SmsService.kt` (BroadcastReceiver-initiated):**
        *   Ensure these services perform their tasks quickly and call `stopSelf()` promptly to avoid ANRs or system termination. For any lengthy processing, offload to `WorkManager`.
    *   **`NotificationService.kt` (`NotificationListenerService`):**
        *   The core functionality is tied to this service type. Any subsequent data processing or upload logic initiated by this service could be handed off to `WorkManager`.

### 8. UI Layer (Activities and Fragments):

*   **Files:** All in `Dash-main/app/src/main/kotlin/com/github/muneebwanee/dash/ui/activities/` and `Dash-main/app/src/main/kotlin/com/github/muneebwanee/dash/ui/fragments/`
    *   **Enhancement:** Adopt Jetpack ViewModel and LiveData/StateFlow.
        *   Migrate from the current Interactor pattern to Jetpack ViewModels for managing UI-related data in a lifecycle-aware manner. Use LiveData or Kotlin StateFlow/SharedFlow for observing data changes from ViewModels and updating the UI reactively.
    *   **Enhancement:** UI Modernization with Material You (Material 3).
        *   Refactor XML layouts and styles (or rebuild with Jetpack Compose) to use Material 3 components for a contemporary look and feel, including dynamic color theming.
    *   **`BaseActivity.kt`, `BaseFragment.kt`:** These common base classes are good. Ensure they remain focused and don't become overly large.
    *   **`MainChildActivity.kt` Permission Handling:**
        *   The attempts to enable services via root commands (`activatePermissionRoot`) are unreliable on most modern devices. Refocus on standard Android permission requests and guiding users to system settings.
    *   **`MainParentActivity.kt` `onActivityResult`:**
        *   For gallery picks or other activity results, migrate to the modern Activity Result APIs.
    *   **Fragments - General:**
        *   Timeout logic (`runDelayedOnUiThread(13000)`): Replace with more robust UI state management (e.g., a sealed class representing `Loading`, `Success(data)`, `Error(message)` states, observed via LiveData/Flow).

### 9. Utilities (`ConstFun.kt`, `FileHelper.kt`, etc.):

*   **`ConstFun.kt`:**
    *   `isRoot()`, `permissionRoot()`: Root detection and usage are inherently fragile. Minimize or eliminate reliance on root.
    *   `showApp()`: Modifying component enabled state can have side effects. Use with caution and understanding of PackageManager behavior.
*   **`FileHelper.kt`:**
    *   For `targetSdkVersion 30+`, thoroughly review file access against Scoped Storage principles if interacting with shared storage. App-specific internal/cache directories are generally less problematic.
*   **Hidden Camera Utilities (`HiddenCameraService.kt`, `CameraPreview.kt`):**
    *   The use of `TYPE_SYSTEM_OVERLAY` or `TYPE_APPLICATION_OVERLAY` is heavily restricted and flagged by security systems. For educational exploration of camera APIs, transition to standard `CameraX` or `Camera2` APIs, potentially demonstrating how a minimal or no-preview capture *could* be attempted (while also explaining why this is bad practice for non-consensual apps).

### 10. Overall Code Style and Readability:

*   **KDoc Comments:** Systematically add KDoc comments to all public classes, methods, and complex logic sections to enhance understanding and maintainability.
*   **Naming Conventions:** Ensure consistent and descriptive naming for variables, functions, and classes.

### Summary for the Update:

1.  **Update Gradle & Dependencies:** Foundational.
2.  **Update `targetSdkVersion` & `compileSdkVersion`:** Critical for compatibility and security.
3.  **Replace `kotlin-android-extensions`:** Adopt ViewBinding or DataBinding.
4.  **Implement `WorkManager`:** For reliable, modern background processing.
5.  **Adopt ViewModel & LiveData/StateFlow:** For robust UI state management.
6.  **Refine Permission Handling:** Align with current Android best practices.
7.  **Modernize UI:** Explore Material 3 / Jetpack Compose.
8.  **Strengthen Error Handling & Logging.**
9.  **Secure API Key Management:** Move keys out of version control (e.g., `local.properties`).

This roadmap provides a comprehensive list of areas where the codebase can be updated for educational study, focusing on modern Android development techniques.

---

# License

```java 
Copyright [2020] [muneebwanee]

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```
---
### ❤️Supporters❤️
[![Stargazers repo roster for @swagkarna/Rafel-Rat](https://reporoster.com/stars/muneebwanee/Dash)](https://github.com/muneebwanee/Dash/stargazers)
[![Forkers repo roster for @swagkarna/Rafel-Rat](https://reporoster.com/forks/muneebwanee/Dash)](https://github.com/muneebwanee/Dash/network/members)

---
