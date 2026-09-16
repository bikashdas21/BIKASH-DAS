package com.example

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.localization.LanguageRegistry
import com.example.localization.Strings
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.CommunityNodesListView
import com.example.ui.screens.DiagnosticsScreen
import com.example.ui.screens.FileTransferScreen
import com.example.ui.screens.FirstLaunchScreen
import com.example.ui.screens.FreedomServicesScreen
import com.example.ui.screens.GatewayScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LocationScreen
import com.example.ui.screens.MapScreen
import com.example.ui.screens.NearbyScreen
import com.example.ui.screens.NetworkTopologyScreen
import com.example.ui.screens.PermissionCenterScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SosScreen
import com.example.ui.screens.TwoPhoneTestScreen
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.MeshGreen
import com.example.ui.theme.ZeroGridTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDark by viewModel.isDarkMode.collectAsState()
            val language by viewModel.language.collectAsState()

            val layoutDirection = if (LanguageRegistry.isRtl(language)) {
                LayoutDirection.Rtl
            } else {
                LayoutDirection.Ltr
            }

            ZeroGridTheme(darkTheme = isDark) {
                CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                    MainContent(viewModel = viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(
    viewModel: MainViewModel
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val networkState by viewModel.networkState.collectAsState()
    val hasGateway by viewModel.hasGateway.collectAsState()
    val discoveredPeers by viewModel.discoveredPeers.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()
    val activeTransport by viewModel.activeTransport.collectAsState()

    val myNodeId = viewModel.myNodeId
    val displayName by viewModel.displayName.collectAsState()
    val fingerprint = viewModel.keyFingerprint
    val isRelayMode by viewModel.isRelayMode.collectAsState()
    val isBatterySaver by viewModel.isBatterySaver.collectAsState()
    val isZeroGridActive by viewModel.isZeroGridActive.collectAsState()
    val isDark by viewModel.isDarkMode.collectAsState()
    val countryCode by viewModel.country.collectAsState()
    val timeZone by viewModel.timeZone.collectAsState()
    val language by viewModel.language.collectAsState()
    val is24Hour by viewModel.is24Hour.collectAsState()
    val isMetric by viewModel.isMetric.collectAsState()
    val isLowBandwidth by viewModel.isLowBandwidth.collectAsState()
    val familyPhone by viewModel.familyPhone.collectAsState()
    val medicalPhone by viewModel.medicalPhone.collectAsState()

    val messages by viewModel.allMessages.collectAsState()
    val activeChatPeerId by viewModel.activeChatPeerId.collectAsState()
    val activeChatPeerName by viewModel.activeChatPeerName.collectAsState()

    val transfers by viewModel.allTransfers.collectAsState()
    val currentLocation by viewModel.currentLocation.collectAsState()
    val shareMode by viewModel.locationShareMode.collectAsState()
    val incomingSos by viewModel.incomingSos.collectAsState()

    val communityNodes by viewModel.communityNodes.collectAsState()
    val selectedNodeId by viewModel.selectedNodeId.collectAsState()
    val testResults by viewModel.testResults.collectAsState()

    val isBangla = language == "bn"

    // Request essential Nearby and Notification permissions
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        // Permissions handled
    }

    LaunchedEffect(Unit) {
        val perms = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            perms.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            perms.add(Manifest.permission.BLUETOOTH_SCAN)
            perms.add(Manifest.permission.BLUETOOTH_CONNECT)
            perms.add(Manifest.permission.BLUETOOTH_ADVERTISE)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            perms.add(Manifest.permission.NEARBY_WIFI_DEVICES)
        }
        if (perms.isNotEmpty()) {
            permissionLauncher.launch(perms.toTypedArray())
        }
    }

    val isTopLevel = currentScreen is Screen.Home ||
            currentScreen is Screen.Nearby ||
            currentScreen is Screen.Chat ||
            currentScreen is Screen.Network ||
            currentScreen is Screen.Settings

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            if (!isTopLevel && currentScreen !is Screen.FirstLaunch && currentScreen !is Screen.PermissionCenter && currentScreen !is Screen.TwoPhoneTest) {
                TopAppBar(
                    title = {
                        Text(
                            text = when (currentScreen) {
                                Screen.Location -> "Offline Location"
                                Screen.Sos -> Strings.get("sos_title", language)
                                Screen.Files -> Strings.get("tab_files", language)
                                Screen.Community -> "Community Nodes"
                                Screen.Map -> Strings.get("tab_map", language)
                                Screen.Diagnostics -> Strings.get("tab_diagnostics", language)
                                else -> "Zero-Grid"
                            },
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.navigateTo(Screen.Home) }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            }
        },
        bottomBar = {
            if (isTopLevel && currentScreen !is Screen.FirstLaunch) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = currentScreen is Screen.Home,
                        onClick = { viewModel.navigateTo(Screen.Home) },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text(Strings.get("tab_home", language)) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ElectricCyan,
                            selectedTextColor = ElectricCyan,
                            indicatorColor = ElectricCyan.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("nav_home")
                    )

                    NavigationBarItem(
                        selected = currentScreen is Screen.Nearby,
                        onClick = { viewModel.navigateTo(Screen.Nearby) },
                        icon = { Icon(Icons.Default.People, contentDescription = "Nearby") },
                        label = { Text(Strings.get("tab_nearby", language)) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MeshGreen,
                            selectedTextColor = MeshGreen,
                            indicatorColor = MeshGreen.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("nav_nearby")
                    )

                    NavigationBarItem(
                        selected = currentScreen is Screen.Chat,
                        onClick = { viewModel.navigateTo(Screen.Chat) },
                        icon = { Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Chat") },
                        label = { Text(Strings.get("tab_chat", language)) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ElectricCyan,
                            selectedTextColor = ElectricCyan,
                            indicatorColor = ElectricCyan.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("nav_chat")
                    )

                    NavigationBarItem(
                        selected = currentScreen is Screen.Network,
                        onClick = { viewModel.navigateTo(Screen.Network) },
                        icon = { Icon(Icons.Default.WifiTethering, contentDescription = "Topology") },
                        label = { Text("Topology") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ElectricCyan,
                            selectedTextColor = ElectricCyan,
                            indicatorColor = ElectricCyan.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("nav_network")
                    )

                    NavigationBarItem(
                        selected = currentScreen is Screen.Settings,
                        onClick = { viewModel.navigateTo(Screen.Settings) },
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                        label = { Text(Strings.get("tab_settings", language)) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ElectricCyan,
                            selectedTextColor = ElectricCyan,
                            indicatorColor = ElectricCyan.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("nav_settings")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                is Screen.Home -> {
                    val connectedCount = discoveredPeers.count { it.isConnected }
                    val nearest = discoveredPeers.minByOrNull { it.distanceMeters }?.distanceMeters?.toInt() ?: 320
                    HomeScreen(
                        networkState = networkState,
                        connectedCount = connectedCount,
                        nearestDistanceMeters = nearest,
                        connectionTransport = activeTransport,
                        myNodeId = myNodeId,
                        countryCode = countryCode,
                        language = language,
                        isMetric = isMetric,
                        isRelayMode = isRelayMode,
                        isZeroGridActive = isZeroGridActive,
                        onToggleZeroGrid = { active -> viewModel.toggleZeroGridActive(active) },
                        onNavigateToChat = { viewModel.navigateTo(Screen.Chat) },
                        onNavigateToNearby = { viewModel.navigateTo(Screen.Nearby) },
                        onNavigateToNetwork = { viewModel.navigateTo(Screen.Network) },
                        onNavigateToFreedomServices = { viewModel.navigateTo(Screen.FreedomServices) },
                        onNavigateToLocation = { viewModel.navigateTo(Screen.Location) },
                        onNavigateToSos = { viewModel.navigateTo(Screen.Sos) },
                        onNavigateToFiles = { viewModel.navigateTo(Screen.Files) },
                        onNavigateToCommunity = { viewModel.navigateTo(Screen.Community) },
                        onNavigateToGateway = { viewModel.navigateTo(Screen.Gateway) },
                        onNavigateToSettings = { viewModel.navigateTo(Screen.Settings) },
                        onNavigateToMap = { viewModel.navigateTo(Screen.Map) },
                        onNavigateToDiagnostics = { viewModel.navigateTo(Screen.Diagnostics) },
                        onNavigateToTwoPhoneTest = { viewModel.navigateTo(Screen.TwoPhoneTest) },
                        onNavigateToPermissionCenter = { viewModel.navigateTo(Screen.PermissionCenter) }
                    )
                }

                is Screen.Nearby -> {
                    NearbyScreen(
                        peers = discoveredPeers,
                        isScanning = isScanning,
                        isBangla = isBangla,
                        onScan = { viewModel.startScan() },
                        onConnectToggle = { peer -> viewModel.toggleConnectPeer(peer) },
                        onOpenChat = { peer -> viewModel.openChatWith(peer.deviceId, peer.name) }
                    )
                }

                is Screen.Chat -> {
                    ChatScreen(
                        messages = messages,
                        activePeerId = activeChatPeerId,
                        activePeerName = activeChatPeerName,
                        myNodeId = myNodeId,
                        voiceEngine = viewModel.voiceEngine,
                        isBangla = isBangla,
                        onSendMessage = { text -> viewModel.sendTextMessage(text) },
                        onSendVoiceMessage = { path, dur -> viewModel.sendVoiceMessage(path, dur) },
                        onSelectChannel = { id, name -> viewModel.openChatWith(id, name) }
                    )
                }

                is Screen.Network -> {
                    NetworkTopologyScreen(
                        peers = discoveredPeers,
                        myNodeId = myNodeId,
                        hasGateway = hasGateway,
                        language = language,
                        globalNodes = communityNodes,
                        onSelectGlobalNode = { nodeId ->
                            viewModel.selectMapNode(nodeId)
                            viewModel.navigateTo(Screen.Map)
                        }
                    )
                }

                is Screen.Map -> {
                    MapScreen(
                        nodes = communityNodes,
                        selectedNodeId = selectedNodeId,
                        language = language,
                        timeZone = timeZone,
                        is24Hour = is24Hour,
                        onSelectNode = { nodeId -> viewModel.selectMapNode(nodeId) },
                        onOpenChat = { nodeId, nodeName -> viewModel.openChatWith(nodeId, nodeName) }
                    )
                }

                is Screen.Diagnostics -> {
                    DiagnosticsScreen(
                        testCases = testResults,
                        language = language,
                        onRerunDiagnostics = { viewModel.runDiagnostics() }
                    )
                }

                is Screen.Settings -> {
                    SettingsScreen(
                        myNodeId = myNodeId,
                        displayName = displayName,
                        fingerprint = fingerprint,
                        countryCode = countryCode,
                        timeZone = timeZone,
                        language = language,
                        is24Hour = is24Hour,
                        isMetric = isMetric,
                        isLowBandwidth = isLowBandwidth,
                        isRelayMode = isRelayMode,
                        isBatterySaver = isBatterySaver,
                        isDarkMode = isDark,
                        familyPhone = familyPhone,
                        medicalPhone = medicalPhone,
                        onUpdateDisplayName = { name -> viewModel.updateDisplayName(name) },
                        onSetCountry = { code -> viewModel.setCountry(code) },
                        onSetTimeZone = { tz -> viewModel.setTimeZone(tz) },
                        onSetLanguage = { lang -> viewModel.setLanguage(lang) },
                        onSet24Hour = { is24 -> viewModel.set24Hour(is24) },
                        onSetMetric = { m -> viewModel.setMetric(m) },
                        onSetLowBandwidth = { lb -> viewModel.setLowBandwidth(lb) },
                        onToggleRelayMode = { en -> viewModel.toggleRelayMode(en) },
                        onToggleBatterySaver = { en -> viewModel.toggleBatterySaver(en) },
                        onToggleDarkMode = { en -> viewModel.toggleDarkMode(en) },
                        onSetFamilyPhone = { p -> viewModel.setFamilyPhone(p) },
                        onSetMedicalPhone = { p -> viewModel.setMedicalPhone(p) },
                        onClearAllData = { viewModel.clearAllData() },
                        onNavigateToPermissionCenter = { viewModel.navigateTo(Screen.PermissionCenter) },
                        onNavigateToTwoPhoneTest = { viewModel.navigateTo(Screen.TwoPhoneTest) },
                        onResetFirstLaunch = { viewModel.resetFirstLaunch() }
                    )
                }

                is Screen.Location -> {
                    LocationScreen(
                        currentLocation = currentLocation,
                        shareMode = shareMode,
                        isBangla = isBangla,
                        onRequestLocationMode = { mode -> viewModel.requestLocationMode(mode) },
                        onBroadcastLocationToMesh = { loc -> viewModel.shareLocationOnMesh(loc) }
                    )
                }

                is Screen.Sos -> {
                    SosScreen(
                        currentLocation = currentLocation,
                        countryCode = countryCode,
                        familyPhone = familyPhone,
                        medicalPhone = medicalPhone,
                        language = language,
                        onBroadcastSos = { note, attachGps -> viewModel.broadcastSos(note, attachGps) }
                    )
                }

                is Screen.Files -> {
                    FileTransferScreen(
                        transfers = transfers,
                        isBangla = isBangla,
                        onPickAndSendFile = { viewModel.createSampleTransfer() },
                        onResumeTransfer = { t -> viewModel.resumeTransfer(t) },
                        onPauseTransfer = { t -> viewModel.pauseTransfer(t) }
                    )
                }

                is Screen.Community -> {
                    CommunityNodesListView(
                        peers = discoveredPeers,
                        language = language
                    )
                }

                is Screen.FirstLaunch -> {
                    FirstLaunchScreen(
                        language = language,
                        onGetStarted = { viewModel.completeFirstLaunch(asGuest = false) },
                        onContinueAsGuest = { viewModel.completeFirstLaunch(asGuest = true) }
                    )
                }

                is Screen.PermissionCenter -> {
                    PermissionCenterScreen(
                        language = language,
                        onBack = { viewModel.navigateTo(Screen.Home) }
                    )
                }

                is Screen.TwoPhoneTest -> {
                    TwoPhoneTestScreen(
                        discoveredPeers = discoveredPeers,
                        isScanning = isScanning,
                        myNodeId = myNodeId,
                        language = language,
                        onScan = { viewModel.startScan() },
                        onSendTestPacket = { targetId, msg -> viewModel.sendRealTestPacket(targetId, msg) },
                        onBack = { viewModel.navigateTo(Screen.Home) }
                    )
                }

                is Screen.FreedomServices -> {
                    FreedomServicesScreen(
                        language = language,
                        myNodeId = myNodeId,
                        onNavigateToChat = { viewModel.navigateTo(Screen.Chat) },
                        onNavigateToFiles = { viewModel.navigateTo(Screen.Files) },
                        onBack = { viewModel.navigateTo(Screen.Home) }
                    )
                }

                is Screen.Gateway -> {
                    GatewayScreen(
                        networkState = networkState,
                        hasGateway = hasGateway,
                        language = language,
                        onBack = { viewModel.navigateTo(Screen.Home) }
                    )
                }
            }
        }
    }

    // Emergency SOS Mesh Broadcast Dialog
    incomingSos?.let { sosPacket ->
        AlertDialog(
            onDismissRequest = { viewModel.dismissSosDialog() },
            icon = {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = "Alert",
                    tint = EmergencyRed,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "🚨 ${Strings.get("sos_title", language).uppercase()}!",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    color = EmergencyRed
                )
            },
            text = {
                Text(
                    text = "${sosPacket.sourceName} (${sosPacket.sourceNodeId}) requested emergency mesh assistance.\nMesh Hop Count: ${sosPacket.hopCount}",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.dismissSosDialog()
                        viewModel.openChatWith(sosPacket.sourceNodeId, sosPacket.sourceName)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed)
                ) {
                    Text("View Alert")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissSosDialog() }) {
                    Text("Dismiss")
                }
            }
        )
    }
}
