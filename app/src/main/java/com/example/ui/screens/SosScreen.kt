package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.CountryRegistry
import com.example.localization.Strings
import com.example.location.OfflineLocation
import com.example.location.OfflineLocationManager
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.MeshGreen
import com.example.ui.theme.RelayGold

@Composable
fun SosScreen(
    currentLocation: OfflineLocation?,
    countryCode: String,
    familyPhone: String,
    medicalPhone: String,
    language: String,
    onBroadcastSos: (emergencyNote: String, attachLocation: Boolean) -> Unit
) {
    val context = LocalContext.current
    var showConfirmDialog by remember { mutableStateOf(false) }
    var attachGps by remember { mutableStateOf(true) }
    var emergencyDetails by remember { mutableStateOf("EMERGENCY: Urgent medical/security assistance required!") }
    val scrollState = rememberScrollState()

    val countryInfo = remember(countryCode) { CountryRegistry.getCountryByCode(countryCode) }
    var phoneToCallPrompt by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(20.dp)
            .testTag("sos_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = Strings.get("sos_title", language),
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
            color = EmergencyRed
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Broadcasts with top priority across all reachable mesh nodes.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Large Glowing Red SOS Button
        Box(
            modifier = Modifier
                .size(190.dp)
                .clip(CircleShape)
                .background(EmergencyRed.copy(alpha = 0.15f))
                .border(2.dp, EmergencyRed.copy(alpha = 0.4f), CircleShape)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = { showConfirmDialog = true },
                modifier = Modifier
                    .size(150.dp)
                    .testTag("btn_trigger_sos"),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "SOS",
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.onError
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "SOS",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        ),
                        color = MaterialTheme.colorScheme.onError
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // SOS Delivery Progression Timeline
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "📡 Real Delivery Status Pipeline",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatusStep(step = "1. Created", active = true, color = MeshGreen)
                    StatusStep(step = "2. Sending", active = true, color = MeshGreen)
                    StatusStep(step = "3. Relaying", active = true, color = RelayGold)
                    StatusStep(step = "4. Delivered", active = false, color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "No fake nationwide coverage: packets hop node-to-node within actual physical radio range.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Country Emergency Dial Card (Requires user confirmation)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = androidx.compose.foundation.BorderStroke(1.dp, EmergencyRed.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${countryInfo.flagEmoji} ${countryInfo.name} Emergency",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Official National Number: ${countryInfo.defaultEmergencyNumber}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = { phoneToCallPrompt = countryInfo.defaultEmergencyNumber },
                        colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(countryInfo.defaultEmergencyNumber)
                    }
                }

                if (familyPhone.isNotBlank() || medicalPhone.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Configured Trusted Contacts:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    if (familyPhone.isNotBlank()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { phoneToCallPrompt = familyPhone }.padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Family Contact", style = MaterialTheme.typography.bodySmall)
                            Text(familyPhone, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
                        }
                    }

                    if (medicalPhone.isNotBlank()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { phoneToCallPrompt = medicalPhone }.padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Medical Contact", style = MaterialTheme.typography.bodySmall)
                            Text(medicalPhone, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Options Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Emergency Message Details:",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = emergencyDetails,
                    onValueChange = { emergencyDetails = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = attachGps,
                        onCheckedChange = { attachGps = it },
                        colors = CheckboxDefaults.colors(checkedColor = EmergencyRed)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "Attach Offline GPS coordinates",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        currentLocation?.let { loc ->
                            Text(
                                text = OfflineLocationManager.formatCoordinates(loc.latitude, loc.longitude),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Warning against false alarms
        Text(
            text = "⚠️ False alerts are strictly prohibited. Use only in life or safety emergencies.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }

    // Accidental click confirmation dialog
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = EmergencyRed,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = Strings.get("sos_prompt_confirm", language),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Text(
                    text = "This will transmit high-priority alert packets to all surrounding Zero-Grid nodes.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDialog = false
                        onBroadcastSos(emergencyDetails, attachGps)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
                    modifier = Modifier.testTag("btn_confirm_sos")
                ) {
                    Text("Yes, Send SOS")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Emergency Call Explicit Confirmation Dialog
    phoneToCallPrompt?.let { numberToCall ->
        AlertDialog(
            onDismissRequest = { phoneToCallPrompt = null },
            icon = { Icon(Icons.Default.Phone, contentDescription = null, tint = EmergencyRed) },
            title = { Text("Confirm Emergency Dial") },
            text = {
                Text("Confirm user action: Do you want to open the system dialer to call $numberToCall?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        phoneToCallPrompt = null
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$numberToCall"))
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed)
                ) {
                    Text("Call $numberToCall")
                }
            },
            dismissButton = {
                TextButton(onClick = { phoneToCallPrompt = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun StatusStep(step: String, active: Boolean, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(if (active) color else Color.Gray)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = step,
            style = MaterialTheme.typography.labelSmall,
            color = if (active) color else Color.Gray,
            fontSize = 10.sp
        )
    }
}
