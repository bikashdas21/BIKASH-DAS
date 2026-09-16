package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ZeroGridApplication
import com.example.community.GlobalCommunityNode
import com.example.community.GlobalCommunityNodeRegistry
import com.example.core.crypto.CryptoEngine
import com.example.database.Message
import com.example.database.Transfer
import com.example.diagnostics.GlobalTestMatrixRunner
import com.example.diagnostics.TestCase
import com.example.gateway.GatewayInfo
import com.example.gateway.GatewayManager
import com.example.gateway.NetworkLayer
import com.example.location.LocationShareMode
import com.example.location.OfflineLocation
import com.example.mesh.MeshRelayService
import com.example.mesh.ZeroGridTileService
import com.example.transport.DiscoveredPeer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Nearby : Screen("nearby")
    data object Chat : Screen("chat")
    data object Network : Screen("network")
    data object Settings : Screen("settings")
    data object Location : Screen("location")
    data object Sos : Screen("sos")
    data object Files : Screen("files")
    data object Community : Screen("community")
    data object Map : Screen("map")
    data object Diagnostics : Screen("diagnostics")
    data object FirstLaunch : Screen("first_launch")
    data object PermissionCenter : Screen("permission_center")
    data object TwoPhoneTest : Screen("two_phone_test")
    data object FreedomServices : Screen("freedom_services")
    data object Gateway : Screen("gateway")
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as ZeroGridApplication

    val isFirstLaunch = app.preferencesManager.isFirstLaunch

    private val _currentScreen = MutableStateFlow<Screen>(
        if (app.preferencesManager.isFirstLaunch.value) Screen.FirstLaunch else Screen.Home
    )
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    val networkState = app.meshRouter.networkState
    val hasGateway = app.meshRouter.hasGateway
    val incomingSos = app.meshRouter.incomingSosAlert

    val discoveredPeers = app.transportManager.allDiscoveredPeers
    val isScanning = app.transportManager.isScanning
    val activeTransport = app.transportManager.activeTransport

    val country = app.preferencesManager.country
    val timeZone = app.preferencesManager.timeZone
    val language = app.preferencesManager.language
    val is24Hour = app.preferencesManager.is24Hour
    val isMetric = app.preferencesManager.isMetric
    val isLowBandwidth = app.preferencesManager.isLowBandwidth
    val isDarkMode = app.preferencesManager.isDarkMode
    val isRelayMode = app.preferencesManager.isRelayMode
    val isBatterySaver = app.preferencesManager.isBatterySaver
    val isGuestMode = app.preferencesManager.isGuestMode
    val isZeroGridActive = app.preferencesManager.isZeroGridActive
    val familyPhone = app.preferencesManager.familyPhone
    val medicalPhone = app.preferencesManager.medicalPhone

    fun toggleZeroGridActive(active: Boolean) {
        app.preferencesManager.setZeroGridActive(active)
        if (active) {
            MeshRelayService.startService(app)
            app.meshRouter.activate()
        } else {
            MeshRelayService.stopService(app)
            app.meshRouter.deactivate()
        }
        ZeroGridTileService.requestTileUpdate(app)
    }

    val myNodeId = app.preferencesManager.myNodeId
    private val _displayName = MutableStateFlow(app.preferencesManager.myDisplayName)
    val displayName: StateFlow<String> = _displayName.asStateFlow()

    val keyFingerprint: String = CryptoEngine.getPublicKeyFingerprint()

    val currentLocation = app.locationManager.currentLocation
    val locationShareMode = app.locationManager.shareMode
    val voiceEngine = app.voiceEngine

    val allMessages: StateFlow<List<Message>> = app.database.messageDao()
        .getAllMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTransfers: StateFlow<List<Transfer>> = app.database.transferDao()
        .getAllTransfers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeChatPeerId = MutableStateFlow("BROADCAST")
    val activeChatPeerId: StateFlow<String> = _activeChatPeerId.asStateFlow()

    private val _activeChatPeerName = MutableStateFlow("Public Mesh Broadcast")
    val activeChatPeerName: StateFlow<String> = _activeChatPeerName.asStateFlow()

    // Global Community & Solar Nodes
    private val _communityNodes = MutableStateFlow<List<GlobalCommunityNode>>(GlobalCommunityNodeRegistry.SAMPLE_GLOBAL_NODES)
    val communityNodes: StateFlow<List<GlobalCommunityNode>> = _communityNodes.asStateFlow()

    private val _selectedNodeId = MutableStateFlow<String?>(null)
    val selectedNodeId: StateFlow<String?> = _selectedNodeId.asStateFlow()

    // Diagnostics / Test Matrix
    private val testRunner = GlobalTestMatrixRunner(app)
    private val _testResults = MutableStateFlow<List<TestCase>>(emptyList())
    val testResults: StateFlow<List<TestCase>> = _testResults.asStateFlow()

    // Gateway Manager
    val gatewayManager = GatewayManager()
    val currentNetworkLayer: StateFlow<NetworkLayer> = gatewayManager.currentLayer
    val activeGatewayInfo: StateFlow<GatewayInfo?> = gatewayManager.activeGateway

    init {
        // Run initial diagnostic pass
        runDiagnostics()

        // Seed initial welcome message in the database if empty
        viewModelScope.launch(Dispatchers.IO) {
            val initial = app.database.messageDao().getMessageById("INIT-WELCOME")
            if (initial == null) {
                app.database.messageDao().insertMessage(
                    Message(
                        messageId = "INIT-WELCOME",
                        senderId = "SYSTEM",
                        senderName = "Zero-Grid Freedom Network",
                        receiverId = "BROADCAST",
                        encryptedPayload = CryptoEngine.encryptPayload("Zero-Grid Global Mesh online. Communicating device-to-device with hardware-backed encryption. No cellular or internet infrastructure required."),
                        plaintextCache = "Zero-Grid Global Mesh online. Communicating device-to-device with hardware-backed encryption. No cellular or internet infrastructure required.",
                        timestamp = System.currentTimeMillis() - 60000,
                        status = "DELIVERED"
                    )
                )
            }
        }
    }

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun runDiagnostics() {
        viewModelScope.launch(Dispatchers.Default) {
            _testResults.value = testRunner.executeAllTests()
        }
    }

    fun selectMapNode(nodeId: String?) {
        _selectedNodeId.value = nodeId
    }

    fun startScan() {
        app.transportManager.scanAll()
    }

    fun toggleConnectPeer(peer: DiscoveredPeer) {
        viewModelScope.launch(Dispatchers.IO) {
            if (peer.isConnected) {
                app.transportManager.disconnectPeer(peer.deviceId)
            } else {
                app.transportManager.connectToPeer(peer.deviceId)
            }
        }
    }

    fun openChatWith(peerId: String, peerName: String) {
        _activeChatPeerId.value = peerId
        _activeChatPeerName.value = peerName
        _currentScreen.value = Screen.Chat
    }

    fun sendTextMessage(text: String) {
        app.meshRouter.sendTextMessage(
            receiverId = _activeChatPeerId.value,
            content = text
        )
    }

    fun sendVoiceMessage(audioPath: String, durationSec: Int) {
        app.meshRouter.sendVoiceMessage(
            receiverId = _activeChatPeerId.value,
            audioFilePath = audioPath,
            durationSeconds = durationSec
        )
    }

    fun broadcastSos(note: String, attachLocation: Boolean) {
        val loc = if (attachLocation) currentLocation.value else null
        val locString = if (loc != null) " [GPS: ${loc.latitude}, ${loc.longitude}, Acc: ${loc.accuracyMeters}m]" else ""
        app.meshRouter.sendTextMessage(
            receiverId = "BROADCAST",
            content = "🚨 EMERGENCY SOS: $note$locString",
            isSos = true,
            lat = loc?.latitude,
            lon = loc?.longitude
        )
        _currentScreen.value = Screen.Chat
    }

    fun requestLocationMode(mode: LocationShareMode) {
        app.locationManager.requestLocation(mode)
    }

    fun shareLocationOnMesh(loc: OfflineLocation) {
        val coordStr = "📍 Offline GNSS Coordinates: ${loc.latitude}, ${loc.longitude} (Accuracy: ~${loc.accuracyMeters.toInt()}m)"
        app.meshRouter.sendTextMessage(
            receiverId = "BROADCAST",
            content = coordStr,
            lat = loc.latitude,
            lon = loc.longitude
        )
    }

    fun createSampleTransfer() {
        viewModelScope.launch(Dispatchers.IO) {
            val sampleFile = File(app.cacheDir, "ZeroGrid_Global_Mesh_Manual.pdf")
            if (!sampleFile.exists()) {
                sampleFile.writeText("Zero-Grid Freedom Network Global Decentralized Communication Specification.")
            }
            app.fileTransferManager.prepareOutgoingTransfer(sampleFile)
        }
    }

    fun resumeTransfer(transfer: Transfer) {
        viewModelScope.launch(Dispatchers.IO) {
            val updated = transfer.copy(status = "IN_PROGRESS")
            app.database.transferDao().updateTransfer(updated)
        }
    }

    fun pauseTransfer(transfer: Transfer) {
        viewModelScope.launch(Dispatchers.IO) {
            val updated = transfer.copy(status = "PAUSED")
            app.database.transferDao().updateTransfer(updated)
        }
    }

    fun updateDisplayName(name: String) {
        app.preferencesManager.setDisplayName(name)
        _displayName.value = name
    }

    fun setCountry(countryCode: String) {
        app.preferencesManager.setCountry(countryCode)
    }

    fun setTimeZone(tzId: String) {
        app.preferencesManager.setTimeZone(tzId)
    }

    fun setLanguage(lang: String) {
        app.preferencesManager.setLanguage(lang)
    }

    fun set24Hour(is24: Boolean) {
        app.preferencesManager.set24Hour(is24)
    }

    fun setMetric(metric: Boolean) {
        app.preferencesManager.setMetric(metric)
    }

    fun setLowBandwidth(enabled: Boolean) {
        app.preferencesManager.setLowBandwidth(enabled)
    }

    fun setFamilyPhone(phone: String) {
        app.preferencesManager.setFamilyPhone(phone)
    }

    fun setMedicalPhone(phone: String) {
        app.preferencesManager.setMedicalPhone(phone)
    }

    fun toggleRelayMode(enabled: Boolean) {
        app.preferencesManager.setRelayMode(enabled)
        if (enabled) {
            MeshRelayService.startService(app)
        } else {
            MeshRelayService.stopService(app)
        }
    }

    fun toggleBatterySaver(enabled: Boolean) {
        app.preferencesManager.setBatterySaver(enabled)
    }

    fun toggleDarkMode(enabled: Boolean) {
        app.preferencesManager.setDarkMode(enabled)
    }

    fun clearAllData() {
        viewModelScope.launch(Dispatchers.IO) {
            app.database.messageDao().clearMessages()
            app.database.deviceDao().clearDevices()
            app.database.transferDao().clearTransfers()
            app.database.networkNodeDao().clearNodes()
        }
    }

    fun dismissSosDialog() {
        app.meshRouter.dismissSosAlert()
    }

    fun completeFirstLaunch(asGuest: Boolean) {
        app.preferencesManager.completeFirstLaunch(asGuest)
        _currentScreen.value = Screen.Home
    }

    fun resetFirstLaunch() {
        app.preferencesManager.resetFirstLaunch()
        _currentScreen.value = Screen.FirstLaunch
    }

    suspend fun sendRealTestPacket(targetDeviceId: String?, testMessage: String): Pair<Boolean, String> {
        return app.transportManager.sendRealTestPacket(targetDeviceId, testMessage)
    }
}
