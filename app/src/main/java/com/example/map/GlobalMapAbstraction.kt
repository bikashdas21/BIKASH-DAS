package com.example.map

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.community.GlobalCommunityNode

interface MapProvider {
    val providerName: String
    val isOfflineCapable: Boolean
}

class OfflineVectorMapProvider : MapProvider {
    override val providerName: String = "Zero-Grid Vector Grid (Offline GNSS)"
    override val isOfflineCapable: Boolean = true
}

data class MapNodePin(
    val id: String,
    val title: String,
    val latitude: Double,
    val longitude: Double,
    val isGateway: Boolean,
    val isSolar: Boolean,
    val status: String,
    val country: String
)

@Composable
fun GlobalMapView(
    nodes: List<GlobalCommunityNode>,
    selectedNodeId: String?,
    onNodeSelected: (GlobalCommunityNode) -> Unit,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableFloatStateOf(1.0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 6f,
        targetValue = 22f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_radius"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF030712))
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(0.6f, 5.0f)
                    offset += pan
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val centerX = width / 2f + offset.x
            val centerY = height / 2f + offset.y

            // Draw World Coordinate Grid (Latitude & Longitude lines)
            val gridColor = Color(0xFF1E293B)
            val equatorColor = Color(0xFF0284C7)

            // Latitude lines (-60 to +60 in steps of 30)
            for (lat in -60..60 step 30) {
                val y = centerY - (lat / 90f) * (height * 0.45f * scale)
                drawLine(
                    color = if (lat == 0) equatorColor else gridColor,
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = if (lat == 0) 2f else 1f
                )
            }

            // Longitude lines (-180 to +180 in steps of 45)
            for (lon in -180..180 step 45) {
                val x = centerX + (lon / 180f) * (width * 0.48f * scale)
                drawLine(
                    color = if (lon == 0) equatorColor else gridColor,
                    start = Offset(x, 0f),
                    end = Offset(x, height),
                    strokeWidth = if (lon == 0) 2f else 1f
                )
            }

            // Draw stylized continental landmass approximations (Equirectangular projection)
            drawWorldContinents(centerX, centerY, width, height, scale)

            // Convert nodes to map coordinates
            val nodePoints = nodes.mapNotNull { node ->
                if (node.latitude != null && node.longitude != null) {
                    val x = centerX + (node.longitude.toFloat() / 180f) * (width * 0.48f * scale)
                    val y = centerY - (node.latitude.toFloat() / 90f) * (height * 0.45f * scale)
                    Triple(node, Offset(x, y), node.isGateway)
                } else null
            }

            // Draw Relay Mesh links between nearby nodes
            for (i in nodePoints.indices) {
                for (j in i + 1 until nodePoints.size) {
                    val p1 = nodePoints[i].second
                    val p2 = nodePoints[j].second
                    val distancePx = (p1 - p2).getDistance()
                    if (distancePx < 250f * scale) {
                        drawLine(
                            color = Color(0x5500F0FF),
                            start = p1,
                            end = p2,
                            strokeWidth = 1.5f * scale
                        )
                    }
                }
            }

            // Draw Node Markers
            nodePoints.forEach { (node, pt, isGateway) ->
                val isSelected = node.nodeId == selectedNodeId

                // Pulse ring for active nodes
                drawCircle(
                    color = if (isGateway) Color(0x3300E5FF) else Color(0x3300FF88),
                    radius = pulseRadius * scale,
                    center = pt
                )

                // Node center circle
                drawCircle(
                    color = if (isGateway) Color(0xFF00E5FF) else if (node.isSolarPowered) Color(0xFFFFB300) else Color(0xFF00FF88),
                    radius = (if (isSelected) 8f else 5f) * scale,
                    center = pt
                )

                if (isSelected) {
                    drawCircle(
                        color = Color.White,
                        radius = 11f * scale,
                        center = pt,
                        style = Stroke(width = 2f)
                    )
                }
            }
        }

        // Overlay Map Controls & Disclosures
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
        ) {
            Surface(
                color = Color(0xDD0B132B),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        "🌐 Offline World Mesh Topology",
                        color = Color(0xFF00F0FF),
                        fontSize = 12.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                    Text(
                        "P2P GNSS Projection • No Google API lock-in",
                        color = Color(0xFF94A3B8),
                        fontSize = 10.sp
                    )
                }
            }
        }

        // Legend overlay at bottom
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(12.dp)
                .background(Color(0xDD0A0F1D), RoundedCornerShape(20.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).background(Color(0xFF00FF88), androidx.compose.foundation.shape.CircleShape))
                Spacer(Modifier.width(4.dp))
                Text("Peer Node", color = Color(0xFFE2E8F0), fontSize = 11.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).background(Color(0xFFFFB300), androidx.compose.foundation.shape.CircleShape))
                Spacer(Modifier.width(4.dp))
                Text("Solar Hub", color = Color(0xFFE2E8F0), fontSize = 11.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).background(Color(0xFF00E5FF), androidx.compose.foundation.shape.CircleShape))
                Spacer(Modifier.width(4.dp))
                Text("Internet Gateway", color = Color(0xFFE2E8F0), fontSize = 11.sp)
            }
        }
    }
}

