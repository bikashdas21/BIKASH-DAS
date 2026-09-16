package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.location.LocationShareMode
import com.example.location.OfflineLocation
import com.example.location.OfflineLocationManager
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.MeshGreen
import com.example.ui.theme.OfflineGray

@Composable
fun LocationScreen(
    currentLocation: OfflineLocation?,
    shareMode: LocationShareMode,
    isBangla: Boolean,
    onRequestLocationMode: (LocationShareMode) -> Unit,
    onBroadcastLocationToMesh: (OfflineLocation) -> Unit
) {
    val context = LocalContext.current

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        val fineGranted = perms[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineGranted || coarseGranted) {
            onRequestLocationMode(LocationShareMode.SHARE_ONCE)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .testTag("location_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (isBangla) "অফলাইন লোকেশন শেয়ারিং" else "Offline Location Sharing",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = if (isBangla) "Google Play Services ছাড়াই সরাসরি ডিভাইসের হার্ডওয়্যার GPS চিপসেট থেকে কোঅর্ডিনেট নেওয়া হয়।"
            else "Raw coordinates from device hardware GPS chipset without Google Play Services.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // GPS Coordinates Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(ElectricCyan.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = null,
                        tint = ElectricCyan,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (currentLocation != null) {
                    Text(
                        text = OfflineLocationManager.formatCoordinates(
                            currentLocation.latitude,
                            currentLocation.longitude
                        ),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isBangla) "নির্ভুলতা: ~${currentLocation.accuracyMeters.toInt()} মিটার (GPS ফিক্সড)"
                        else "Accuracy: ~${currentLocation.accuracyMeters.toInt()} m (Hardware GPS Fix)",
                        style = MaterialTheme.typography.labelMedium,
                        color = MeshGreen
                    )
                } else {
                    Text(
                        text = if (isBangla) "GPS সিগন্যাল খোঁজা হচ্ছে…" else "Acquiring GPS Fix…",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Privacy-Respecting Sharing Limit Options
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (isBangla) "শেয়ারিং অপশন ও সময়সীমা (Privacy Controlled):"
                    else "Privacy Controlled Duration:",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Button 1: Share Once
                FilledTonalButton(
                    onClick = {
                        val hasPerm = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED

                        if (!hasPerm) {
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        } else {
                            onRequestLocationMode(LocationShareMode.SHARE_ONCE)
                            currentLocation?.let { onBroadcastLocationToMesh(it) }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isBangla) "একবার শেয়ার করুন (Share Once)" else "Share Once")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Button 2: Share for 15 minutes
                Button(
                    onClick = {
                        val hasPerm = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED

                        if (!hasPerm) {
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        } else {
                            onRequestLocationMode(LocationShareMode.SHARE_15_MINUTES)
                            currentLocation?.let { onBroadcastLocationToMesh(it) }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (shareMode == LocationShareMode.SHARE_15_MINUTES) MeshGreen
                        else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (shareMode == LocationShareMode.SHARE_15_MINUTES) {
                            if (isBangla) "১৫ মিনিটের জন্য শেয়ারিং সক্রিয়" else "Sharing Active (15m)"
                        } else {
                            if (isBangla) "১৫ মিনিটের জন্য শেয়ার করুন" else "Share for 15 minutes"
                        }
                    )
                }

                if (shareMode != LocationShareMode.OFF) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { onRequestLocationMode(LocationShareMode.OFF) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (isBangla) "শেয়ারিং বন্ধ করুন (Stop Sharing)" else "Stop Sharing")
                    }
                }
            }
        }

        // Privacy Guarantee Card
        Row(
            modifier = Modifier.padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Security, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isBangla) "লোকেশন কোনো কেন্দ্রীয় ক্লাউডে সংরক্ষণ বা ট্র্যাকিং করা হয় না।"
                else "Location is strictly end-to-end and never logged on central servers.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
