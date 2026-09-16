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
import com.example.localization.CountryInfo
import com.example.localization.CountryRegistry
import com.example.localization.DateTimeFormatterHelper
import com.example.localization.LanguageRegistry
import com.example.localization.Strings
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.MeshGreen
import com.example.ui.theme.RelayGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    myNodeId: String,
    displayName: String,
    fingerprint: String,
    countryCode: String,
    timeZone: String,
    language: String,
    is24Hour: Boolean,
    isMetric: Boolean,
    isLowBandwidth: Boolean,
    isRelayMode: Boolean,
    isBatterySaver: Boolean,
    isDarkMode: Boolean,
    familyPhone: String,
    medicalPhone: String,
    onUpdateDisplayName: (String) -> Unit,
    onSetCountry: (String) -> Unit,
    onSetTimeZone: (String) -> Unit,
    onSetLanguage: (String) -> Unit,
    onSet24Hour: (Boolean) -> Unit,
    onSetMetric: (Boolean) -> Unit,
    onSetLowBandwidth: (Boolean) -> Unit,
    onToggleRelayMode: (Boolean) -> Unit,
    onToggleBatterySaver: (Boolean) -> Unit,
    onToggleDarkMode: (Boolean) -> Unit,
    onSetFamilyPhone: (String) -> Unit,
    onSetMedicalPhone: (String) -> Unit,
    onClearAllData: () -> Unit,
    onNavigateToPermissionCenter: () -> Unit = {},
    onNavigateToTwoPhoneTest: () -> Unit = {},
    onResetFirstLaunch: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    var showEditNameDialog by remember { mutableStateOf(false) }
    var tempDisplayName by remember { mutableStateOf(displayName) }
    var showCountryDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showTimeZoneDialog by remember { mutableStateOf(false) }
    var showContactsDialog by remember { mutableStateOf(false) }
    var showWipeConfirmDialog by remember { mutableStateOf(false) }
    var showKeyDialog by remember { mutableStateOf(false) }

    val currentCountry = remember(countryCode) { CountryRegistry.getCountryByCode(countryCode) }
    val currentLang = remember(language) { LanguageRegistry.getLanguage(language) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(20.dp)
            .testTag("settings_screen"),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = Strings.get("tab_settings", language),
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )

        // 1. Account / Device Identity (Guest mode, no SIM required)
        SettingsSection(title = "Device Identity & Guest Mode") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showEditNameDialog = true }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(ElectricCyan.copy(alpha = 0.15f))
                        .border(1.5.dp, ElectricCyan, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = ElectricCyan)
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = displayName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Node ID: $myNodeId • ${Strings.get("guest_mode_badge", language)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // 2. Country, Region & Wireless Regulation Compliance
        SettingsSection(title = Strings.get("country_region_label", language)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showCountryDialog = true },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(currentCountry.flagEmoji, fontSize = 24.sp)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "${currentCountry.name} (${currentCountry.nativeName})",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Dial Code: ${currentCountry.callingCode} • Emergency: ${currentCountry.defaultEmergencyNumber}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    TextButton(onClick = { showCountryDialog = true }) {
                        Text("Change")
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                // Regulatory spectrum disclosure
                Row(verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.CellTower, contentDescription = null, tint = RelayGold, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Regional Radio Spectrum: ${currentCountry.loraFrequencyBand}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = RelayGold
                        )
                        Text(
                            text = "Zero-Grid adheres to local unlicensed ISM bands. No claim of universal government approval without local type-certification.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // 3. Global Language (12 Languages & RTL)
        SettingsSection(title = Strings.get("language_label", language)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showLanguageDialog = true }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Language, contentDescription = null, tint = ElectricCyan)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "${currentLang.flag} ${currentLang.nativeName} (${currentLang.englishName})",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        if (currentLang.isRtl) {
                            Text("Right-to-Left (RTL) Layout Active", style = MaterialTheme.typography.labelSmall, color = ElectricCyan)
                        }
                    }
                }
                TextButton(onClick = { showLanguageDialog = true }) {
                    Text("Select")
                }
            }
        }

        // 4. Time Zone, Clock & Units
        SettingsSection(title = "Time Zone & Units Formatting") {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showTimeZoneDialog = true },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("IANA Time Zone", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                        Text(timeZone, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    TextButton(onClick = { showTimeZoneDialog = true }) {
                        Text("Change")
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                // 24-Hour Clock Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("24-Hour Clock", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                        Text(if (is24Hour) "14:30:00" else "02:30:00 PM", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(checked = is24Hour, onCheckedChange = onSet24Hour)
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                // Metric vs Imperial Units
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Measurement Units", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                        Text(
                            if (isMetric) Strings.get("unit_metric", language) else Strings.get("unit_imperial", language),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(checked = isMetric, onCheckedChange = onSetMetric)
                }
            }
        }

        // 5. Low-Bandwidth Mode (Disaster & Remote)
        SettingsSection(title = Strings.get("low_bandwidth_title", language)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Prioritize Critical SOS & Text", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                        Text(
                            Strings.get("low_bandwidth_desc", language),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = isLowBandwidth,
                        onCheckedChange = onSetLowBandwidth,
                        colors = SwitchDefaults.colors(checkedThumbColor = RelayGold, checkedTrackColor = RelayGold.copy(alpha = 0.4f))
                    )
                }
            }
        }

        // 6. Trusted Emergency Contacts
        SettingsSection(title = Strings.get("emergency_contacts_title", language)) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Family Phone: ${familyPhone.ifBlank { "Not set" }}", style = MaterialTheme.typography.bodySmall)
                        Text("Medical Phone: ${medicalPhone.ifBlank { "Not set" }}", style = MaterialTheme.typography.bodySmall)
                    }
                    Button(onClick = { showContactsDialog = true }, shape = RoundedCornerShape(8.dp)) {
                        Text("Configure")
                    }
                }
                Text(
                    Strings.get("emergency_confirmation_req", language),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 7. Cryptographic Identity & Honest Security
        SettingsSection(title = "Cryptographic Identity & Security") {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showKeyDialog = true },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Fingerprint, contentDescription = null, tint = MeshGreen)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Hardware Key Fingerprint", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                        Text(
                            text = fingerprint,
                            style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace, color = ElectricCyan)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = RelayGold, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = Strings.get("security_disclaimer", language),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // 8. Mesh & Relay Service
        SettingsSection(title = "Relay & Battery Settings") {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Packet Relay Node", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                        Text("Forward encrypted multi-hop packets for neighboring peers", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(checked = isRelayMode, onCheckedChange = onToggleRelayMode)
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Discovery Power Saver", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                        Text("Throttle radio scan intervals", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(checked = isBatterySaver, onCheckedChange = onToggleBatterySaver)
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Dark Cyber Theme", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                    Switch(checked = isDarkMode, onCheckedChange = onToggleDarkMode)
                }
            }
        }

        // 9. Data Management & Wipe
        SettingsSection(title = "Local Data Minimization") {
            Column(modifier = Modifier.padding(16.dp)) {
                Button(
                    onClick = { showWipeConfirmDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Erase All Local Data")
                }
            }
        }

        // 10. Hardware, Permissions & Two-Phone Test Mode
        SettingsSection(title = "Developer Tools & Verification") {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = onNavigateToPermissionCenter,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = ElectricCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Permission Center (Real System Status)")
                }

                OutlinedButton(
                    onClick = onNavigateToTwoPhoneTest,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Sensors, contentDescription = null, tint = RelayGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Two-Phone Test Mode (Real Device Verification)")
                }

                OutlinedButton(
                    onClick = onResetFirstLaunch,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.RestartAlt, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Show First-Launch Welcome Screen")
                }

                Spacer(modifier = Modifier.height(4.dp))

                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Zero-Grid Freedom Network",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Package: com.zerogrid.freedomnetwork\nVersion: 1.0.0 (Version Code 1)\nOffline Engine: Real Wi-Fi Direct & BLE Sockets",
                            style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }

    // Country Selection Dialog
    if (showCountryDialog) {
        AlertDialog(
            onDismissRequest = { showCountryDialog = false },
            title = { Text(Strings.get("country_region_label", language)) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 380.dp).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CountryRegistry.COUNTRIES.forEach { c ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSetCountry(c.code)
                                    onSetTimeZone(c.defaultTimeZone)
                                    showCountryDialog = false
                                },
                            color = if (c.code == countryCode) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(c.flagEmoji, fontSize = 22.sp)
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text("${c.name} (${c.nativeName})", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("Calling: ${c.callingCode} • LoRa: ${c.loraFrequencyBand}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCountryDialog = false }) { Text("Close") }
            }
        )
    }

    // Language Selection Dialog
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text(Strings.get("language_label", language)) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 380.dp).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LanguageRegistry.LANGUAGES.forEach { langItem ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSetLanguage(langItem.code)
                                    showLanguageDialog = false
                                },
                            color = if (langItem.code == language) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(langItem.flag, fontSize = 22.sp)
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(langItem.nativeName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(langItem.englishName + if (langItem.isRtl) " (RTL)" else "", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) { Text("Close") }
            }
        )
    }

    // Time Zone Selection Dialog
    if (showTimeZoneDialog) {
        AlertDialog(
            onDismissRequest = { showTimeZoneDialog = false },
            title = { Text(Strings.get("timezone_label", language)) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 380.dp).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    DateTimeFormatterHelper.COMMON_TIMEZONES.forEach { tz ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSetTimeZone(tz)
                                    showTimeZoneDialog = false
                                },
                            color = if (tz == timeZone) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = tz,
                                modifier = Modifier.padding(12.dp),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = if (tz == timeZone) FontWeight.Bold else FontWeight.Normal)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showTimeZoneDialog = false }) { Text("Close") }
            }
        )
    }

    // Emergency Contacts Config Dialog
    if (showContactsDialog) {
        var tempFam by remember { mutableStateOf(familyPhone) }
        var tempMed by remember { mutableStateOf(medicalPhone) }

        AlertDialog(
            onDismissRequest = { showContactsDialog = false },
            title = { Text("Configure Emergency Contacts") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Enter numbers with international country calling code (e.g. ${currentCountry.callingCode}):", fontSize = 12.sp)
                    OutlinedTextField(
                        value = tempFam,
                        onValueChange = { tempFam = it },
                        label = { Text("Family Emergency Number") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = tempMed,
                        onValueChange = { tempMed = it },
                        label = { Text("Medical Emergency Number") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    onSetFamilyPhone(tempFam)
                    onSetMedicalPhone(tempMed)
                    showContactsDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showContactsDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Display Name Edit Dialog
    if (showEditNameDialog) {
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            title = { Text("Edit Display Name") },
            text = {
                OutlinedTextField(
                    value = tempDisplayName,
                    onValueChange = { tempDisplayName = it },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    onUpdateDisplayName(tempDisplayName)
                    showEditNameDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditNameDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Wipe Dialog
    if (showWipeConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showWipeConfirmDialog = false },
            title = { Text("Wipe all local data?") },
            text = {
                Text("All local messages, contacts, and discovery caches will be irreversibly erased.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showWipeConfirmDialog = false
                        onClearAllData()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed)
                ) {
                    Text("Erase")
                }
            },
            dismissButton = {
                TextButton(onClick = { showWipeConfirmDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Key Details Dialog
    if (showKeyDialog) {
        AlertDialog(
            onDismissRequest = { showKeyDialog = false },
            title = { Text("Keystore Cryptographic Details") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Algorithm: RSA-2048 / AES-GCM-256")
                    Text("Provider: Android KeyStore (Hardware-Backed)")
                    Text("Fingerprint:\n$fingerprint", fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = { showKeyDialog = false }) { Text("OK") }
            }
        )
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        ) {
            content()
        }
    }
}

