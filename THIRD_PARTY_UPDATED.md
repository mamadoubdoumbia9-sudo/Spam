# Third-Party Libraries and Dependencies - Updated

This document lists all third-party libraries, frameworks, and tools used in the WhAlert application, along with their licenses, versions, and GitHub repositories.

## Core Android Libraries

### AndroidX Libraries

| Library | Version | License | Repository | Purpose |
|--------|---------|---------|------------|---------|
| androidx.core:core-ktx | 1.12.0 | Apache License 2.0 | [AndroidX](https://github.com/androidx/androidx) | Core Kotlin extensions |
| androidx.appcompat:appcompat | 1.6.1 | Apache License 2.0 | [AndroidX](https://github.com/androidx/androidx) | App compatibility |
| com.google.android.material:material | 1.11.0 | Apache License 2.0 | [Material Components](https://github.com/material-components/material-components-android) | Material Design components |
| androidx.constraintlayout:constraintlayout | 2.1.4 | Apache License 2.0 | [ConstraintLayout](https://github.com/androidx/androidx) | Constraint layout |
| androidx.lifecycle:lifecycle-viewmodel-ktx | 2.7.0 | Apache License 2.0 | [AndroidX Lifecycle](https://github.com/androidx/androidx) | Lifecycle components |
| androidx.lifecycle:lifecycle-runtime-ktx | 2.7.0 | Apache License 2.0 | [AndroidX Lifecycle](https://github.com/androidx/androidx) | Lifecycle runtime |
| androidx.lifecycle:lifecycle-viewmodel-compose | 2.7.0 | Apache License 2.0 | [AndroidX Lifecycle](https://github.com/androidx/androidx) | Compose lifecycle |
| androidx.lifecycle:lifecycle-runtime-compose | 2.7.0 | Apache License 2.0 | [AndroidX Lifecycle](https://github.com/androidx/androidx) | Compose lifecycle runtime |
| androidx.activity:activity-compose | 1.8.2 | Apache License 2.0 | [AndroidX Activity](https://github.com/androidx/androidx) | Activity Compose |

### Jetpack Compose

| Library | Version | License | Repository | Purpose |
|--------|---------|---------|------------|---------|
| androidx.compose:compose-bom | 2024.02.00 | Apache License 2.0 | [Jetpack Compose](https://github.com/androidx/androidx) | Compose BOM |
| androidx.compose.ui:ui | 2024.02.00 | Apache License 2.0 | [Jetpack Compose](https://github.com/androidx/androidx) | Compose UI |
| androidx.compose.ui:ui-graphics | 2024.02.00 | Apache License 2.0 | [Jetpack Compose](https://github.com/androidx/androidx) | Compose graphics |
| androidx.compose.ui:ui-tooling-preview | 2024.02.00 | Apache License 2.0 | [Jetpack Compose](https://github.com/androidx/androidx) | Compose tooling preview |
| androidx.compose.material3:material3 | 2024.02.00 | Apache License 2.0 | [Material 3](https://github.com/androidx/androidx) | Material 3 |
| androidx.compose.material:material-icons-extended | 2024.02.00 | Apache License 2.0 | [Material Icons](https://github.com/androidx/androidx) | Material icons |
| androidx.compose.animation:animation | 2024.02.00 | Apache License 2.0 | [Jetpack Compose](https://github.com/androidx/androidx) | Compose animations |
| androidx.compose.foundation:foundation | 2024.02.00 | Apache License 2.0 | [Jetpack Compose](https://github.com/androidx/androidx) | Compose foundation |

### Navigation

| Library | Version | License | Repository | Purpose |
|--------|---------|---------|------------|---------|
| androidx.navigation:navigation-compose | 2.7.7 | Apache License 2.0 | [Navigation Compose](https://github.com/androidx/androidx) | Navigation Compose |
| androidx.navigation:navigation-fragment-ktx | 2.7.7 | Apache License 2.0 | [Navigation](https://github.com/androidx/androidx) | Navigation fragment |
| androidx.navigation:navigation-ui-ktx | 2.7.7 | Apache License 2.0 | [Navigation](https://github.com/androidx/androidx) | Navigation UI |

## Data Persistence

### Room Database

| Library | Version | License | Repository | Purpose |
|--------|---------|---------|------------|---------|
| androidx.room:room-runtime | 2.6.1 | Apache License 2.0 | [Room](https://github.com/androidx/androidx) | Room runtime |
| androidx.room:room-ktx | 2.6.1 | Apache License 2.0 | [Room](https://github.com/androidx/androidx) | Room Kotlin extensions |
| androidx.room:room-compiler | 2.6.1 | Apache License 2.0 | [Room](https://github.com/androidx/androidx) | Room compiler |

## Networking

| Library | Version | License | Repository | Purpose |
|--------|---------|---------|------------|---------|
| com.squareup.retrofit2:retrofit | 2.9.0 | Apache License 2.0 | [Retrofit](https://github.com/square/retrofit) | Retrofit HTTP client |
| com.squareup.retrofit2:converter-gson | 2.9.0 | Apache License 2.0 | [Retrofit](https://github.com/square/retrofit) | Retrofit Gson converter |
| com.squareup.okhttp3:okhttp | 4.12.0 | Apache License 2.0 | [OkHttp](https://github.com/square/okhttp) | OkHttp client |
| com.squareup.okhttp3:logging-interceptor | 4.12.0 | Apache License 2.0 | [OkHttp](https://github.com/square/okhttp) | OkHttp logging |

### JSON Serialization

| Library | Version | License | Repository | Purpose |
|--------|---------|---------|------------|---------|
| com.google.code.gson:gson | 2.10.1 | Apache License 2.0 | [Gson](https://github.com/google/gson) | JSON serialization |

### Phone Number Validation

| Library | Version | License | Repository | Purpose |
|--------|---------|---------|------------|---------|
| com.googlecode.libphonenumber:libphonenumber | 8.13.42 | Apache License 2.0 | [libphonenumber](https://github.com/google/libphonenumber) | Phone number parsing, formatting, and validation |

**Description**: Google's libphonenumber is the most comprehensive library for handling international phone numbers. It provides:
- Parsing phone numbers in various formats
- Validation of phone numbers for all countries
- Formatting phone numbers in E.164 and other formats
- Country code extraction and region detection
- Metadata for all countries and regions

**Why chosen**: 
- Official Google library, widely used and maintained
- Comprehensive support for all countries
- Regular updates with new phone number rules
- Used by major companies worldwide
- Apache License 2.0 (permissive)

## Image Loading

| Library | Version | License | Repository | Purpose |
|--------|---------|---------|------------|---------|
| io.coil-kt:coil-compose | 2.6.0 | Apache License 2.0 | [Coil](https://github.com/coil-kt/coil) | Image loading for Compose |

**Description**: Coil is a modern image loading library for Android and Compose Multiplatform.

**Why chosen**:
- Modern, Kotlin-first approach
- Full Compose integration
- Efficient memory management
- Support for various image formats
- Easy to use API

## Dependency Injection

| Library | Version | License | Repository | Purpose |
|--------|---------|---------|------------|---------|
| com.google.dagger:hilt-android | 2.48.1 | Apache License 2.0 | [Hilt](https://github.com/google/dagger) | Hilt dependency injection |
| com.google.dagger:hilt-compiler | 2.48.1 | Apache License 2.0 | [Hilt](https://github.com/google/dagger) | Hilt compiler |

**Description**: Hilt is a dependency injection library built on top of Dagger.

**Why chosen**:
- Official Google recommendation for Android
- Simplifies Dagger usage
- Full Kotlin support
- Compile-time safety
- Android-specific features

## Security

| Library | Version | License | Repository | Purpose |
|--------|---------|---------|------------|---------|
| androidx.security:security-crypto | 1.1.0-alpha06 | Apache License 2.0 | [AndroidX Security](https://github.com/androidx/androidx) | AndroidX Security Crypto |
| com.google.android.gms:play-services-safetynet | 18.0.2 | Apache License 2.0 | [SafetyNet](https://developers.google.com/android/guides/safetynet) | SafetyNet API |

**Description**: 
- **AndroidX Security Crypto**: Provides EncryptedSharedPreferences for secure storage
- **SafetyNet**: Google's API for device integrity and safety checks

**Why chosen**:
- Official Android security libraries
- Integration with Android Keystore
- Safe storage of sensitive data
- Device integrity verification

## Firebase (Optional Backend)

| Library | Version | License | Repository | Purpose |
|--------|---------|---------|------------|---------|
| com.google.firebase:firebase-bom | 32.7.2 | Apache License 2.0 | [Firebase](https://github.com/firebase/firebase-android-sdk) | Firebase BOM |
| com.google.firebase:firebase-analytics-ktx | 32.7.2 | Apache License 2.0 | [Firebase](https://github.com/firebase/firebase-android-sdk) | Firebase Analytics |
| com.google.firebase:firebase-auth-ktx | 32.7.2 | Apache License 2.0 | [Firebase](https://github.com/firebase/firebase-android-sdk) | Firebase Authentication |
| com.google.firebase:firebase-firestore-ktx | 32.7.2 | Apache License 2.0 | [Firebase](https://github.com/firebase/firebase-android-sdk) | Firebase Firestore |
| com.google.firebase:firebase-storage-ktx | 32.7.2 | Apache License 2.0 | [Firebase](https://github.com/firebase/firebase-android-sdk) | Firebase Storage |
| com.google.firebase:firebase-crashlytics-ktx | 32.7.2 | Apache License 2.0 | [Firebase](https://github.com/firebase/firebase-android-sdk) | Firebase Crashlytics |
| com.google.gms:google-services | 4.4.0 | Apache License 2.0 | [Google Services](https://github.com/google/play-services-plugins) | Google Services plugin |
| com.google.firebase:firebase-crashlytics-gradle | 3.0.1 | Apache License 2.0 | [Firebase Crashlytics Gradle](https://github.com/firebase/firebase-android-sdk) | Crashlytics Gradle plugin |

**Description**: Firebase provides a complete backend solution for Android applications.

**Why chosen**:
- Official Google backend service
- Free tier available
- Scalable infrastructure
- Authentication, database, storage, analytics
- Crash reporting

## Kotlin

| Library | Version | License | Repository | Purpose |
|--------|---------|---------|------------|---------|
| org.jetbrains.kotlin:kotlin-stdlib-jdk8 | 2.0.0 | Apache License 2.0 | [Kotlin](https://github.com/JetBrains/kotlin) | Kotlin standard library |
| org.jetbrains.kotlin:kotlin-gradle-plugin | 2.0.0 | Apache License 2.0 | [Kotlin Gradle](https://github.com/JetBrains/kotlin) | Kotlin Gradle plugin |
| org.jetbrains.kotlinx:kotlinx-coroutines-android | 1.7.3 | Apache License 2.0 | [Kotlin Coroutines](https://github.com/Kotlin/kotlinx-coroutines) | Coroutines Android |
| org.jetbrains.kotlinx:kotlinx-coroutines-core | 1.7.3 | Apache License 2.0 | [Kotlin Coroutines](https://github.com/Kotlin/kotlinx-coroutines) | Coroutines Core |
| org.jetbrains.kotlinx:kotlinx-coroutines-test | 1.7.3 | Apache License 2.0 | [Kotlin Coroutines](https://github.com/Kotlin/kotlinx-coroutines) | Coroutines Test |

**Description**: Kotlin is the preferred language for Android development.

**Why chosen**:
- Official Android language
- Modern, concise syntax
- Full interoperability with Java
- Coroutines for async programming

## Testing

| Library | Version | License | Repository | Purpose |
|--------|---------|---------|------------|---------|
| junit:junit | 4.13.2 | Eclipse Public License 1.0 | [JUnit](https://github.com/junit-team/junit4) | JUnit testing |
| org.mockito:mockito-core | 5.11.0 | MIT License | [Mockito](https://github.com/mockito/mockito) | Mockito mocking |
| org.mockito.kotlin:mockito-kotlin | 5.3.1 | MIT License | [Mockito Kotlin](https://github.com/mockito/mockito-kotlin) | Mockito Kotlin |
| androidx.test.ext:junit | 1.1.5 | Apache License 2.0 | [AndroidX Test](https://github.com/androidx/androidx) | AndroidX Test JUnit |
| androidx.test.espresso:espresso-core | 3.5.1 | Apache License 2.0 | [Espresso](https://github.com/androidx/androidx) | Espresso testing |
| androidx.compose.ui:ui-test-junit4 | 2024.02.00 | Apache License 2.0 | [Compose Test](https://github.com/androidx/androidx) | Compose UI testing |
| androidx.test:runner | 1.5.2 | Apache License 2.0 | [Android Test Runner](https://github.com/androidx/androidx) | Android Test Runner |
| androidx.test:rules | 1.5.0 | Apache License 2.0 | [Android Test Rules](https://github.com/androidx/androidx) | Android Test Rules |
| android.arch.core:core-testing | 2.2.0 | Apache License 2.0 | [Architecture Core Testing](https://github.com/androidx/androidx) | Architecture Core Testing |

## Gradle Plugins

| Plugin | Version | License | Repository | Purpose |
|--------|---------|---------|------------|---------|
| com.android.tools.build:gradle | 8.2.2 | Apache License 2.0 | [Android Gradle Plugin](https://github.com/gradle/gradle) | Android Gradle plugin |

## Build Tools

| Tool | Version | License | Repository | Purpose |
|------|---------|---------|------------|---------|
| Android Gradle Plugin | 8.2.2 | Apache License 2.0 | [Gradle](https://github.com/gradle/gradle) | Android build |
| Kotlin Gradle Plugin | 2.0.0 | Apache License 2.0 | [Kotlin Gradle](https://github.com/JetBrains/kotlin) | Kotlin build |

## Summary

### Key Libraries Added from GitHub Research

1. **Google libphonenumber** (https://github.com/google/libphonenumber)
   - **Version**: 8.13.42
   - **License**: Apache License 2.0
   - **Purpose**: Phone number parsing, formatting, and validation
   - **Why**: The most comprehensive and widely-used library for international phone number handling

2. **Coil** (https://github.com/coil-kt/coil)
   - **Version**: 2.6.0
   - **License**: Apache License 2.0
   - **Purpose**: Image loading for Jetpack Compose
   - **Why**: Modern, Kotlin-first image loading library with excellent Compose support

3. **AndroidX Security Crypto** (https://github.com/androidx/androidx)
   - **Version**: 1.1.0-alpha06
   - **License**: Apache License 2.0
   - **Purpose**: EncryptedSharedPreferences for secure storage
   - **Why**: Official Android library for secure data storage using Android Keystore

### License Compliance

All third-party libraries used in this project have permissive licenses:

- **Apache License 2.0**: Allows use, modification, and distribution with attribution (most libraries)
- **MIT License**: Allows use, modification, and distribution with attribution (Mockito)
- **Eclipse Public License 1.0**: Allows use, modification, and distribution with attribution (JUnit)

### Verification

1. All dependencies are managed through Gradle and are automatically downloaded during the build process
2. No proprietary or closed-source libraries are used in this project
3. All dependencies are compatible with the project's minimum SDK version (API 26)
4. All dependencies are maintained and actively supported by their respective communities
5. All libraries have been verified to be actively maintained with recent updates

### Notes

1. Firebase dependencies are included but optional - the application can function without a backend
2. All security-related libraries use official Android APIs
3. Phone number validation uses both regex (for quick checks) and libphonenumber (for comprehensive validation)
4. All network communication uses HTTPS with proper certificate validation
