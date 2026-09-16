package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.community.GlobalCommunityNode
import com.example.community.GlobalCommunityNodeRegistry
import com.example.localization.Strings
import com.example.map.GlobalMapView
import com.example.transport.DiscoveredPeer
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.MeshGreen
import com.example.ui.theme.OfflineGray
import com.example.ui.theme.RelayGold

@Composable
fun NetworkTopologyScreen(
    peers: List<DiscoveredPeer>,
    myNodeId: String,
    hasGateway: Boolean,
    language: String,
    globalNodes: List<GlobalCommunityNode> = GlobalCommunityNodeRegistry.SAMPLE_GLOBAL_NODES,
    onSelectGlobalNode: ((String) -> Unit)? = null
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("network_screen")
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Text(
                        text = "Hop Sequence",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Text(
                        text = "Global Map",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = {
                    Text(
                        text = "Community Nodes",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            )
        }

        when (selectedTab) {
            0 -> TopologyMapView(
                peers = peers,
                myNodeId = myNodeId,
                hasGateway = hasGateway,
                language = language
            )
            1 -> Box(modifier = Modifier.fillMaxSize()) {
                GlobalMapView(
                    nodes = globalNodes,
                    selectedNodeId = null,
                    onNodeSelected = { node -> onSelectGlobalNode?.invoke(node.nodeId) },
                    modifier = Modifier.fillMaxSize()
                )
            }
            2 -> CommunityNodesListView(
                peers = peers,
                language = language
            )
        }
    }
}

@Composable
fun TopologyMapView(
    peers: List<DiscoveredPeer>,
    myNodeId: String,
    hasGateway: Boolean,
    language: String
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(20.dp)
    ) {
        // Gateway & Route Pipeline Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (hasGateway) Color(0xFF07231A) else Color(0xFF142033)
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (hasGateway) MeshGreen else ElectricCyan.copy(alpha = 0.5f)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (hasGateway) Strings.get("network_layer_gateway", language)
                        else Strings.get("network_layer_offline", language),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (hasGateway) MeshGreen else RelayGold
                    )
                    Icon(
                        imageVector = if (hasGateway) Icons.Default.Language else Icons.Default.Router,
                        contentDescription = null,
                        tint = if (hasGateway) MeshGreen else RelayGold
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (hasGateway) "Traffic relays to internet backhaul through an authorized gateway node."
                    else "Pure local device-to-device decentralized radio communications.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Discovered Mesh Hop Sequence",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Visual Hop Sequence Pipeline
        // 1. My Phone
        TopologyNodeRow(
            title = "My Device (Host Node)",
            subtitle = myNodeId,
            statusColor = MeshGreen,
            statusLabel = "Host",
            icon = Icons.Default.Router,
            isLast = false
        )

        // 2. Intermediate Relay Nodes
        val relays = peers.take(3)
        relays.forEachIndexed { idx, peer ->
            val isCommunity = peer.isCommunityNode
            TopologyNodeRow(
                title = peer.name,
                subtitle = "${peer.deviceId} • ${peer.transportType} • ~${peer.distanceMeters.toInt()}m",
                statusColor = if (peer.isConnected) (if (isCommunity) RelayGold else MeshGreen) else OfflineGray,
                statusLabel = if (isCommunity) "Community" else (if (peer.isConnected) "Relay (Hop ${idx + 1})" else "Offline"),
                icon = if (isCommunity) Icons.Default.SolarPower else Icons.Default.CellTower,
                isLast = false
            )
        }

        // 3. Community Node / Gateway
        val communityNode = peers.firstOrNull { it.isCommunityNode } ?: peers.lastOrNull()
        if (communityNode != null) {
            TopologyNodeRow(
                title = communityNode.name,
                subtitle = "Solar-Powered Node • ${communityNode.deviceId}",
                statusColor = RelayGold,
                statusLabel = if (hasGateway) "Gateway" else "Relay",
                icon = Icons.Default.SolarPower,
                isLast = false
            )
        }

        // 4. Destination Node
        val destination = peers.lastOrNull()
        TopologyNodeRow(
            title = destination?.name ?: "Recipient Peer",
            subtitle = destination?.deviceId ?: "ZG-PEER-REMOTE",
            statusColor = if (destination?.isConnected == true) MeshGreen else OfflineGray,
            statusLabel = if (destination?.isConnected == true) "Delivered" else "Offline",
            icon = Icons.Default.Router,
            isLast = true
        )
    }
}

@Composable
fun TopologyNodeRow(
    title: String,
    subtitle: String,
    statusColor: Color,
    statusLabel: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isLast: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(36.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(statusColor.copy(alpha = 0.2f))
                    .border(2.dp, statusColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(statusColor)
                )
            }

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(52.dp)
                        .background(statusColor.copy(alpha = 0.5f))
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = if (isLast) 0.dp else 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(statusColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = statusLabel,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = statusColor
                    )
                }
            }
        }
    }
}

@Composable
fun CommunityNodesListView(
    peers: List<DiscoveredPeer>,
    language: String
) {
    val communityPeers = peers.filter { it.isCommunityNode }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SolarPower, contentDescription = null, tint = RelayGold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Community Node Architecture",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "• Power: 20W Solar Panel + 12Ah LiFePO4 Battery\n• Network: 802.11 b/g/n Wi-Fi Direct Mesh Repeater\n• Low Bandwidth: LoRa 433/868/915 MHz (Text & SOS only)\n• Processor: Low-power ESP32 / ARM Embedded Linux",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        items(communityPeers) { node ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, RelayGold.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = node.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(RelayGold.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = if (node.isGateway) "Gateway" else "Repeater",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = RelayGold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Node ID: ${node.deviceId}",
                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.BatteryChargingFull, contentDescription = null, tint = MeshGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("94% Solar", style = MaterialTheme.typography.labelMedium)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Wifi, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(node.transportType, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }
    }
}
