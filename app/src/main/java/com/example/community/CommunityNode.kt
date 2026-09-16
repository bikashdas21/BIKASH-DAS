package com.example.community

enum class CommunityNodeType(val label: String, val icon: String) {
    HOME_NODE("Home Node", "🏠"),
    VILLAGE_NODE("Village Node", "🏡"),
    COMMUNITY_NODE("Community Node", "🏛️"),
    DISASTER_RESPONSE("Disaster-Response Node", "🚨"),
    CAMPUS_NODE("Campus Node", "🎓"),
    EVENT_NODE("Event Node", "🎪"),
    REMOTE_AREA("Remote-Area Node", "⛰️"),
    SOLAR_POWERED("Solar-Powered Node", "☀️")
}

data class SolarTelemetry(
    val solarInputWatts: Double,
    val batteryVoltage: Double,
    val batteryTempC: Double,
    val isCharging: Boolean
)

data class GlobalCommunityNode(
    val nodeId: String,
    val name: String,
    val type: CommunityNodeType,
    val countryCode: String,
    val regionOrState: String,
    val status: String, // ONLINE, RELAY_ACTIVE, OFFLINE
    val availableTransports: List<String>,
    val batteryLevel: Int?,
    val isSolarPowered: Boolean,
    val solarTelemetry: SolarTelemetry?,
    val isGateway: Boolean,
    val lastSeenTime: Long,
    val latitude: Double?,
    val longitude: Double?,
    val hardwareSpecNote: String = "Example / Recommended specification"
)

object GlobalCommunityNodeRegistry {
    val SAMPLE_GLOBAL_NODES: List<GlobalCommunityNode> = listOf(
        GlobalCommunityNode(
            nodeId = "NODE-SOLAR-01",
            name = "Sundarbans Coastal Solar Hub",
            type = CommunityNodeType.SOLAR_POWERED,
            countryCode = "BD",
            regionOrState = "Khulna / Bay of Bengal",
            status = "ONLINE",
            availableTransports = listOf("Wi-Fi Direct", "BLE", "LoRa AS923"),
            batteryLevel = 98,
            isSolarPowered = true,
            solarTelemetry = SolarTelemetry(42.5, 13.4, 28.5, true),
            isGateway = false,
            lastSeenTime = System.currentTimeMillis() - 45000,
            latitude = 21.9497,
            longitude = 89.1833
        ),
        GlobalCommunityNode(
            nodeId = "NODE-ALPS-07",
            name = "Alpine High-Pass Shelter Node",
            type = CommunityNodeType.REMOTE_AREA,
            countryCode = "DE",
            regionOrState = "Bavaria / Alps",
            status = "RELAY_ACTIVE",
            availableTransports = listOf("Wi-Fi Direct", "BLE", "LoRa EU868"),
            batteryLevel = 84,
            isSolarPowered = true,
            solarTelemetry = SolarTelemetry(24.0, 12.8, 12.0, true),
            isGateway = false,
            lastSeenTime = System.currentTimeMillis() - 120000,
            latitude = 47.4211,
            longitude = 10.9853
        ),
        GlobalCommunityNode(
            nodeId = "NODE-US-GATE-03",
            name = "Pacific Northwest Internet Gateway",
            type = CommunityNodeType.COMMUNITY_NODE,
            countryCode = "US",
            regionOrState = "Oregon",
            status = "ONLINE",
            availableTransports = listOf("Wi-Fi Direct", "Local Wi-Fi", "Starlink Backhaul"),
            batteryLevel = 100,
            isSolarPowered = false,
            solarTelemetry = null,
            isGateway = true,
            lastSeenTime = System.currentTimeMillis() - 15000,
            latitude = 45.5152,
            longitude = -122.6784
        ),
        GlobalCommunityNode(
            nodeId = "NODE-HIMALAYA-09",
            name = "Ladakh High-Altitude Village Node",
            type = CommunityNodeType.VILLAGE_NODE,
            countryCode = "IN",
            regionOrState = "Ladakh",
            status = "ONLINE",
            availableTransports = listOf("Wi-Fi Direct", "BLE", "LoRa IN865"),
            batteryLevel = 91,
            isSolarPowered = true,
            solarTelemetry = SolarTelemetry(55.2, 13.6, 8.5, true),
            isGateway = false,
            lastSeenTime = System.currentTimeMillis() - 80000,
            latitude = 34.1526,
            longitude = 77.5771
        ),
        GlobalCommunityNode(
            nodeId = "NODE-DISASTER-SAO",
            name = "Serra Do Mar Flood Relief Relay",
            type = CommunityNodeType.DISASTER_RESPONSE,
            countryCode = "BR",
            regionOrState = "São Paulo",
            status = "RELAY_ACTIVE",
            availableTransports = listOf("Wi-Fi Direct", "BLE", "LoRa AU915"),
            batteryLevel = 76,
            isSolarPowered = true,
            solarTelemetry = SolarTelemetry(18.5, 12.4, 25.0, true),
            isGateway = false,
            lastSeenTime = System.currentTimeMillis() - 210000,
            latitude = -23.5505,
            longitude = -46.6333
        ),
        GlobalCommunityNode(
            nodeId = "NODE-TOKYO-CAMPUS",
            name = "Shinjuku Decentralized Campus Hub",
            type = CommunityNodeType.CAMPUS_NODE,
            countryCode = "JP",
            regionOrState = "Tokyo",
            status = "ONLINE",
            availableTransports = listOf("Wi-Fi Direct", "Local Wi-Fi", "BLE"),
            batteryLevel = 100,
            isSolarPowered = false,
            solarTelemetry = null,
            isGateway = true,
            lastSeenTime = System.currentTimeMillis() - 30000,
            latitude = 35.6895,
            longitude = 139.6917
        )
    )
}
