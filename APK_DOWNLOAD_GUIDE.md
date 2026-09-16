# ZERO-GRID — APK DOWNLOAD & REAL DEVICE INSTALLATION GUIDE

This document details the verified step-by-step pipeline to export your Zero-Grid project from Google AI Studio, build it via GitHub Actions CI, download the genuine `app-release.apk` directly in Google Chrome, and install it on a physical Android phone.

---

## The End-to-End Pipeline

```
Google AI Studio
       ↓
    Export
       ↓
Download as .zip
       ↓
Push / Import project to GitHub
       ↓
 GitHub Actions
       ↓
Run Build APK workflow
       ↓
Open successful workflow run
       ↓
Download Zero-Grid-Release-APK
       ↓
Extract artifact
       ↓
Get REAL app-release.apk
       ↓
Download APK in Chrome
       ↓
Install on real Android device
```

---

## Detailed Step-by-Step Instructions

### Step 1: Google AI Studio → Export
1. In the Google AI Studio top-right navigation bar, click the **Project Menu** (the three vertical dots `⋮` or the Settings gear icon `⚙️`).
2. If available directly, you may click **"Push to GitHub"** and connect your GitHub repository directly.
3. Otherwise, select **"Export Project as ZIP"**.

### Step 2: Download as `.zip`
1. Save the downloaded `.zip` file (e.g., `Zero-Grid.zip`) onto your computer.
2. Extract the ZIP file into a local folder.
3. Notice that the project includes:
   - Root `gradlew` and `gradlew.bat`
   - `gradle/wrapper/gradle-wrapper.jar`
   - Android source code in `app/src/main/`
   - Automated workflow in `.github/workflows/build-apk.yml`

### Step 3: Push / Import Project to GitHub
1. Open [GitHub](https://github.com) in your browser and create a new repository (e.g., `zero-grid`).
2. Push your project to GitHub using git from the extracted folder:
   ```bash
   git init
   git add .
   git commit -m "Initialize Zero-Grid freedom network project"
   git branch -M main
   git remote add origin https://github.com/YOUR_USERNAME/YOUR_REPOSITORY.git
   git push -u origin main
   ```

### Step 4: GitHub Actions
1. In your GitHub repository, click on the **Actions** tab at the top.
2. You will see the **Build & Release Zero-Grid Artifacts** workflow.

### Step 5: Run Build APK Workflow
1. The workflow automatically runs on push to `main` or `master`.
2. To trigger it manually:
   - Click **Build & Release Zero-Grid Artifacts** in the left sidebar under Workflows.
   - Click the **Run workflow** dropdown on the right.
   - Select the branch (`main`) and click **Run workflow**.

### Step 6: Open Successful Workflow Run
1. Watch the workflow execute the steps:
   - Sets up JDK 17
   - Grants executable permissions to `gradlew`
   - Runs `./gradlew assembleRelease --no-daemon --stacktrace`
   - Measures and prints the exact generated APK file size in bytes
   - Runs `./gradlew bundleRelease --no-daemon --stacktrace`
   - Measures and prints the exact generated AAB bundle file size in bytes
2. When the build completes with a green checkmark (PASS), click on the completed run title.

### Step 7: Download `Zero-Grid-Release-APK`
1. Scroll down to the **Artifacts** section at the bottom of the workflow summary page.
2. You will see two genuine build artifacts:
   - **`Zero-Grid-Release-APK`** (Contains the installable `app-release.apk`)
   - **`Zero-Grid-Release-AAB`** (Contains `app-release.aab` for Google Play Store)
3. Click on **`Zero-Grid-Release-APK`** to download it.

### Step 8: Extract Artifact
1. The artifact downloads as `Zero-Grid-Release-APK.zip` from GitHub's artifact packaging.
2. Extract the file on your device or computer.

### Step 9: Get REAL `app-release.apk`
1. Inside the extracted folder is the genuine Android application binary:
   ```
   app-release.apk
   ```
2. This is a real, signed Android package containing compiled `classes.dex`, ARM/x86 native libraries, and the Android manifest. It is **never a fake APK, renamed ZIP, or HTML wrapper**.

### Step 10: Download APK in Chrome on Your Android Phone
1. Transfer or host `app-release.apk` so you can download it via Chrome on your phone:
   - If using GitHub Releases (by creating a version tag like `v1.0.0`), navigate directly to the Release page in Google Chrome on your phone and tap `app-release.apk`.
   - Or upload `app-release.apk` to Google Drive or your preferred private cloud storage and open the download link in Chrome.
2. When Chrome prompts:
   > *"File might be harmful. Do you want to download app-release.apk anyway?"*
3. Tap **Download anyway**.

### Step 11: Install on Real Android Device
1. In Chrome, open **Downloads** from the three-dot menu (or tap the download complete notification in the status bar).
2. Tap on **`app-release.apk`**.
3. If prompted by Android security:
   > *"For your security, your phone is not allowed to install unknown apps from this source."*
   - Tap **Settings**.
   - Toggle ON **"Allow from this source"** for Chrome.
   - Tap the Back button.
4. Tap **Install** when the system asks *"Do you want to install this app?"*.
5. Tap **Open** to launch Zero-Grid!

---

## Testing on Physical Android Devices

Once installed on physical devices:
1. **Quick Settings Tile**: Pull down the notification shade twice, tap the pencil edit icon, and drag the **`ZERO-GRID 📡`** tile into your active tiles. Tap it to toggle Zero-Grid mesh radio on and off without opening the app.
2. **Offline 2-Phone Direct Mesh**:
   - Turn OFF cellular data and Wi-Fi Internet router on both devices.
   - Keep Wi-Fi and Bluetooth hardware switches ON.
   - Open Zero-Grid on Phone A and Phone B → navigate to Nearby → tap Scan → tap Connect.
   - Exchange encrypted offline messages and walkie-talkie voice notes.
3. **Multi-Hop Relay**: Place Phone B between Phone A and Phone C to verify store-and-forward packet relaying across the mesh.
