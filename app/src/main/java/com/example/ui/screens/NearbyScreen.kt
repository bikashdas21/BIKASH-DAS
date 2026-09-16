package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.transport.DiscoveredPeer
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.MeshGreen
import com.example.ui.theme.OfflineGray
import com.example.ui.theme.RelayGold

@Composable
fun NearbyScreen(
    peers: List<DiscoveredPeer>,
    isScanning: Boolean,
    isBangla: Boolean,
    onScan: () -> Unit,
    onConnectToggle: (DiscoveredPeer) -> Unit,
    onOpenChat: (DiscoveredPeer) -> Unit
) {
    var selectedProfilePeer by remember { mutableStateOf<DiscoveredPeer?>(null) }

    val infiniteTransition = rememberInfiniteTransition(label = "scan_spin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .testTag("nearby_screen")
    ) {
        // Top Header with Scan Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (isBangla) "কাছাকাছি ডিভাইসসমূহ" else "Nearby Devices",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (isBangla) "${peers.size} টি দৃশ্যমান নোড আবিষ্কৃত হয়েছে" else "${peers.size} visible nodes discovered",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = onScan,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("btn_scan_nearby")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier
                        .size(18.dp)
                        .then(if (isScanning) Modifier.rotate(rotation) else Modifier)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = if (isScanning) (if (isBangla) "স্ক্যানিং…" else "Scanning…") else (if (isBangla) "স্ক্যান" else "Scan"))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Device List
        if (peers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isBangla) "কাছাকাছি কোনো সক্রিয় ডিভাইস পাওয়া যায়নি।\nWi-Fi বা Bluetooth চালু রেখে স্ক্যান করুন।"
                    else "No nearby active devices found.\nEnable Wi-Fi or Bluetooth and scan.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(peers, key = { it.deviceId }) { peer ->
                    PeerDeviceCard(
                        peer = peer,
                        isBangla = isBangla,
                        onConnectToggle = { onConnectToggle(peer) },
                        onChat = { onOpenChat(peer) },
                        onViewProfile = { selectedProfilePeer = peer }
                    )
                }
            }
        }
    }

    // Peer Profile Modal
    selectedProfilePeer?.let { peer ->
        AlertDialog(
            onDismissRequest = { selectedProfilePeer = null },
            title = {
                Text(
                    text = peer.name,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Device ID: ${peer.deviceId}",
                        style = MaterialTheme.typography.labelMedium.copy(fontFamily = FontFamily.Monospace)
                    )
                    Text(
                        text = if (isBangla) "সংযোগ মাধ্যম: ${peer.transportType}" else "Transport: ${peer.transportType}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = if (isBangla) "সিগন্যাল শক্তি: ${peer.signalDbm} dBm" else "Signal: ${peer.signalDbm} dBm",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = if (isBangla) "আনুমানিক দূরত্ব: ${peer.distanceMeters.toInt()} মিটার" else "Approx Distance: ${peer.distanceMeters.toInt()} m",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = if (peer.isCommunityNode) {
                            if (isBangla) "✅ পাবলিক সৌরচালিত কমিউনিটি নোড" else "✅ Public Solar Community Node"
                        } else {
                            if (isBangla) "📱 পিয়ার হ্যান্ডসেট নোড" else "📱 Peer Handset Node"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = ElectricCyan
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val p = selectedProfilePeer
                    selectedProfilePeer = null
                    p?.let { onOpenChat(it) }
                }) {
                    Text(if (isBangla) "বার্তা পাঠান" else "Chat")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedProfilePeer = null }) {
                    Text(if (isBangla) "বন্ধ করুন" else "Close")
                }
            }
        )
    }
}

@Composable
fun PeerDeviceCard(
    peer: DiscoveredPeer,
    isBangla: Boolean,
    onConnectToggle: () -> Unit,
    onChat: () -> Unit,
    onViewProfile: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("device_card_${peer.deviceId}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (peer.isConnected) MeshGreen.copy(alpha = 0.8f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Name, ID, Connection status badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (peer.isConnected) MeshGreen else OfflineGray)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = peer.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (peer.isConnected) MeshGreen.copy(alpha = 0.15f)
                            else MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (peer.isConnected) (if (isBangla) "সংযুক্ত" else "Connected")
                        else (if (isBangla) "প্রাপ্য" else "Available"),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (peer.isConnected) MeshGreen else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Details: Transport type, signal dBm, distance
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (peer.transportType.contains("Wi-Fi")) Icons.Default.Wifi else Icons.Default.Bluetooth,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = peer.transportType,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "${peer.signalDbm} dBm",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "~${peer.distanceMeters.toInt()}m",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                    color = ElectricCyan
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Buttons: Connect / Disconnect, Chat, Profile
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilledTonalButton(
                    onClick = onConnectToggle,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = if (peer.isConnected) MaterialTheme.colorScheme.errorContainer
                        else MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Icon(
                        imageVector = if (peer.isConnected) Icons.Default.LinkOff else Icons.Default.Link,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (peer.isConnected) (if (isBangla) "বিচ্ছিন্ন" else "Disconnect")
                        else (if (isBangla) "সংযোগ" else "Connect"),
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                Button(
                    onClick = onChat,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Chat,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isBangla) "চ্যাট" else "Chat",
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                OutlinedButton(
                    onClick = onViewProfile,
                    modifier = Modifier.width(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Profile",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
