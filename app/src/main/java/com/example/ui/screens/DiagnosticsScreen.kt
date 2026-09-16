package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diagnostics.TestCase
import com.example.diagnostics.TestStatus
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.MeshGreen
import com.example.ui.theme.RelayGold

@Composable
fun DiagnosticsScreen(
    testCases: List<TestCase>,
    language: String,
    onRerunDiagnostics: () -> Unit
) {
    val passedCount = testCases.count { it.status == TestStatus.PASSED }
    val limitedCount = testCases.count { it.status == TestStatus.LIMITED }
    val failedCount = testCases.count { it.status == TestStatus.FAILED }
    val notSupportedCount = testCases.count { it.status == TestStatus.NOT_SUPPORTED }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("diagnostics_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            // Header card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Device Diagnostics",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "15-Point Hardware & Radio Transport Verification",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(onClick = onRerunDiagnostics) {
                            Icon(Icons.Default.Refresh, contentDescription = "Rerun", tint = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Summary counts
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SummaryChip(label = "Pass", count = passedCount, color = MeshGreen)
                        SummaryChip(label = "Limited", count = limitedCount, color = RelayGold)
                        SummaryChip(label = "Failed", count = failedCount, color = EmergencyRed)
                        SummaryChip(label = "Unsupported", count = notSupportedCount, color = Color.Gray)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Results reflect actual device environment & radio hardware without automated fake PASS.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        items(testCases, key = { it.id }) { testCase ->
            TestCaseItem(testCase = testCase)
        }
    }
}

@Composable
fun SummaryChip(label: String, count: Int, color: Color) {
    Surface(
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = color
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = color
            )
        }
    }
}

@Composable
fun TestCaseItem(testCase: TestCase) {
    val (statusColor, statusIcon, statusLabel) = when (testCase.status) {
        TestStatus.PASSED -> Triple(MeshGreen, Icons.Default.CheckCircle, "✅ PASS")
        TestStatus.LIMITED -> Triple(RelayGold, Icons.Default.Warning, "⚠️ LIMITED")
        TestStatus.FAILED -> Triple(EmergencyRed, Icons.Default.Error, "❌ FAILED")
        TestStatus.NOT_SUPPORTED -> Triple(Color.Gray, Icons.Default.RemoveCircleOutline, "➖ NOT SUPPORTED")
        TestStatus.RUNNING -> Triple(Color(0xFF00E5FF), Icons.Default.Refresh, "RUNNING")
        TestStatus.IDLE -> Triple(Color.Gray, Icons.Default.Refresh, "QUEUED")
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(statusIcon, contentDescription = null, tint = statusColor, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = testCase.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Surface(
                    color = statusColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = statusLabel,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = statusColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = testCase.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = testCase.resultDetail,
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace, fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
