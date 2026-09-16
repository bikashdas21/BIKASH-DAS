package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.SolarPower
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
import com.example.localization.CountryRegistry
import com.example.localization.DateTimeFormatterHelper
import com.example.localization.Strings
import com.example.map.GlobalMapView
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.MeshGreen
import com.example.ui.theme.RelayGold

@Composable
fun MapScreen(
    nodes: List<GlobalCommunityNode>,
    selectedNodeId: String?,
    language: String,
    timeZone: String,
    is24Hour: Boolean,
    onSelectNode: (String?) -> Unit,
    onOpenChat: (String, String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Visual Vector Map, 1 = Node List
    var selectedFilterCountry by remember { mutableStateOf("ALL") }

    val filteredNodes = remember(nodes, selectedFilterCountry) {
        if (selectedFilterCountry == "ALL") nodes
        else nodes.filter { it.countryCode.equals(selectedFilterCountry, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("map_screen")
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = Strings.get("tab_map", language),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Worldwide Geographic Mesh & Nodes",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Tab Toggle (Map vs List)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(4.dp)
            ) {
                TabButton(
                    text = "Map",
                    isSelected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                TabButton(
                    text = "Nodes (${filteredNodes.size})",
                    isSelected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
            }
        }

        // Country Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.FilterList, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(16.dp))
            listOf("ALL" to "🌍 All", "US" to "🇺🇸 US", "IN" to "🇮🇳 IN", "BD" to "🇧🇩 BD", "DE" to "🇩🇪 EU", "BR" to "🇧🇷 BR", "JP" to "🇯🇵 JP").forEach { (code, label) ->
                FilterChip(
                    selected = selectedFilterCountry == code,
                    onClick = { selectedFilterCountry = code },
                    label = { Text(label, fontSize = 11.sp) }
                )
            }
        }

        if (selectedTab == 0) {
            // Visual Vector Map View
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                GlobalMapView(
                    nodes = filteredNodes,
                    selectedNodeId = selectedNodeId,
                    onNodeSelected = { node -> onSelectNode(node.nodeId) },
                    modifier = Modifier.fillMaxSize()
                )

                // Selected Node Inspector Card (Floating)
                val selectedNode = filteredNodes.find { it.nodeId == selectedNodeId }
                if (selectedNode != null) {
                    Card(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xF00F172A)),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyan)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(selectedNode.type.icon, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            selectedNode.name,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = Color.White
                                        )
                                        Text(
                                            "${selectedNode.regionOrState} • ${selectedNode.type.label}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color(0xFF94A3B8)
                                        )
                                    }
                                }

                                Button(
                                    onClick = { onOpenChat(selectedNode.nodeId, selectedNode.name) },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Chat")
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Status: ${selectedNode.status}",
                                    color = if (selectedNode.status == "ONLINE") MeshGreen else RelayGold,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Battery: ${selectedNode.batteryLevel ?: "--"}%",
                                    color = Color(0xFFE2E8F0),
                                    fontSize = 12.sp
                                )
                                Text(
                                    "Transports: ${selectedNode.availableTransports.joinToString(", ")}",
                                    color = ElectricCyan,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Detailed List of Global Nodes
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    // Truth note
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = RelayGold, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(10.dp))
                            Text(
                                Strings.get("solar_recommended_note", language),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                items(filteredNodes) { node ->
                    GlobalNodeCard(
                        node = node,
                        timeZone = timeZone,
                        is24Hour = is24Hour,
                        onChat = { onOpenChat(node.nodeId, node.name) }
                    )
                }
            }
        }
    }
}

@Composable
fun TabButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun GlobalNodeCard(
    node: GlobalCommunityNode,
    timeZone: String,
    is24Hour: Boolean,
    onChat: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(node.type.icon, fontSize = 24.sp)
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            text = node.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${node.countryCode} • ${node.regionOrState} • ${node.type.label}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (node.isGateway) {
                    Surface(
                        color = ElectricCyan.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "GATEWAY",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = ElectricCyan
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            // Telemetry badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Battery", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            "${node.batteryLevel ?: "--"}% ${if (node.isSolarPowered) "☀️" else ""}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = if ((node.batteryLevel ?: 0) > 40) MeshGreen else RelayGold
                        )
                    }
                }

                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Last Seen", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            DateTimeFormatterHelper.formatTime(node.lastSeenTime, timeZone, is24Hour),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            if (node.solarTelemetry != null) {
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Solar In: ${node.solarTelemetry.solarInputWatts}W • ${node.solarTelemetry.batteryVoltage}V • ${node.solarTelemetry.batteryTempC}°C",
                        style = MaterialTheme.typography.labelSmall,
                        color = RelayGold
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Transports: ${node.availableTransports.joinToString(", ")}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )

                Button(
                    onClick = onChat,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text("Connect & Chat", fontSize = 12.sp)
                }
            }
        }
    }
}
