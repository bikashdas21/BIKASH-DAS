package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gateway.GatewayInfo
import com.example.localization.Strings
import com.example.mesh.NetworkConnectionState
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.MeshGreen
import com.example.ui.theme.OfflineGray
import com.example.ui.theme.RelayGold

enum class GatewayNodeStatus {
    AVAILABLE,
    CONNECTED,
    BUSY,
    OFFLINE,
    NOT_AUTHORIZED
}

data class CommunityGatewayItem(
    val id: String,
    val name: String,
    val uplinkType: String, // e.g. "Fiber WAN", "Satellite Backhaul (Starlink)", "Cellular 4G/5G"
    val status: GatewayNodeStatus,
    val hopCount: Int,
    val latencyMs: Long?,
    val bandwidthMbps: Double?,
    val locationDesc: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GatewayScreen(
    networkState: NetworkConnectionState,
    hasGateway: Boolean,
    language: String,
    onBack: () -> Unit
) {
    var isCheckingStatus by remember { mutableStateOf(false) }
    var testResultText by remember { mutableStateOf<String?>(null) }

    val gateways = remember {
        mutableStateListOf(
            CommunityGatewayItem(
                id = "GW-DHAKA-01",
                name = "Dhaka Community Center Gateway",
                uplinkType = "Fiber Gigabit WAN (Dedicated)",
                status = if (hasGateway) GatewayNodeStatus.CONNECTED else GatewayNodeStatus.AVAILABLE,
                hopCount = 2,
                latencyMs = 28,
                bandwidthMbps = 45.0,
                locationDesc = "Dhanmondi Civic Center, Dhaka"
            ),
            CommunityGatewayItem(
                id = "GW-SAT-COAST-03",
                name = "Coastal Emergency Satellite Node",
                uplinkType = "Satellite Backhaul (Low-Earth Orbit)",
                status = GatewayNodeStatus.BUSY,
                hopCount = 3,
                latencyMs = 92,
                bandwidthMbps = 15.0,
                locationDesc = "Cox's Bazar Disaster Hub"
            ),
            CommunityGatewayItem(
                id = "GW-CELL-RURAL-07",
                name = "North District Mobile Backhaul",
                uplinkType = "Cellular 4G/5G WAN Gateway",
                status = GatewayNodeStatus.OFFLINE,
                hopCount = 4,
                latencyMs = null,
                bandwidthMbps = null,
                locationDesc = "Sylhet Foothills Relay"
            ),
            CommunityGatewayItem(
                id = "GW-UNVERIFIED-99",
                name = "Private Rogue Node #99",
                uplinkType = "Unknown / Unverified Uplink",
                status = GatewayNodeStatus.NOT_AUTHORIZED,
                hopCount = 1,
                latencyMs = null,
                bandwidthMbps = null,
                locationDesc = "Unregistered Node"
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (language == "bn") "কমিউনিটি গেটওয়ে" else "Community Gateway",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = if (language == "bn") "বৈধ ইন্টারনেট সংযোগ ব্যবস্থাপনা" else "Authorized Backhaul Discovery",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            isCheckingStatus = true
                            testResultText = null
                        },
                        modifier = Modifier.testTag("btn_refresh_gateway")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = ElectricCyan)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .testTag("gateway_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Three Clearly Differentiated Internet Connectivity States (Section 25)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        when (networkState) {
                            NetworkConnectionState.NETWORK_AVAILABLE -> MeshGreen
                            NetworkConnectionState.OFFLINE_MESH -> RelayGold
                            NetworkConnectionState.NO_CONNECTION -> EmergencyRed
                        }
                    )
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "NETWORK CONNECTIVITY STATUS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = MaterialTheme.colorScheme.outline
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (networkState) {
                                            NetworkConnectionState.NETWORK_AVAILABLE -> MeshGreen
                                            NetworkConnectionState.OFFLINE_MESH -> RelayGold
                                            NetworkConnectionState.NO_CONNECTION -> EmergencyRed
                                        }
                                    )
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = when (networkState) {
                                    NetworkConnectionState.NETWORK_AVAILABLE -> "ZERO-GRID GATEWAY"
                                    NetworkConnectionState.OFFLINE_MESH -> "ZERO-GRID LOCAL"
                                    NetworkConnectionState.NO_CONNECTION -> "NO NETWORK"
                                },
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                                color = when (networkState) {
                                    NetworkConnectionState.NETWORK_AVAILABLE -> MeshGreen
                                    NetworkConnectionState.OFFLINE_MESH -> RelayGold
                                    NetworkConnectionState.NO_CONNECTION -> EmergencyRed
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = when (networkState) {
                                NetworkConnectionState.NETWORK_AVAILABLE ->
                                    if (language == "bn")
                                        "একটি অনুমোদিত গেটওয়ে নোডের মাধ্যমে ইন্টারনেট ব্যাকহলে সংযুক্ত। বৈধ ট্রাফিক আদান-প্রদান করা সম্ভব।"
                                    else
                                        "Connected through an authorized community gateway. Upstream lawful internet routing active."
                                NetworkConnectionState.OFFLINE_MESH ->
                                    if (language == "bn")
                                        "কোন ইন্টারনেট গেটওয়ে নেই। লোকাল মেশ পরিসেবা, মেসেজিং ও ফাইল শেয়ারিং সম্পূর্ণরূপে সক্রিয়।"
                                    else
                                        "No Internet gateway. Local P2P mesh services, messaging, and offline content available."
                                NetworkConnectionState.NO_CONNECTION ->
                                    if (language == "bn")
                                        "কোন ব্যবহারযোগ্য জিরো-গ্রিড সংযোগ পাওয়া যায়নি। কাছাকাছি ডিভাইসের জন্য স্ক্যান করুন।"
                                    else
                                        "No usable Zero-Grid path available. Keep Wi-Fi and Bluetooth active to discover peers."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Technical Truth & Regulatory Compliance Banner (Section 1, 8, 31)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Technical Truth & Compliance Policy",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (language == "bn")
                                    "জিরো-গ্রিড কোনো টেলিকম ফিল্টারিং বাইপাস বা অবৈধ ফ্রি ইন্টারনেট অফার করে না। ইন্টারনেট সেবা পাওয়ার জন্য অনুমোদিত গেটওয়েতে বৈধ ব্রডব্যান্ড বা স্যাটেলাইট সংযোগ প্রয়োজন।"
                                else
                                    "Zero-Grid does NOT bypass carrier billing, ISP restrictions, or authenticate unauthorized data. Gateway access strictly relies on authorized community backhauls.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = if (language == "bn") "আবিষ্কৃত গেটওয়ে নোডসমূহ" else "Discovered Community Gateways",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            items(gateways) { gw ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        when (gw.status) {
                            GatewayNodeStatus.CONNECTED -> MeshGreen
                            GatewayNodeStatus.AVAILABLE -> ElectricCyan
                            GatewayNodeStatus.BUSY -> RelayGold
                            GatewayNodeStatus.OFFLINE -> OfflineGray
                            GatewayNodeStatus.NOT_AUTHORIZED -> EmergencyRed
                        }
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = gw.name,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = gw.locationDesc,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Status Tag
                            Surface(
                                color = when (gw.status) {
                                    GatewayNodeStatus.CONNECTED -> MeshGreen.copy(alpha = 0.15f)
                                    GatewayNodeStatus.AVAILABLE -> ElectricCyan.copy(alpha = 0.15f)
                                    GatewayNodeStatus.BUSY -> RelayGold.copy(alpha = 0.15f)
                                    GatewayNodeStatus.OFFLINE -> OfflineGray.copy(alpha = 0.15f)
                                    GatewayNodeStatus.NOT_AUTHORIZED -> EmergencyRed.copy(alpha = 0.15f)
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = gw.status.name.replace('_', ' '),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = when (gw.status) {
                                        GatewayNodeStatus.CONNECTED -> MeshGreen
                                        GatewayNodeStatus.AVAILABLE -> ElectricCyan
                                        GatewayNodeStatus.BUSY -> RelayGold
                                        GatewayNodeStatus.OFFLINE -> OfflineGray
                                        GatewayNodeStatus.NOT_AUTHORIZED -> EmergencyRed
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Backhaul: ${gw.uplinkType}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = ElectricCyan
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Mesh Distance: ${gw.hopCount} hops",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            if (gw.latencyMs != null) {
                                Text(
                                    text = "Ping: ${gw.latencyMs} ms",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MeshGreen
                                )
                            }

                            if (gw.bandwidthMbps != null) {
                                Text(
                                    text = "Speed: ${gw.bandwidthMbps} Mbps",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = RelayGold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
