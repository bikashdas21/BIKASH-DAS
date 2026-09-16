package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.MeshGreen
import com.example.ui.theme.RelayGold

enum class PermissionState {
    GRANTED,
    LIMITED,
    NOT_GRANTED,
    NOT_REQUIRED
}

data class PermissionItem(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val permissions: List<String>,
    val minSdk: Int = 0,
    val maxSdk: Int = Int.MAX_VALUE
)

@Composable
fun PermissionCenterScreen(
    language: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var refreshTrigger by remember { mutableIntStateOf(0) }

    val permissionItems = remember {
        listOf(
            PermissionItem(
                id = "nearby_wifi",
                title = "Nearby Wi-Fi Devices",
                description = "Required on Android 13+ to discover and connect to peers via Wi-Fi Direct without location tracking.",
                icon = Icons.Default.WifiTethering,
                permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    listOf(Manifest.permission.NEARBY_WIFI_DEVICES)
                } else emptyList(),
                minSdk = Build.VERSION_CODES.TIRAMISU
            ),
            PermissionItem(
                id = "bluetooth",
                title = "Bluetooth & BLE Mesh",
                description = "Allows discovering nearby Zero-Grid beacons and establishing peer-to-peer relay channels.",
                icon = Icons.Default.Bluetooth,
                permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    listOf(
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.BLUETOOTH_ADVERTISE
                    )
                } else {
                    listOf(
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN
                    )
                }
            ),
            PermissionItem(
                id = "location",
                title = "Precise Location (GNSS)",
                description = "Used strictly for offline emergency SOS coordinates and Wi-Fi Direct discovery on Android 12 and below.",
                icon = Icons.Default.MyLocation,
                permissions = listOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            ),
            PermissionItem(
                id = "microphone",
                title = "Microphone (Voice Engine)",
                description = "Required to record lightweight encrypted voice walkie-talkie packets over the local mesh.",
                icon = Icons.Default.Mic,
                permissions = listOf(Manifest.permission.RECORD_AUDIO)
            ),
            PermissionItem(
                id = "notifications",
                title = "Notifications & Alerts",
                description = "Alerts you immediately when a high-priority SOS broadcast or emergency message is received.",
                icon = Icons.Default.Notifications,
                permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    listOf(Manifest.permission.POST_NOTIFICATIONS)
                } else emptyList(),
                minSdk = Build.VERSION_CODES.TIRAMISU
            )
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        refreshTrigger++
    }

    fun checkStatus(item: PermissionItem): PermissionState {
        if (Build.VERSION.SDK_INT < item.minSdk || Build.VERSION.SDK_INT > item.maxSdk) {
            return PermissionState.NOT_REQUIRED
        }
        if (item.permissions.isEmpty()) {
            return PermissionState.NOT_REQUIRED
        }
        val grantedCount = item.permissions.count {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
        return when {
            grantedCount == item.permissions.size -> PermissionState.GRANTED
            grantedCount > 0 -> PermissionState.LIMITED
            else -> PermissionState.NOT_GRANTED
        }
    }

    // Force re-check on refreshTrigger
    val currentStatuses = remember(refreshTrigger) {
        permissionItems.associate { it.id to checkStatus(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("permission_center_screen")
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
                        text = if (language == "bn") "অনুমতি কেন্দ্র" else "Permission Center",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Technical Truth & Hardware Access Status",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(onClick = { refreshTrigger++ }) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Status Card Explanation
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Permission Transparency Policy",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Zero-Grid requests only permissions genuinely needed for device-to-device radio transport and emergency communication. No telemetry or analytics data is ever collected.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Permissions List
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(permissionItems, key = { it.id }) { item ->
                val state = currentStatuses[item.id] ?: PermissionState.NOT_GRANTED

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
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
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surface),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = item.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Status Badge
                            StatusBadge(state)
                        }

                        if (state == PermissionState.NOT_GRANTED || state == PermissionState.LIMITED) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Button(
                                    onClick = {
                                        if (item.permissions.isNotEmpty()) {
                                            launcher.launch(item.permissions.toTypedArray())
                                        }
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan)
                                ) {
                                    Text(
                                        text = "Grant Permission",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Open App Settings Button
        OutlinedButton(
            onClick = {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("btn_open_app_settings"),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
        ) {
            Icon(Icons.Default.Settings, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (language == "bn") "সিস্টেম সেটিংস খুলুন" else "Open App Settings",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun StatusBadge(state: PermissionState) {
    val (text, color, icon) = when (state) {
        PermissionState.GRANTED -> Triple("✅ Granted", MeshGreen, Icons.Default.CheckCircle)
        PermissionState.LIMITED -> Triple("⚠️ Limited", RelayGold, Icons.Default.Warning)
        PermissionState.NOT_GRANTED -> Triple("❌ Not Granted", EmergencyRed, Icons.Default.Cancel)
        PermissionState.NOT_REQUIRED -> Triple("➖ Not Required", Color.Gray, Icons.Default.RemoveCircleOutline)
    }

    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = color
            )
        }
    }
}
