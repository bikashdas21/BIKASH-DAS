package com.example.emergency

data class EmergencyContact(
    val id: String,
    val role: String, // Family, Local Emergency, Community Responder, Medical Contact
    val name: String,
    val phoneWithCountryCode: String,
    val isVerified: Boolean = false
)

enum class SosState {
    IDLE,
    CREATED,
    SENDING,
    RELAYING,
    DELIVERED,
    OFFLINE
}

data class SosStatusReport(
    val state: SosState,
    val packetId: String,
    val timestamp: Long,
    val relayHopCount: Int,
    val recipientNodes: List<String>,
    val gpsLat: Double?,
    val gpsLon: Double?,
    val note: String
)
