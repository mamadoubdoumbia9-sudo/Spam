# WhAlert - WhatsApp Account Reporting Application

## Overview

**WhAlert** is a professional Android application designed to help users document and report suspicious or malicious WhatsApp accounts. The application follows WhatsApp's official procedures and does not use any private APIs or unofficial endpoints.

## Features

### Core Functionality
- **Report Creation**: Users can create detailed reports about suspicious WhatsApp accounts
- **Phone Number Validation**: E.164 format validation with country code support
- **Category Selection**: Multiple report categories (spam, scam, impersonation, harassment, etc.)
- **Evidence Collection**: Add screenshots and text evidence to support reports
- **Report Verification**: Review all details before submission
- **Official Channel Integration**: Guides users to WhatsApp's official reporting mechanism

### Report Management
- **Status Tracking**: Track report status from draft to submitted
- **History**: View all past reports with filtering and search
- **Daily Limit**: Application-enforced limit of 3 reports per 24 hours
- **Local Storage**: All reports stored locally with Room database

### User Experience
- **Modern UI**: Built with Jetpack Compose and Material 3
- **Responsive Design**: Works on all screen sizes and densities
- **Dark/Light Mode**: Automatic or manual theme selection
- **Accessibility**: Full accessibility support

### Security
- **Data Protection**: Sensitive data is masked and protected
- **Secure Storage**: Uses Android Keystore for sensitive information
- **No Private APIs**: Does not use any WhatsApp private APIs or endpoints
- **HTTPS Only**: All network communication uses HTTPS

### Admin Console
- **Private Access**: PIN-protected admin console
- **Statistics**: View application usage statistics
- **Integration Status**: Monitor backend service health
- **Recent Reports**: View recent report activity

## Architecture

### Technology Stack
- **Language**: Kotlin 2.0
- **UI Framework**: Jetpack Compose
- **Design System**: Material 3
- **Architecture**: MVVM (Model-View-ViewModel)
- **Dependency Injection**: Hilt
- **Database**: Room (SQLite)
- **Networking**: Retrofit + OkHttp
- **Coroutines**: Kotlin Coroutines for async operations
- **Backend**: Firebase (optional integration)

### Project Structure
```
app/
├── src/
│   ├── main/
│   │   ├── java/com/whalert/app/
│   │   │   ├── di/              # Dependency injection
│   │   │   ├── model/           # Data models
│   │   │   ├── repository/      # Data repositories
│   │   │   ├── service/         # Background services
│   │   │   ├── ui/              # UI components and screens
│   │   │   ├── util/            # Utility classes
│   │   │   └── viewmodel/       # ViewModels
│   │   └── res/                 # Resources
│   └── test/                    # Unit tests
│   └── androidTest/             # Instrumentation tests
├── build.gradle.kts            # App-level build configuration
└── proguard-rules.pro          # ProGuard rules
```

## Getting Started

### Prerequisites
- Android Studio (latest version)
- Java JDK 17+
- Android SDK (API 35)
- Minimum SDK: API 26 (Android 8.0 Oreo)

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/mamadoubdoumbia9-sudo/Spam.git
   cd Spam
   ```

2. Open the project in Android Studio

3. Sync Gradle dependencies

4. Build the project:
   ```bash
   ./gradlew clean assembleDebug
   ```

5. Run on an emulator or physical device

### Building APK
To build a release APK:
```bash
# Clean previous builds
./gradlew clean

# Build release APK
./gradlew assembleRelease

# The APK will be in: app/build/outputs/apk/release/app-release.apk
```

## Usage

### Creating a Report
1. Tap "Nouveau signalement" on the home screen
2. Enter the suspicious phone number in international format (E.164)
3. Select the appropriate category
4. Provide a detailed description of the issue
5. Add evidence (screenshots, text) if available
6. Review and confirm the report
7. Follow the link to WhatsApp's official reporting mechanism

### Viewing Report History
1. Tap "Mes signalements" on the home screen
2. Browse, filter, or search your past reports
3. Tap on a report to view its details and status

### Accessing Admin Console
1. Tap "Console privée" on the home screen
2. Enter your PIN (set up during first access)
3. View statistics, integration status, and recent reports

## Important Notes

### What WhAlert CAN Do
- ✅ Prepare a complete report dossier
- ✅ Organize and structure evidence
- ✅ Generate professional report text
- ✅ Request user confirmation before submission
- ✅ Guide users to WhatsApp's official reporting mechanism
- ✅ Store report history locally
- ✅ Display technical confirmations

### What WhAlert CANNOT Do
- ❌ Guarantee account bans
- ❌ Access WhatsApp's internal decisions
- ❌ Send fake reports
- ❌ Multiply reports artificially
- ❌ Bypass WhatsApp's protections
- ❌ Force WhatsApp to suspend accounts

### Legal Compliance
- This application does NOT use any WhatsApp private APIs
- All reports must be submitted through WhatsApp's official mechanisms
- Users must provide genuine, authentic reports
- The application enforces a daily limit to prevent abuse

## Security

### Data Protection
- Phone numbers are validated and formatted
- Sensitive data is masked in the UI
- No user data is sent to external servers without consent
- All network communication uses HTTPS

### Authentication
- User authentication is handled securely
- WhatsApp connection does NOT store OTP or passwords
- Console access requires PIN authentication

### Privacy
- User data is stored locally on the device
- No tracking or analytics without user consent
- All data can be deleted by the user

## Testing

### Running Tests
```bash
# Unit tests
./gradlew testDebugUnitTest

# Instrumentation tests
./gradlew connectedDebugAndroidTest
```

### Test Coverage
- Unit tests for ViewModels and repositories
- UI tests for Compose screens
- Network tests for API calls
- Validation tests for phone numbers and forms
- Security tests for data protection

## Contributing

### Code Style
- Follow Kotlin coding conventions
- Use AndroidX and Jetpack libraries
- Follow Material Design 3 guidelines
- Write tests for new features

### Pull Requests
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## License

This project is proprietary software. All rights reserved.

## Support

For support or questions, please contact the project maintainer.

## Disclaimer

**IMPORTANT**: This application does not guarantee that any reported account will be banned or suspended. The final decision always belongs to WhatsApp's moderation team. This application only helps users prepare and submit legitimate reports through WhatsApp's official channels.

---

**WhAlert** - Version 1.0.0

Package: `com.whalert.app`

Copyright © 2024 WhAlert
