# Third-Party Libraries and Dependencies

This document lists all third-party libraries, frameworks, and tools used in the WhAlert application, along with their licenses and versions.

## AndroidX Libraries

| Library | Version | License | Purpose |
|--------|---------|---------|---------|
| androidx.core:core-ktx | 1.12.0 | Apache License 2.0 | Core Kotlin extensions |
| androidx.appcompat:appcompat | 1.6.1 | Apache License 2.0 | App compatibility |
| com.google.android.material:material | 1.11.0 | Apache License 2.0 | Material Design components |
| androidx.constraintlayout:constraintlayout | 2.1.4 | Apache License 2.0 | Constraint layout |
| androidx.lifecycle:lifecycle-viewmodel-ktx | 2.7.0 | Apache License 2.0 | Lifecycle components |
| androidx.lifecycle:lifecycle-runtime-ktx | 2.7.0 | Apache License 2.0 | Lifecycle runtime |
| androidx.lifecycle:lifecycle-viewmodel-compose | 2.7.0 | Apache License 2.0 | Compose lifecycle |
| androidx.lifecycle:lifecycle-runtime-compose | 2.7.0 | Apache License 2.0 | Compose lifecycle runtime |
| androidx.activity:activity-compose | 1.8.2 | Apache License 2.0 | Activity Compose |
| androidx.navigation:navigation-compose | 2.7.7 | Apache License 2.0 | Navigation Compose |
| androidx.navigation:navigation-fragment-ktx | 2.7.7 | Apache License 2.0 | Navigation fragment |
| androidx.navigation:navigation-ui-ktx | 2.7.7 | Apache License 2.0 | Navigation UI |

## Jetpack Compose

| Library | Version | License | Purpose |
|--------|---------|---------|---------|
| androidx.compose:compose-bom | 2024.02.00 | Apache License 2.0 | Compose BOM |
| androidx.compose.ui:ui | 2024.02.00 | Apache License 2.0 | Compose UI |
| androidx.compose.ui:ui-graphics | 2024.02.00 | Apache License 2.0 | Compose graphics |
| androidx.compose.ui:ui-tooling-preview | 2024.02.00 | Apache License 2.0 | Compose tooling preview |
| androidx.compose.material3:material3 | 2024.02.00 | Apache License 2.0 | Material 3 |
| androidx.compose.material:material-icons-extended | 2024.02.00 | Apache License 2.0 | Material icons |

## Room Database

| Library | Version | License | Purpose |
|--------|---------|---------|---------|
| androidx.room:room-runtime | 2.6.1 | Apache License 2.0 | Room runtime |
| androidx.room:room-ktx | 2.6.1 | Apache License 2.0 | Room Kotlin extensions |
| androidx.room:room-compiler | 2.6.1 | Apache License 2.0 | Room compiler |

## Networking

| Library | Version | License | Purpose |
|--------|---------|---------|---------|
| com.squareup.retrofit2:retrofit | 2.9.0 | Apache License 2.0 | Retrofit HTTP client |
| com.squareup.retrofit2:converter-gson | 2.9.0 | Apache License 2.0 | Retrofit Gson converter |
| com.squareup.okhttp3:okhttp | 4.12.0 | Apache License 2.0 | OkHttp client |
| com.squareup.okhttp3:logging-interceptor | 4.12.0 | Apache License 2.0 | OkHttp logging |
| com.google.code.gson:gson | 2.10.1 | Apache License 2.0 | JSON serialization |

## Image Loading

| Library | Version | License | Purpose |
|--------|---------|---------|---------|
| io.coil-kt:coil-compose | 2.6.0 | Apache License 2.0 | Image loading for Compose |

## Dependency Injection

| Library | Version | License | Purpose |
|--------|---------|---------|---------|
| com.google.dagger:hilt-android | 2.48.1 | Apache License 2.0 | Hilt dependency injection |
| com.google.dagger:hilt-compiler | 2.48.1 | Apache License 2.0 | Hilt compiler |

## Firebase

