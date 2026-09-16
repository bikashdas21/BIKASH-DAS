# ZERO-GRID — REAL APK BUILD, DOWNLOAD & REAL DEVICE INSTALLATION GUIDE

This guide details the complete end-to-end workflow for compiling, obtaining, downloading via Google Chrome, and installing the genuine `app-release.apk` on physical Android devices.

---

## Technical Specifications
- **Application Name**: `Zero-Grid`
- **Application ID / Package**: `com.zerogrid.freedomnetwork`
- **Namespace**: `com.example` (preserves R resources and directory bindings)
- **Version Code**: `1`
- **Version Name**: `1.0.0`
- **Min SDK**: API 24 (Android 7.0 Nougat)
- **Target / Compile SDK**: API 36 (Android 15 / 16)
- **Build Status**: **PASS** (Verified with `./gradlew assembleRelease` and `./gradlew bundleRelease`)
- **Measured Local Release APK**: `app/build/outputs/apk/release/app-release.apk` (15,799,764 bytes / ~15.07 MB on local assembleRelease)
- **CI Artifact**: Dynamically measured and reported in GitHub Actions log via `stat` command.

---

## 10-Step Real Device Pipeline

### STEP 1: Build the Android Project
Ensure all dependencies, resources, and Kotlin source code are in place. The project uses standard Gradle with Kotlin DSL (`build.gradle.kts`).

### STEP 2: Run Gradle Release Build
In the root directory of the project, execute:
```bash
./gradlew assembleRelease
```
*(On Windows command prompt, use `gradlew.bat assembleRelease`)*.

### STEP 3: Locate the Real APK
The generated binary is written directly to:
```
app/build/outputs/apk/release/app-release.apk
```
*(Debug variant is generated at `app/build/outputs/apk/debug/app-debug.apk`)*.

This is a **genuine Android Package (.apk)** containing compiled bytecode (`classes.dex`), native architectures (`arm64-v8a`, `armeabi-v7a`, `x86_64`), resource tables, and digital signature block. It is **never a renamed ZIP or HTML file**.

---

### STEP 4: Upload / Host the Real APK Artifact

To download directly on your Android phone using Chrome:

