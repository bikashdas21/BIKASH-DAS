# ZERO-GRID FREEDOM NETWORK 📡

**Connect Beyond the Grid.**

Zero-Grid is a real native Android application engineered for decentralized peer-to-peer mesh networking, encrypted offline communications, emergency SOS, community repeaters, and authorized Internet gateway access. It operates completely independently of cellular infrastructure, central servers, or commercial ISPs.

---

## 📱 Application Profile
- **App Name**: Zero-Grid
- **Brand**: ZERO-GRID FREEDOM NETWORK
- **Tagline**: Connect Beyond the Grid.
- **Application ID / Package**: `com.zerogrid.freedomnetwork`
- **Version**: 1.0.0 (Version Code: 1)
- **Minimum SDK**: Android 7.0 Nougat (API 24)
- **Target SDK**: Android 15 / 16 (API 36)
- **Architecture**: 100% Kotlin, Jetpack Compose, Material 3, Room SQLite, Android Keystore, Kotlin Coroutines & Flow

---

## 🌐 Honest Architecture & Technical Boundaries

Zero-Grid never fakes networking, simulated users, or internet connectivity:

### 1. Pure Local Freedom Network (Without Internet)
```
Phone A ↔ (Wi-Fi Direct / BLE / Local LAN) ↔ Phone B ↔ Phone C
```
- Completely offline, decentralized communications.
- End-to-end encrypted messaging, walkie-talkie voice notes, GPS coordinate exchanges, and resumable chunked file transfers.
- Multi-hop store-and-forward routing with loop suppression, hop limits (TTL=5), and duplicate packet filters.

### 2. Authorized Internet Gateway (With Verified Uplink)
```
Phone A ↔ Zero-Grid Mesh ↔ Community Node ↔ Authorized Gateway ↔ Satellite / Fiber / 4G WAN ↔ Internet
```
- **Technical Truth**: Zero-Grid does **NOT** promise magical free internet across the globe without physical backhaul. Internet packets only route outward when an authorized node with a real uplink (Starlink, terrestrial fiber, or mobile cell) connects to the mesh.

---

## ⚡ Core System Features

1. **Real Wi-Fi Direct Transport**:
   - `WifiP2pManager` peer group formation on high-speed 5 GHz / 2.4 GHz channels (up to 250 Mbps).
   - Dedicated background TCP server socket (`port 8988`) for packet exchanges.
2. **Bluetooth LE (BLE) Discovery**:
   - Continuous nearby device scanning and advertising for low-power neighbor awareness.
3. **Android Quick Settings Tile (`ZeroGridTileService`)**:
   - Pull down Quick Settings → Tap **ZERO-GRID 📡** to immediately activate or release all radio sockets and background relay services.
4. **Decentralized Security Engine**:
   - Hardware-backed Android Keystore with 2048-bit RSA key generation.
   - AES-GCM-256 packet payload encryption.
   - SHA-256 cryptographic packet signing and replay protection.
5. **High-Priority SOS Alert Engine**:
   - Full-mesh broadcast with audible alarms, device vibration, and offline GPS satellite coordinates.
6. **Chunked Resumable File Transfers**:
   - 32 KB chunking engine with per-chunk SHA-256 verification and automatic retry/resume.
7. **Offline Freedom Services**:
   - Offline community bulletin board, survival & first-aid medical guides, local services hub, and offline knowledge base.
8. **15-Point Hardware Diagnostics Matrix**:
   - Real-time evaluation of Bluetooth, Wi-Fi, Wi-Fi Direct, Wi-Fi Aware, Nearby Discovery, Permissions, Notifications, Battery Optimizations, Microphone, GPS Location, and Gateway Uplink.

---

## 🛠️ Building the Project in Android Studio

1. Clone or export this repository as a `.ZIP` file.
2. Open **Android Studio** (Ladybug / Iguana / Koala or newer).
3. Select **Open an Existing Project** and choose this project root directory.
4. Allow Gradle to sync dependencies.
5. Select your target device or connect an Android phone via USB.

### Build Outputs:
- **Debug APK**: Run `./gradlew assembleDebug`
  - Output: `app/build/outputs/apk/debug/app-debug.apk`
- **Release APK**: Run `./gradlew assembleRelease`
  - Output: `app/build/outputs/apk/release/app-release.apk`
- **Release AAB (Play Store)**: Run `./gradlew :app:bundleRelease`
  - Output: `app/build/outputs/bundle/release/app-release.aab`

For comprehensive release instructions, signing keystore generation, and Google Play Store deployment guidelines, refer to **[RELEASE_GUIDE.md](RELEASE_GUIDE.md)**.
