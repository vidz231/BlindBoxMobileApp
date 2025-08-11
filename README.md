# BlindBox Mobile App

BlindBox is an ecommerce Android application built with Kotlin and follows the Google **Now in Android** best practices.

## Getting Started

### Prerequisites

- [Android Studio](https://developer.android.com/studio) Hedgehog (2023.1.1) or newer
- [JDK 17](https://adoptium.net/) or compatible Java 17 runtime
- Android SDK with compile and target SDKs configured in the project

Some features use Mapbox and Goong services. Replace the placeholder values in `gradle.properties` with your own API keys.

### Clone the repository

```bash
git clone https://github.com/<your-user>/BlindBoxMobileApp.git
cd BlindBoxMobileApp
```

### Build and run

Using Android Studio:
1. Select **File > Open** and choose the project directory.
2. Allow Gradle to sync and download dependencies.
3. Connect an Android device or start an emulator.
4. Press the **Run** button to install and launch the app.

Using the command line:

```bash
./gradlew assembleDebug        # Build a debug APK
./gradlew installDebug         # Install the debug build on a connected device
```

### Run tests

```bash
./gradlew test
```

## License

This project is open source and available under the [Apache License 2.0](LICENSE).
