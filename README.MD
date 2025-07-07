# Compose Multiplatform Application

## Before running!
 - install JDK 17 or higher on your machine  
 - add `local.properties` file to the project root and set a path to Android SDK there  

### Android
To run the application on android device/emulator:  
 - open project in Android Studio and run imported android run configuration  

To build the application bundle:  
 - run `./gradlew :composeApp:assembleDebug`  
 - find `.apk` file in `composeApp/build/outputs/apk/debug/composeApp-debug.apk`  

Run android UI tests on the connected device: `./gradlew :composeApp:connectedDebugAndroidTest`

### Desktop
Run the desktop application: `./gradlew :composeApp:run`  
Run the desktop **hot reload** application: `./gradlew :composeApp:jvmRunHot`  
Run desktop UI tests: `./gradlew :composeApp:jvmTest`  

