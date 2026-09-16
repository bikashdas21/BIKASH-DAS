package com.example.mesh

import com.example.core.PreferencesManager
import com.example.core.crypto.CryptoEngine
import com.example.database.AppDatabase
import com.example.database.Message
import com.example.database.NetworkNode
import com.example.transport.MeshPacket
import com.example.transport.PacketType
import com.example.transport.TransportManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

enum class NetworkConnectionState {
    NETWORK_AVAILABLE,    // Connected to mesh and gateway
    OFFLINE_MESH,         // Connected to offline mesh nodes
    NO_CONNECTION         // Isolated
}

class MeshRouter(
    private val database: AppDatabase,
    private val transportManager: TransportManager,
    private val prefs: PreferencesManager,
    private val scope: CoroutineScope
) {
    // Visited packet IDs to prevent routing loops and duplicate relays
    private val visitedPacketIds = ConcurrentHashMap.newKeySet<String>()

    // Priority queue for SOS packets
    private val _incomingSosAlert = MutableStateFlow<MeshPacket?>(null)
    val incomingSosAlert: StateFlow<MeshPacket?> = _incomingSosAlert.asStateFlow()

    private val _networkState = MutableStateFlow(NetworkConnectionState.OFFLINE_MESH)
    val networkState: StateFlow<NetworkConnectionState> = _networkState.asStateFlow()

    private val _hasGateway = MutableStateFlow(false)
    val hasGateway: StateFlow<Boolean> = _hasGateway.asStateFlow()

    init {
        if (prefs.isZeroGridActive.value) {
            activate()
        } else {
            _networkState.value = NetworkConnectionState.NO_CONNECTION
        }
    }

    fun activate() {
        transportManager.start { packet ->
            handleIncomingPacket(packet)
        }
        transportManager.scanAll()
        evaluateNetworkState()
    }

    fun deactivate() {
        transportManager.stop()
        _hasGateway.value = false
        _networkState.value = NetworkConnectionState.NO_CONNECTION
    }

    fun dismissSosAlert() {
        _incomingSosAlert.value = null
    }

    private fun evaluateNetworkState() {
        scope.launch(Dispatchers.Default) {
            val peers = transportManager.allDiscoveredPeers.value
            val connected = peers.filter { it.isConnected }
            val hasGw = connected.any { it.isGateway }
            _hasGateway.value = hasGw

            _networkState.value = when {
                connected.isEmpty() -> NetworkConnectionState.NO_CONNECTION
                hasGw -> NetworkConnectionState.NETWORK_AVAILABLE
                else -> NetworkConnectionState.OFFLINE_MESH
            }
        }
    }

    fun sendTextMessage(
        receiverId: String,
        content: String,
        isSos: Boolean = false,
        lat: Double? = null,
        lon: Double? = null
    ) {
        scope.launch(Dispatchers.IO) {
            if (!prefs.isZeroGridActive.value) {
                return@launch
            }
            val messageId = "MSG-" + System.currentTimeMillis() + "-" + (1000..9999).random()
            val encrypted = CryptoEngine.encryptPayload(content)
            val signature = CryptoEngine.signData(content)

            // Save in local Room database
            val localMessage = Message(
                messageId = messageId,
                senderId = prefs.myNodeId,
                senderName = prefs.myDisplayName,
                receiverId = receiverId,
                encryptedPayload = encrypted,
                plaintextCache = content,
                timestamp = System.currentTimeMillis(),
                ttl = 5,
                hopCount = 0,
                status = if (transportManager.allDiscoveredPeers.value.any { it.isConnected }) "RELAYING" else "OFFLINE",
                isSos = isSos
            )
            database.messageDao().insertMessage(localMessage)

            // Wrap in MeshPacket
            val packet = MeshPacket(
                packetId = messageId,
                type = if (isSos) PacketType.SOS_ALERT else PacketType.TEXT_MESSAGE,
                sourceNodeId = prefs.myNodeId,
                sourceName = prefs.myDisplayName,
                destinationNodeId = receiverId,
                encryptedPayload = encrypted,
                signature = signature,
                ttl = 5,
                hopCount = 0,
                timestamp = System.currentTimeMillis(),
                isSos = isSos,
                gpsLat = lat,
                gpsLon = lon
            )

            visitedPacketIds.add(messageId)
            val sent = transportManager.broadcastPacket(packet)

            if (sent) {
                database.messageDao().updateMessageStatus(messageId, "DELIVERED")
            }
            evaluateNetworkState()
        }
    }

    fun sendVoiceMessage(
        receiverId: String,
        audioFilePath: String,
        durationSeconds: Int
    ) {
        scope.launch(Dispatchers.IO) {
            val messageId = "VOICE-" + System.currentTimeMillis()
            val dummyEncrypted = CryptoEngine.encryptPayload("Voice Note ($durationSeconds s)")

            val localMessage = Message(
                messageId = messageId,
                senderId = prefs.myNodeId,
                senderName = prefs.myDisplayName,
                receiverId = receiverId,
                encryptedPayload = dummyEncrypted,
                plaintextCache = "🎙️ ভয়েস মেসেজ (${durationSeconds} সে)",
                timestamp = System.currentTimeMillis(),
                ttl = 5,
                hopCount = 0,
                status = "DELIVERED",
                isVoice = true,
                mediaUri = audioFilePath
            )
            database.messageDao().insertMessage(localMessage)

            val packet = MeshPacket(
                packetId = messageId,
                type = PacketType.VOICE_MESSAGE,
                sourceNodeId = prefs.myNodeId,
                sourceName = prefs.myDisplayName,
                destinationNodeId = receiverId,
                encryptedPayload = dummyEncrypted,
                signature = CryptoEngine.signData(messageId),
                ttl = 5,
                hopCount = 0,
                fileName = audioFilePath
            )
            visitedPacketIds.add(messageId)
            transportManager.broadcastPacket(packet)
        }
    }

    private fun handleIncomingPacket(packet: MeshPacket) {
        scope.launch(Dispatchers.IO) {
            if (!prefs.isZeroGridActive.value) {
                return@launch
            }
            // 1. Duplicate detection: Ignore if visited already
            if (visitedPacketIds.contains(packet.packetId)) {
                return@launch
            }
            visitedPacketIds.add(packet.packetId)

            // 2. Check Expiration & TTL
            if (packet.ttl <= 0) {
                return@launch
            }

            val isForMe = packet.destinationNodeId == "BROADCAST" ||
                    packet.destinationNodeId == prefs.myNodeId

            // Decrypt payload if it's for me
            if (isForMe) {
                val decrypted = CryptoEngine.decryptPayload(packet.encryptedPayload)
                val msg = Message(
                    messageId = packet.packetId,
                    senderId = packet.sourceNodeId,
                    senderName = packet.sourceName,
                    receiverId = packet.destinationNodeId,
                    encryptedPayload = packet.encryptedPayload,
                    plaintextCache = decrypted,
                    timestamp = packet.timestamp,
                    ttl = packet.ttl,
                    hopCount = packet.hopCount,
                    status = "DELIVERED",
                    isSos = packet.isSos
                )
                database.messageDao().insertMessage(msg)

                // Update node in database
                val node = NetworkNode(
                    nodeId = packet.sourceNodeId,
                    nodeName = packet.sourceName,
                    lastSeen = System.currentTimeMillis(),
                    hopCount = packet.hopCount,
                    connectionType = "Wi-Fi Direct",
                    status = "CONNECTED",
                    isGateway = false,
                    latitude = packet.gpsLat,
                    longitude = packet.gpsLon
                )
                database.networkNodeDao().insertNode(node)

                if (packet.isSos) {
                    _incomingSosAlert.value = packet
                }
            }

            // 3. Controlled Store & Forward Relay:
            // Intermediate device relay node acts as relay without decrypting payload!
            if (prefs.isRelayMode.value && packet.ttl > 1) {
                val relayPacket = packet.copy(
                    ttl = packet.ttl - 1,
                    hopCount = packet.hopCount + 1
                )
                // Relay onwards to other nearby devices
                transportManager.broadcastPacket(relayPacket)
            }
            evaluateNetworkState()
        }
    }
}