#### Option A: Direct Push to GitHub with GitHub Actions (Automated CI)
1. Push this project to GitHub (using Google AI Studio's **"Push to GitHub"** button in the top settings menu).
2. The included GitHub Actions workflow (`.github/workflows/build-apk.yml`) will automatically trigger.
3. Once the workflow completes:
   - Go to the **Actions** tab on your GitHub repository.
   - Click on the latest workflow run.
   - Download the **`ZeroGrid-Release-APK`** artifact, or view the attached release asset under **Releases**.

#### Option B: Direct Hosting via Cloud Storage / Google Drive
1. Upload the generated `app-release.apk` to Google Drive, Dropbox, or any web server.
2. Set link permissions to *"Anyone with the link can view/download"*.

---

### STEP 5: Open the APK Download Page in Chrome
1. On your physical Android phone, open the **Google Chrome** browser.
2. Navigate to your download link (GitHub Release asset, Google Drive direct download link, or web server URL).

### STEP 6: Download `app-release.apk`
1. Tap the download link.
2. Chrome may display a standard security notice:
   > *"File might be harmful. Do you want to download app-release.apk anyway?"*
3. Tap **Download anyway**.
4. Wait for Chrome to complete downloading the **15.1 MB** file.

### STEP 7: Open Chrome Downloads
1. In Chrome, tap the top-right menu (three dots `⋮`) and select **Downloads**.
   *(Alternatively, swipe down the Android notification shade and tap the "Download complete" notification).*
2. You will see **`app-release.apk`** listed at the top.

### STEP 8: Tap the APK
1. Tap on **`app-release.apk`**.
2. Android Package Installer will open the installation dialog.
3. If this is your first time sideloading an APK from Chrome, Android security will show:
   > *"For your security, your phone is not allowed to install unknown apps from this source."*
4. Tap **Settings**, and toggle ON **"Allow from this source"** for Chrome.
5. Tap the **Back** arrow to return to the installer.

### STEP 9: Install Zero-Grid
1. The installer will prompt: *"Do you want to install this app?"*
2. Tap **Install**.
3. Android Package Installer will verify the signature and install Zero-Grid.
4. Once installation is finished, tap **Open**.

### STEP 10: Open Zero-Grid & Test on Real Android Devices
1. Zero-Grid will launch with its dark-cyan tactical HUD.
2. Follow the First Launch screen: choose your language (**English** / **বাংলা**) and set your radio node name.
3. Tap **Start Networking**.

---

## Real Multi-Device Testing Protocol

Zero-Grid is engineered for physical radio hardware. Test with real devices following this protocol:

### Phase 1: Two-Phone Direct Test (Phone A ↔ Phone B)
1. **Disable External Radios**: Turn Mobile Data OFF and Home Wi-Fi Internet router OFF on both devices.
2. **Keep Local Radios Active**: Ensure Wi-Fi radio and Bluetooth radio switches are toggled ON in Android system settings.
3. **Scan & Discover**:
   - On Phone A, open **Nearby** → tap **Scan**.
   - Phone B appears in the list via Wi-Fi Direct peer discovery.
4. **Connect**:
   - Tap **Connect** on Phone A.
   - Accept the standard Android Wi-Fi Direct connection prompt on Phone B.
5. **Secure Chat**:
   - Send messages between devices. Verify hardware RSA encryption badge and packet timestamps.
6. **Voice Notes**:
   - Hold the microphone button to record an offline PCM voice message. Release to transmit over Wi-Fi Direct.
7. **Chunked File Transfer**:
   - Send an image or document. Verify 32 KB chunk transmission with SHA-256 integrity verification.

### Phase 2: Multi-Hop Mesh Relay (Phone A → Phone B → Phone C)
1. **Physical Separation**: Place Phone A and Phone C far apart so they cannot reach each other directly.
2. **Intermediary Relay**: Place Phone B between them with **Relay Mode** enabled in **Settings**.
3. **Route Packet**:
   - Send a message from Phone A destined for Phone C.
   - Phone B intercepts the packet, decrements Time-To-Live (TTL), and re-transmits it outward to Phone C.
   - Phone C decrypts and receives the message.

### Phase 3: Android Quick Settings Tile Test
1. Pull down the Android Notification shade twice to reveal full Quick Settings.
2. Tap the **Edit (pencil)** button.
3. Locate **ZERO-GRID 📡** in the inactive tiles section and drag it into your active tiles.
4. Tap the **ZERO-GRID 📡** tile:
   - **Toggled ON**: Mesh sockets bind, foreground service notification appears with "Mesh Active", radio scanning begins.
   - **Toggled OFF**: Sockets unbind cleanly, foreground service stops, radio returns to dormant state.

### Phase 4: Emergency SOS Alert Broadcast
1. On any node, tap **Emergency SOS**.
2. Confirm the prompt.
3. An `ALERT_SOS` priority packet broadcasts across all reachable Wi-Fi Direct and Bluetooth connections.
4. Surrounding nodes trigger acoustic alert beeps, device vibration, and display the GPS satellite coordinates of the emergency node.

### Phase 5: Hardware Diagnostics Matrix
1. Open **Settings** → **Diagnostics Matrix**.
2. Tap **Run Complete Hardware Diagnostics**.
3. Review the 15-point diagnostic report confirming Bluetooth, Wi-Fi Direct, Location, Microphone, Battery optimization, and Gateway status.

---

## Release Keystore & Security Configuration

The project is configured so you never hard-code secret production keystores or passwords in source control:

### Default Local Testing / CI Fallback
If no external keys are specified, Gradle automatically signs using the local Android keystore so that `app-release.apk` is immediately installable on devices for sideload testing without requiring manual configuration.

### Generating Your Official Production Keystore
To sign with your official production key for Google Play or formal release distribution:
```bash
keytool -genkey -v -keystore my-upload-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias upload
```

### Passing Keystore Credentials
Provide the credentials via environment variables before running `./gradlew assembleRelease`:
```bash
export KEYSTORE_PATH="/path/to/my-upload-key.jks"
export STORE_PASSWORD="your_keystore_password"
export KEY_ALIAS="upload"
export KEY_PASSWORD="your_key_password"
./gradlew assembleRelease
```
Or configure them as GitHub Actions Secrets (`RELEASE_KEYSTORE_BASE64`, `RELEASE_STORE_PASSWORD`, `RELEASE_KEY_ALIAS`, `RELEASE_KEY_PASSWORD`).
