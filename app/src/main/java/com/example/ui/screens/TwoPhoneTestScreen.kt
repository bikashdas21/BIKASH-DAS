package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import com.example.transport.DiscoveredPeer
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.MeshGreen
import com.example.ui.theme.RelayGold
import kotlinx.coroutines.launch

enum class PhoneRole {
    PHONE_A, // Sender / Initiator
    PHONE_B  // Receiver / Listener
}

@Composable
fun TwoPhoneTestScreen(
    discoveredPeers: List<DiscoveredPeer>,
    isScanning: Boolean,
    myNodeId: String,
    language: String,
    onScan: () -> Unit,
    onSendTestPacket: suspend (String?, String) -> Pair<Boolean, String>,
    onBack: () -> Unit
) {
    var selectedRole by remember { mutableStateOf(PhoneRole.PHONE_A) }
    var testMessage by remember { mutableStateOf("Zero-Grid Two-Phone Verification Packet") }
    var testResult by remember { mutableStateOf<Pair<Boolean, String>?>(null) }
    var isSending by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("two_phone_test_screen")
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Two-Phone Test Mode",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Device-to-Device Radio Verification",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(onClick = onScan) {
                Icon(Icons.Default.Refresh, contentDescription = "Scan", tint = ElectricCyan)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Role Selector Tabs (Phone A vs Phone B)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                .padding(4.dp)
        ) {
            Button(
                onClick = { selectedRole = PhoneRole.PHONE_A; testResult = null },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedRole == PhoneRole.PHONE_A) ElectricCyan else Color.Transparent,
                    contentColor = if (selectedRole == PhoneRole.PHONE_A) Color.Black else MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Phone A (Sender)", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { selectedRole = PhoneRole.PHONE_B; testResult = null },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedRole == PhoneRole.PHONE_B) RelayGold else Color.Transparent,
                    contentColor = if (selectedRole == PhoneRole.PHONE_B) Color.Black else MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Phone B (Receiver)", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedRole == PhoneRole.PHONE_A) {
            // PHONE A LOGIC:
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Role: Phone A (Initiator)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = ElectricCyan
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Host ID: $myNodeId",
                        style = MaterialTheme.typography.labelMedium.copy(fontFamily = FontFamily.Monospace),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    if (discoveredPeers.isEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 12.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = RelayGold
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Waiting for another Zero-Grid device…",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = RelayGold
                            )
                        }
                    } else {
                        val firstPeer = discoveredPeers.first()
                        Surface(
                            color = MeshGreen.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MeshGreen.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MeshGreen)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Zero-Grid device discovered.",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MeshGreen
                                    )
                                    Text(
                                        text = "${firstPeer.name} (${firstPeer.deviceId})",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Test Packet Input & Action
            OutlinedTextField(
                value = testMessage,
                onValueChange = { testMessage = it },
                label = { Text("Test Message Payload") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                enabled = !isSending
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = {
                    isSending = true
                    scope.launch {
                        val targetId = discoveredPeers.firstOrNull()?.deviceId
                        val res = onSendTestPacket(targetId, testMessage)
                        testResult = res
                        isSending = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_send_real_test_packet"),
                shape = RoundedCornerShape(14.dp),
                enabled = !isSending,
                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan)
            ) {
                if (isSending) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Transmitting over Radio…", color = Color.Black, fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Default.Send, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Phone A → Phone B: Send Real Test Message", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Real Test Result
            testResult?.let { (success, detail) ->
                if (success) {
                    Surface(
                        color = MeshGreen.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(2.dp, MeshGreen),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = MeshGreen, modifier = Modifier.size(32.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "REAL DEVICE TEST PASSED",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                        color = MeshGreen
                                    )
                                    Text(
                                        text = "Actual physical radio transfer confirmed.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = detail,
                                style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    Surface(
                        color = EmergencyRed.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, EmergencyRed),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = EmergencyRed, modifier = Modifier.size(28.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "TEST FAILED",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = EmergencyRed
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = detail,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        } else {
            // PHONE B LOGIC:
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(MeshGreen)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Phone B: Listening for Incoming Radio Packets",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Device ID: $myNodeId",
                        style = MaterialTheme.typography.labelMedium.copy(fontFamily = FontFamily.Monospace),
                        color = RelayGold
                    )
                    Text(
                        text = "Wi-Fi Direct Port: 8988 | BLE Service: Active",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Instructions for Two-Phone Testing:",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "1. Keep this screen open on Phone B.\n2. On Phone A, select 'Phone A (Sender)' mode.\n3. Tap 'Scan' on Phone A to discover Phone B.\n4. Tap 'Send Real Test Message'.\n5. Phone B will automatically verify the packet and return a signed cryptographic acknowledgment.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
