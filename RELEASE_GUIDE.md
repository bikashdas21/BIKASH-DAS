# Release Guide: ZERO-GRID FREEDOM NETWORK

Connect Beyond the Grid. Real native Android application for decentralized peer-to-peer mesh networking, encrypted offline communications, emergency SOS, community repeaters, and authorized Internet gateway access.

---

## 1. Project Overview & Identity
- **Application Name**: `Zero-Grid`
- **Brand**: `ZERO-GRID FREEDOM NETWORK`
- **Tagline**: `Connect Beyond the Grid.`
- **Package ID / Application ID**: `com.zerogrid.freedomnetwork`
- **Version Name**: `1.0.0`
- **Version Code**: `1`
- **Min Android SDK**: API 24 (Android 7.0 Nougat)
- **Target / Compile Android SDK**: API 36

---

## 2. Release & Build Targets

### 2.1 Debug APK (Physical Device Testing)
Use for testing directly on Android phones over USB or wireless adb:
```bash
./gradlew assembleDebug
```
**Generated Output Location:**
```
app/build/outputs/apk/debug/app-debug.apk
```

### 2.2 Release APK (Direct Distribution / Sideloading)
Use for distributing self-signed or production-signed release APK directly to users:
```bash
# If using release keystore:
./gradlew assembleRelease
```
**Generated Output Location:**
```
app/build/outputs/apk/release/app-release.apk
```

### 2.3 Release AAB (Google Play Store Publishing)
Use for generating the Google Play Store publishing bundle:
```bash
./gradlew :app:bundleRelease
```
**Generated Output Location:**
```
app/build/outputs/bundle/release/app-release.aab
```

---

## 3. Creating & Configuring Your Release Keystore

For security compliance, production signing keys are never hard-coded or committed into Git.

### Step 1: Generate your release keystore locally
Run the following command on your machine:
```bash
keytool -genkey -v -keystore my-upload-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias upload
```

### Step 2: Configure Environment Variables (or gradle.properties)
Set the keystore credentials before running the release build:
```bash
export KEYSTORE_PATH="/path/to/my-upload-key.jks"
export STORE_PASSWORD="your-keystore-password"
export KEY_PASSWORD="your-key-password"
```

In Android Studio:
1. Go to **Build** → **Generate Signed Bundle / APK...**
2. Choose **Android App Bundle** (or **APK**)
3. Select your `my-upload-key.jks`, enter your passwords, and choose alias `upload`
4. Select build variant `release` and click **Create**

---

## 4. APK Installation on Real Android Phones

Zero-Grid requires **NO root access** and **NO operating system modifications**.

### Direct Installation Steps:
1. **Transfer or Download the APK**:
   - Copy `app-debug.apk` (or `app-release.apk`) to your phone via USB cable, Google Drive, SD Card, or web download.
2. **Open Downloads**:
   - On your phone, open the **Files** or **Downloads** app.
3. **Tap the APK**:
   - Tap `app-debug.apk` (or `app-release.apk`).
4. **Grant Unknown Sources Permission**:
   - If prompted: *"For your security, your phone is not allowed to install unknown apps from this source"*, tap **Settings** and toggle **Allow from this source**.
5. **Install & Launch**:
   - Tap **Install**.
   - Once complete, tap **Open**.
6. **First Launch Configuration**:
   - On the First Launch screen, choose your language (English / বাংলা), create your profile name, and tap **"Start Networking"**.

---

## 5. Google Play Store Readiness & Checklist

