package com.example.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String,
    val username: String,
    val displayName: String,
    val publicKey: String,
    val avatarColorHex: String = "#00E5FF",
    val isGuest: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "devices")
data class Device(
    @PrimaryKey val deviceId: String,
    val userId: String,
    val deviceName: String,
    val lastSeen: Long = System.currentTimeMillis(),
    val connectionType: String = "Wi-Fi Direct",
    val status: String = "AVAILABLE", // CONNECTED, AVAILABLE, DISCONNECTED
    val signalDbm: Int = -65,
    val approximateDistanceMeters: Double = 12.0
)

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey val messageId: String,
    val senderId: String,
    val senderName: String,
    val receiverId: String, // "BROADCAST" or specific userId
    val groupId: String? = null,
    val encryptedPayload: String,
    val plaintextCache: String,
    val timestamp: Long = System.currentTimeMillis(),
    val ttl: Int = 5,
    val hopCount: Int = 0,
    val status: String = "DELIVERED", // SENDING, RELAYING, DELIVERED, FAILED, OFFLINE
    val isSos: Boolean = false,
    val isVoice: Boolean = false,
    val isFile: Boolean = false,
    val mediaUri: String? = null,
    val fileName: String? = null,
    val fileSize: Long? = null
)

@Entity(tableName = "transfers")
data class Transfer(
    @PrimaryKey val transferId: String,
    val fileName: String,
    val size: Long,
    val checksum: String,
    val progress: Int = 0, // 0 to 100
    val speedKbps: Double = 0.0,
    val direction: String = "SEND", // SEND, RECEIVE
    val status: String = "IN_PROGRESS", // IN_PROGRESS, PAUSED, COMPLETED, FAILED
    val totalChunks: Int = 1,
    val transferredChunks: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "network_nodes")
data class NetworkNode(
    @PrimaryKey val nodeId: String,
    val nodeName: String,
    val lastSeen: Long = System.currentTimeMillis(),
    val hopCount: Int = 1,
    val connectionType: String = "Wi-Fi Direct",
    val status: String = "CONNECTED", // CONNECTED, RELAY, OFFLINE
    val isGateway: Boolean = false,
    val batteryLevel: Int = 90,
    val isCommunityNode: Boolean = false,
    val latitude: Double? = null,
    val longitude: Double? = null
)
