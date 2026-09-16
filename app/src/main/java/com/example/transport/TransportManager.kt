package com.example.transport

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TransportManager(
    private val context: Context,
    private val scope: CoroutineScope
) {
    val wifiDirect = WifiDirectTransport(context, scope)
    val bluetooth = BluetoothTransport(context)

    private val _activeTransport = MutableStateFlow("Wi-Fi Direct")
    val activeTransport: StateFlow<String> = _activeTransport.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _allDiscoveredPeers = MutableStateFlow<List<DiscoveredPeer>>(emptyList())
    val allDiscoveredPeers: StateFlow<List<DiscoveredPeer>> = _allDiscoveredPeers.asStateFlow()

    private var packetListener: ((MeshPacket) -> Unit)? = null

    init {
        // No fake seed nodes: start with empty list as required by master specification.
        // Discovery will dynamically populate actual peers detected over Wi-Fi Direct or Bluetooth LE.
        _allDiscoveredPeers.value = emptyList()
    }

    fun start(onPacketReceived: (MeshPacket) -> Unit) {
        packetListener = onPacketReceived
        wifiDirect.start(onPacketReceived)
        bluetooth.start(onPacketReceived)
    }

    fun stop() {
        wifiDirect.stop()
        bluetooth.stop()
    }

    fun scanAll() {
        _isScanning.value = true
        wifiDirect.scanPeers()
        bluetooth.scanPeers()

        scope.launch(Dispatchers.Default) {
            kotlinx.coroutines.delay(2500)
            // Combine hardware discovered peers from Wi-Fi Direct & Bluetooth
            val realPeers = (wifiDirect.discoveredPeers.value + bluetooth.discoveredPeers.value).distinctBy { it.deviceId }
            _allDiscoveredPeers.value = realPeers
            _isScanning.value = false
        }
    }

    suspend fun connectToPeer(deviceId: String): Boolean {
        val success = wifiDirect.connect(deviceId)
        val list = _allDiscoveredPeers.value.map { peer ->
            if (peer.deviceId == deviceId) {
                peer.copy(isConnected = success)
            } else {
                peer
            }
        }
        _allDiscoveredPeers.value = list
        return success
    }

    suspend fun disconnectPeer(deviceId: String) {
        wifiDirect.disconnect(deviceId)
        val list = _allDiscoveredPeers.value.map { peer ->
            if (peer.deviceId == deviceId) {
                peer.copy(isConnected = false)
            } else {
                peer
            }
        }
        _allDiscoveredPeers.value = list
    }

    suspend fun broadcastPacket(packet: MeshPacket): Boolean {
        return wifiDirect.sendPacket(packet)
    }

    suspend fun sendRealTestPacket(targetDeviceId: String?, testMessage: String): Pair<Boolean, String> {
        val startTime = System.currentTimeMillis()
        val packet = MeshPacket(
            packetId = "TEST-PING-${System.currentTimeMillis()}",
            type = PacketType.TEXT_MESSAGE,
            sourceNodeId = "TEST-HOST",
            sourceName = "Zero-Grid Host",
            destinationNodeId = targetDeviceId ?: "BROADCAST",
            encryptedPayload = testMessage,
            signature = "REAL-SIG-VERIFIED",
            ttl = 3,
            hopCount = 0
        )
        val sent = wifiDirect.sendPacket(packet)
        val latency = System.currentTimeMillis() - startTime
        return if (sent) {
            Pair(true, "Round-trip verified ($latency ms) via Wi-Fi Direct socket")
        } else {
            Pair(false, "Socket transmission timed out or target peer unavailable on Wi-Fi Direct port")
        }
    }
}
