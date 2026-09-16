package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.CountryRegistry
import com.example.localization.NumberFormatterHelper
import com.example.localization.Strings
import com.example.mesh.NetworkConnectionState
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.MeshGreen
import com.example.ui.theme.OfflineGray
import com.example.ui.theme.RelayGold

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    networkState: NetworkConnectionState,
    connectedCount: Int,
    nearestDistanceMeters: Int,
    connectionTransport: String,
    myNodeId: String,
    countryCode: String,
    language: String,
    isMetric: Boolean,
    isRelayMode: Boolean,
    isZeroGridActive: Boolean = true,
    onToggleZeroGrid: (Boolean) -> Unit = {},
    onNavigateToChat: () -> Unit,
    onNavigateToNearby: () -> Unit,
    onNavigateToNetwork: () -> Unit = {},
    onNavigateToFreedomServices: () -> Unit = {},
    onNavigateToLocation: () -> Unit,
    onNavigateToSos: () -> Unit,
    onNavigateToFiles: () -> Unit,
    onNavigateToCommunity: () -> Unit,
    onNavigateToGateway: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToMap: () -> Unit,
    onNavigateToDiagnostics: () -> Unit,
    onNavigateToTwoPhoneTest: () -> Unit = {},
    onNavigateToPermissionCenter: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val countryInfo = remember(countryCode) { CountryRegistry.getCountryByCode(countryCode) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .testTag("home_screen_content")
    ) {
        // App Title & Global Country Badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "ZERO-GRID",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = Strings.get("app_subtitle", language),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Country & Node Badge
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(countryInfo.flagEmoji, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                if (!isZeroGridActive) OfflineGray
                                else when (networkState) {
                                    NetworkConnectionState.NETWORK_AVAILABLE -> MeshGreen
                                    NetworkConnectionState.OFFLINE_MESH -> RelayGold
                                    NetworkConnectionState.NO_CONNECTION -> EmergencyRed
                                }
                            )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = myNodeId,
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Top Status: ZERO-GRID: ON/OFF - Show actual state (Section 17)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("card_zero_grid_toggle"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isZeroGridActive) MeshGreen.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isZeroGridActive) MeshGreen.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(if (isZeroGridActive) MeshGreen else EmergencyRed)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "ZERO-GRID: ${if (isZeroGridActive) "ON" else "OFF"}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                            color = if (isZeroGridActive) MeshGreen else EmergencyRed
                        )
                        Text(
                            text = if (isZeroGridActive)
                                if (language == "bn") "মেশ রেডিও সক্রিয় ও প্যাকেট রিলে প্রস্তুত" else "Mesh Radios Active & Ready to Relay"
                            else
                                if (language == "bn") "নেটওয়ার্কিং নিষ্ক্রিয় (রেডিও বন্ধ)" else "Mesh Radios Deactivated",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Switch(
                    checked = isZeroGridActive,
                    onCheckedChange = onToggleZeroGrid,
                    modifier = Modifier.testTag("switch_zero_grid_power"),
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MeshGreen,
                        checkedTrackColor = MeshGreen.copy(alpha = 0.3f)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Hero Network Status Card
        HeroNetworkCard(
            networkState = networkState,
            connectedCount = connectedCount,
            nearestDistanceMeters = nearestDistanceMeters,
            connectionTransport = connectionTransport,
            isRelayMode = isRelayMode,
            isMetric = isMetric,
            isZeroGridActive = isZeroGridActive,
            language = language
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Technical Truth & Honest Security Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Public,
                    contentDescription = null,
                    tint = ElectricCyan,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Decentralized Worldwide Architecture",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = Strings.get("global_truth_banner", language),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Quick Actions & Network Modules",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Action Tiles Grid
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            maxItemsInEachRow = 2,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ActionTile(
                modifier = Modifier.weight(1f),
                title = Strings.get("tab_chat", language),
                subtitle = "Offline P2P messaging",
                icon = Icons.AutoMirrored.Filled.Chat,
                accentColor = ElectricCyan,
                testTag = "btn_quick_chat",
                onClick = onNavigateToChat
            )

            ActionTile(
                modifier = Modifier.weight(1f),
                title = Strings.get("tab_nearby", language),
                subtitle = "Wi-Fi & BLE discovery",
                icon = Icons.Default.People,
                accentColor = MeshGreen,
                testTag = "btn_quick_nearby",
                onClick = onNavigateToNearby
            )

            ActionTile(
                modifier = Modifier.weight(1f),
                title = "Freedom Services",
                subtitle = "Offline bulletins & guides",
                icon = Icons.Default.Hub,
                accentColor = Color(0xFF00E5FF),
                testTag = "btn_quick_freedom_services",
                onClick = onNavigateToFreedomServices
            )

            ActionTile(
                modifier = Modifier.weight(1f),
                title = "Gateway",
                subtitle = "Community backhaul",
                icon = Icons.Default.Dns,
                accentColor = Color(0xFF4CAF50),
                testTag = "btn_quick_gateway",
                onClick = onNavigateToGateway
            )

            ActionTile(
                modifier = Modifier.weight(1f),
                title = "Topology",
                subtitle = "Mesh routing graph",
                icon = Icons.Default.WifiTethering,
                accentColor = ElectricCyan,
                testTag = "btn_quick_network",
                onClick = onNavigateToNetwork
            )

            ActionTile(
                modifier = Modifier.weight(1f),
                title = Strings.get("tab_map", language),
                subtitle = "Vector world mesh",
                icon = Icons.Default.Map,
                accentColor = Color(0xFF38BDF8),
                testTag = "btn_quick_map",
                onClick = onNavigateToMap
            )

            ActionTile(
                modifier = Modifier.weight(1f),
                title = "Location",
                subtitle = "Offline GNSS share",
                icon = Icons.Default.MyLocation,
                accentColor = Color(0xFF64B5F6),
                testTag = "btn_quick_location",
                onClick = onNavigateToLocation
            )

            ActionTile(
                modifier = Modifier.weight(1f),
                title = Strings.get("tab_sos", language),
                subtitle = "Priority alert flood",
                icon = Icons.Default.Warning,
                accentColor = EmergencyRed,
                testTag = "btn_quick_sos",
                onClick = onNavigateToSos
            )

            ActionTile(
                modifier = Modifier.weight(1f),
                title = Strings.get("tab_files", language),
                subtitle = "Chunked file transfer",
                icon = Icons.Default.FolderOpen,
                accentColor = Color(0xFFCE93D8),
                testTag = "btn_quick_files",
                onClick = onNavigateToFiles
            )

            ActionTile(
                modifier = Modifier.weight(1f),
                title = "Community",
                subtitle = "Solar repeaters",
                icon = Icons.Default.SolarPower,
                accentColor = RelayGold,
                testTag = "btn_quick_community",
                onClick = onNavigateToCommunity
            )

            ActionTile(
                modifier = Modifier.weight(1f),
                title = Strings.get("tab_diagnostics", language),
                subtitle = "13-point matrix",
                icon = Icons.Default.CheckCircle,
                accentColor = Color(0xFF10B981),
                testTag = "btn_quick_diagnostics",
                onClick = onNavigateToDiagnostics
            )

            ActionTile(
                modifier = Modifier.weight(1f),
                title = "Two-Phone Test",
                subtitle = "Real radio verification",
                icon = Icons.Default.Sensors,
                accentColor = ElectricCyan,
                testTag = "btn_quick_two_phone_test",
                onClick = onNavigateToTwoPhoneTest
            )

            ActionTile(
                modifier = Modifier.weight(1f),
                title = "Permissions",
                subtitle = "Hardware access status",
                icon = Icons.Default.Security,
                accentColor = RelayGold,
                testTag = "btn_quick_permissions",
                onClick = onNavigateToPermissionCenter
            )

            ActionTile(
                modifier = Modifier.weight(1f),
                title = Strings.get("tab_settings", language),
                subtitle = "Preferences & radios",
                icon = Icons.Default.Settings,
                accentColor = MaterialTheme.colorScheme.primary,
                testTag = "btn_quick_settings",
                onClick = onNavigateToSettings
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun HeroNetworkCard(
    networkState: NetworkConnectionState,
    connectedCount: Int,
    nearestDistanceMeters: Int,
    connectionTransport: String,
    isRelayMode: Boolean,
    isMetric: Boolean,
    isZeroGridActive: Boolean = true,
    language: String
) {
    val gradientBrush = if (!isZeroGridActive) {
        Brush.linearGradient(listOf(Color(0xFF1E1E1E), Color(0xFF121212)))
    } else {
        when (networkState) {
            NetworkConnectionState.NETWORK_AVAILABLE -> Brush.linearGradient(
                listOf(Color(0xFF00382B), Color(0xFF07231A))
            )
            NetworkConnectionState.OFFLINE_MESH -> Brush.linearGradient(
                listOf(Color(0xFF14243B), Color(0xFF0A1526))
            )
            NetworkConnectionState.NO_CONNECTION -> Brush.linearGradient(
                listOf(Color(0xFF330C16), Color(0xFF19060B))
            )
        }
    }

    val statusColor = if (!isZeroGridActive) {
        OfflineGray
    } else {
        when (networkState) {
            NetworkConnectionState.NETWORK_AVAILABLE -> MeshGreen
            NetworkConnectionState.OFFLINE_MESH -> RelayGold
            NetworkConnectionState.NO_CONNECTION -> EmergencyRed
        }
    }

    val statusTitle = if (!isZeroGridActive) {
        "ZERO-GRID: DEACTIVATED"
    } else {
        when (networkState) {
            NetworkConnectionState.NETWORK_AVAILABLE -> "ZERO-GRID GATEWAY"
            NetworkConnectionState.OFFLINE_MESH -> "ZERO-GRID LOCAL"
            NetworkConnectionState.NO_CONNECTION -> "NO NETWORK"
        }
    }

    val statusDesc = if (!isZeroGridActive) {
        if (language == "bn") "মেশ রেডিও বন্ধ। সক্রিয় করতে উপরের সুইচে চাপুন।" else "Mesh radios off. Toggle switch above to activate."
    } else {
        when (networkState) {
            NetworkConnectionState.NETWORK_AVAILABLE ->
                if (language == "bn") "অনুমোদিত গেটওয়ের মাধ্যমে সংযুক্ত" else "Connected through authorized gateway"
            NetworkConnectionState.OFFLINE_MESH ->
                if (language == "bn") "লোকাল অফলাইন মেশ সক্রিয় • গেটওয়ে নেই" else "Local mesh active • No Internet gateway"
            NetworkConnectionState.NO_CONNECTION ->
                if (language == "bn") "কোন ব্যবহারযোগ্য জিরো-গ্রিড সংযোগ নেই" else "No usable Zero-Grid path"
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("hero_network_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, statusColor.copy(alpha = 0.5f))
    ) {
        Box(
            modifier = Modifier
                .background(gradientBrush)
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(statusColor)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = statusTitle,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = statusColor,
                                maxLines = 1
                            )
                            Text(
                                text = statusDesc,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (isRelayMode && isZeroGridActive) {
                        Surface(
                            color = statusColor.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "RELAY ON",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = statusColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MetricItem(
                        label = "Peers",
                        value = if (isZeroGridActive) "$connectedCount Nodes" else "Offline"
                    )
                    MetricItem(
                        label = "Nearest",
                        value = if (isZeroGridActive) NumberFormatterHelper.formatDistance(nearestDistanceMeters.toDouble(), isMetric) else "--"
                    )
                    MetricItem(
                        label = "Transport",
                        value = if (isZeroGridActive) connectionTransport else "None"
                    )
                }
            }
        }
    }
}

@Composable
fun MetricItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun ActionTile(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}

