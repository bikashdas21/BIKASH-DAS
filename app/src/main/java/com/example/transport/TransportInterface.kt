package com.example.transport

import kotlinx.coroutines.flow.StateFlow

data class DiscoveredPeer(
    val deviceId: String,
    val name: String,
    val transportType: String, // "Wi-Fi Direct", "Wi-Fi Aware", "Bluetooth LE"
    val signalDbm: Int = -60,
    val distanceMeters: Double = 10.0,
    val isConnected: Boolean = false,
    val isGateway: Boolean = false,
    val isCommunityNode: Boolean = false
)

interface TransportInterface {
    val transportName: String
    val isAvailable: Boolean
    val isScanning: StateFlow<Boolean>
    val discoveredPeers: StateFlow<List<DiscoveredPeer>>

    fun start(onPacketReceived: (MeshPacket) -> Unit)
    fun stop()
    fun scanPeers()
    suspend fun connect(peerId: String): Boolean
    suspend fun disconnect(peerId: String)
    suspend fun sendPacket(packet: MeshPacket): Boolean
}