| Library | Version | License | Purpose |
|--------|---------|---------|---------|
| com.google.firebase:firebase-bom | 32.7.2 | Apache License 2.0 | Firebase BOM |
| com.google.firebase:firebase-analytics-ktx | 32.7.2 | Apache License 2.0 | Firebase Analytics |
| com.google.firebase:firebase-auth-ktx | 32.7.2 | Apache License 2.0 | Firebase Authentication |
| com.google.firebase:firebase-firestore-ktx | 32.7.2 | Apache License 2.0 | Firebase Firestore |
| com.google.firebase:firebase-storage-ktx | 32.7.2 | Apache License 2.0 | Firebase Storage |
| com.google.firebase:firebase-crashlytics-ktx | 32.7.2 | Apache License 2.0 | Firebase Crashlytics |
| com.google.gms:google-services | 4.4.0 | Apache License 2.0 | Google Services plugin |
| com.google.firebase:firebase-crashlytics-gradle | 3.0.1 | Apache License 2.0 | Crashlytics Gradle plugin |

## Kotlin

| Library | Version | License | Purpose |
|--------|---------|---------|---------|
| org.jetbrains.kotlin:kotlin-stdlib-jdk8 | 2.0.0 | Apache License 2.0 | Kotlin standard library |
| org.jetbrains.kotlin:kotlin-gradle-plugin | 2.0.0 | Apache License 2.0 | Kotlin Gradle plugin |
| org.jetbrains.kotlinx:kotlinx-coroutines-android | 1.7.3 | Apache License 2.0 | Coroutines Android |
| org.jetbrains.kotlinx:kotlinx-coroutines-core | 1.7.3 | Apache License 2.0 | Coroutines Core |
| org.jetbrains.kotlinx:kotlinx-coroutines-test | 1.7.3 | Apache License 2.0 | Coroutines Test |

## Testing

| Library | Version | License | Purpose |
|--------|---------|---------|---------|
| junit:junit | 4.13.2 | Eclipse Public License 1.0 | JUnit testing |
| org.mockito:mockito-core | 5.11.0 | MIT License | Mockito mocking |
| org.mockito.kotlin:mockito-kotlin | 5.3.1 | MIT License | Mockito Kotlin |
| androidx.test.ext:junit | 1.1.5 | Apache License 2.0 | AndroidX Test JUnit |
| androidx.test.espresso:espresso-core | 3.5.1 | Apache License 2.0 | Espresso testing |
| androidx.compose.ui:ui-test-junit4 | 2024.02.00 | Apache License 2.0 | Compose UI testing |
| androidx.test:runner | 1.5.2 | Apache License 2.0 | Android Test Runner |
| androidx.test:rules | 1.5.0 | Apache License 2.0 | Android Test Rules |
| android.arch.core:core-testing | 2.2.0 | Apache License 2.0 | Architecture Core Testing |

## Security

| Library | Version | License | Purpose |
|--------|---------|---------|---------|
| androidx.security:security-crypto | 1.1.0-alpha06 | Apache License 2.0 | AndroidX Security Crypto |
| com.google.android.gms:play-services-safetynet | 18.0.2 | Apache License 2.0 | SafetyNet API |

## Gradle Plugins

| Plugin | Version | License | Purpose |
|--------|---------|---------|---------|
| com.android.tools.build:gradle | 8.2.2 | Apache License 2.0 | Android Gradle plugin |

## Build Tools

| Tool | Version | License | Purpose |
|------|---------|---------|---------|
| Android Gradle Plugin | 8.2.2 | Apache License 2.0 | Android build |
| Kotlin Gradle Plugin | 2.0.0 | Apache License 2.0 | Kotlin build |

## Summary

All third-party libraries used in this project are open-source and have permissive licenses (mainly Apache License 2.0 and MIT License) that allow their use in both open-source and commercial applications.

### License Compliance

- **Apache License 2.0**: Allows use, modification, and distribution with attribution.
- **MIT License**: Allows use, modification, and distribution with attribution.
- **Eclipse Public License 1.0**: Allows use, modification, and distribution with attribution.

### Notes

1. All dependencies are managed through Gradle and are automatically downloaded during the build process.
2. No proprietary or closed-source libraries are used in this project.
3. All dependencies are compatible with the project's minimum SDK version (API 26).
4. All dependencies are maintained and actively supported by their respective communities.

For more information about each library's license, please refer to the library's official documentation or repository.