### 5.1 Play Console Upload Steps
1. Log in to [Google Play Console](https://play.google.com/console).
2. Select your application or create a new application named **Zero-Grid**.
3. Under **Release** → **Production** (or **Internal Testing**), click **Create new release**.
4. Drag and drop `app/build/outputs/bundle/release/app-release.aab`.
5. Enter Release notes:
   ```
   Initial release of Zero-Grid Freedom Network:
   - Real Wi-Fi Direct and Bluetooth mesh networking
   - End-to-end encrypted peer messaging
   - Emergency SOS broadcast
   - Resumable chunked file sharing
   - Android Quick Settings Tile for zero-grid power control
   ```
6. Review release and submit for rollout.

### 5.2 Google Play Policy & Data Safety Declarations
- **Zero-Permission Photo Picker**: Uses Android system Photo Picker without requiring legacy broad storage permissions (`READ_EXTERNAL_STORAGE` is strictly avoided).
- **Location Permission (`ACCESS_FINE_LOCATION`)**: Required solely for local peer Wi-Fi Direct radio scanning and manual GPS coordinate exchange in SOS alerts. Data is stored strictly on-device in Room database and never uploaded to any remote tracking server.
- **Microphone Permission (`RECORD_AUDIO`)**: Used exclusively for local offline voice note walkie-talkie messaging. Audio recordings remain local or are relayed directly to peers over mesh.
- **Foreground Service (`connectedDevice`)**: Used by `MeshRelayService` to maintain local Wi-Fi Direct and BLE packet listening when the phone screen turns off.
- **No Third-Party Analytics Trackers**: No third-party behavioral tracking or ad SDKs are bundled.

---

## 6. Real Device Testing Protocol

### Test 1: Phone A ↔ Phone B (Offline Local Messaging)
1. Turn **Mobile Data OFF** and **Home Wi-Fi router OFF** on both phones (keep Wi-Fi radio and Bluetooth radio turned ON in system toggles).
2. Open Zero-Grid on Phone A and Phone B.
3. On Phone A, go to **Nearby** and tap **Scan**.
4. Select Phone B and tap **Connect**. Accept the Wi-Fi Direct prompt on Phone B.
5. Open **Chat**, type a message, and send.
6. **Verification**: Confirm message arrives on Phone B with verified RSA signature and encryption indicator.

### Test 2: Multi-Hop Relay (Phone A → Phone B → Phone C)
1. Position Phone A and Phone C out of direct radio range of each other, with Phone B in the middle.
2. Enable **Relay Mode** in **Settings** on Phone B.
3. Phone A sends a packet destined for Phone C or Broadcast.
4. **Verification**: Phone B automatically decrements TTL, increments hop count, and forwards packet to Phone C. Phone C displays the decrypted message.

### Test 3: Large File / Photo Transfer
1. On Phone A, go to **File Share** → tap **Select File / Photo**.
2. Select a photo or document. Tap **Send to Mesh**.
3. **Verification**: Progress bar updates chunk-by-chunk (32 KB chunks) with SHA-256 verification and automatic resume capability.

### Test 4: Emergency SOS
1. From the Home screen, tap **Emergency SOS**.
2. Confirm the alert warning dialog.
3. **Verification**: High-priority alert packet (`ALERT_SOS`) broadcasts across all connected radios. Nearby nodes trigger high-priority alerts with GPS coordinate badges.

### Test 5: Quick Settings ZERO-GRID Tile
1. Pull down the Android Notification Shade / Quick Settings panel on your phone.
2. Tap the **Edit / Pen** icon to add tiles.
3. Drag **ZERO-GRID 📡** into your active tiles list.
4. Tap **ZERO-GRID 📡** to toggle ON/OFF:
   - **ON**: Mesh listening service starts, foreground notification displays, status shows "Mesh Active".
   - **OFF**: Radios unbind, background sockets close, status shows "Mesh Offline".

### Test 6: Authorized Internet Gateway
1. Designate a node (e.g., Phone C) with active cellular or satellite backhaul.
2. In Gateway settings, authorize Phone C as a Community Gateway.
3. Nodes A and B route external requests through Phone C to the gateway uplink without fake Internet simulation.

---

## 7. Version Updates
To release an update, modify `/app/build.gradle.kts`:
```kotlin
defaultConfig {
    versionCode = 2          // Increment integer by 1 for each Play Store release
    versionName = "1.0.1"    // Semver user-visible version
}
```
Recompile with `./gradlew :app:bundleRelease` and upload the new `.aab` to Play Console.