private fun DrawScope.drawWorldContinents(
    centerX: Float,
    centerY: Float,
    width: Float,
    height: Float,
    scale: Float
) {
    val continentColor = Color(0x1838BDF8)
    val strokeColor = Color(0x280284C7)

    fun toPoint(lat: Float, lon: Float): Offset {
        val x = centerX + (lon / 180f) * (width * 0.48f * scale)
        val y = centerY - (lat / 90f) * (height * 0.45f * scale)
        return Offset(x, y)
    }

    // Simplified polygon contours for world regions (Offline vector approximation)
    // North America
    val na = Path().apply {
        val pts = listOf(
            toPoint(70f, -165f), toPoint(70f, -80f), toPoint(45f, -60f),
            toPoint(25f, -80f), toPoint(15f, -95f), toPoint(30f, -120f),
            toPoint(60f, -140f)
        )
        moveTo(pts[0].x, pts[0].y)
        pts.drop(1).forEach { lineTo(it.x, it.y) }
        close()
    }
    drawPath(na, continentColor)
    drawPath(na, strokeColor, style = Stroke(1.2f))

    // South America
    val sa = Path().apply {
        val pts = listOf(
            toPoint(10f, -75f), toPoint(-5f, -35f), toPoint(-35f, -55f),
            toPoint(-55f, -70f), toPoint(-20f, -70f), toPoint(0f, -80f)
        )
        moveTo(pts[0].x, pts[0].y)
        pts.drop(1).forEach { lineTo(it.x, it.y) }
        close()
    }
    drawPath(sa, continentColor)
    drawPath(sa, strokeColor, style = Stroke(1.2f))

    // Eurasia
    val eurasia = Path().apply {
        val pts = listOf(
            toPoint(70f, 10f), toPoint(75f, 170f), toPoint(60f, 160f),
            toPoint(35f, 120f), toPoint(10f, 105f), toPoint(10f, 75f),
            toPoint(25f, 55f), toPoint(35f, 25f), toPoint(45f, -5f),
            toPoint(60f, 5f)
        )
        moveTo(pts[0].x, pts[0].y)
        pts.drop(1).forEach { lineTo(it.x, it.y) }
        close()
    }
    drawPath(eurasia, continentColor)
    drawPath(eurasia, strokeColor, style = Stroke(1.2f))

    // Africa
    val africa = Path().apply {
        val pts = listOf(
            toPoint(35f, -5f), toPoint(30f, 32f), toPoint(10f, 50f),
            toPoint(-10f, 40f), toPoint(-35f, 20f), toPoint(5f, 10f),
            toPoint(15f, -15f)
        )
        moveTo(pts[0].x, pts[0].y)
        pts.drop(1).forEach { lineTo(it.x, it.y) }
        close()
    }
    drawPath(africa, continentColor)
    drawPath(africa, strokeColor, style = Stroke(1.2f))

    // Australia
    val australia = Path().apply {
        val pts = listOf(
            toPoint(-12f, 130f), toPoint(-15f, 150f), toPoint(-38f, 150f),
            toPoint(-35f, 115f), toPoint(-20f, 115f)
        )
        moveTo(pts[0].x, pts[0].y)
        pts.drop(1).forEach { lineTo(it.x, it.y) }
        close()
    }
    drawPath(australia, continentColor)
    drawPath(australia, strokeColor, style = Stroke(1.2f))
}
