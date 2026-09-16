package com.example.transport

import org.json.JSONObject

enum class PacketType {
    DISCOVERY_BEACON,
    TEXT_MESSAGE,
    VOICE_MESSAGE,
    FILE_HEADER,
    FILE_CHUNK,
    SOS_ALERT,
    RELAY_WRAPPER,
    DELIVERY_ACK
}

data class MeshPacket(
    val packetId: String,
    val type: PacketType,
    val sourceNodeId: String,
    val sourceName: String,
    val destinationNodeId: String, // "BROADCAST" or specific Node ID
    val encryptedPayload: String,
    val signature: String,
    val ttl: Int = 5,
    val hopCount: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val isSos: Boolean = false,
    val gpsLat: Double? = null,
    val gpsLon: Double? = null,
    val fileName: String? = null,
    val fileSize: Long? = null
) {
    fun toJson(): String {
        val json = JSONObject()
        json.put("packetId", packetId)
        json.put("type", type.name)
        json.put("sourceNodeId", sourceNodeId)
        json.put("sourceName", sourceName)
        json.put("destinationNodeId", destinationNodeId)
        json.put("encryptedPayload", encryptedPayload)
        json.put("signature", signature)
        json.put("ttl", ttl)
        json.put("hopCount", hopCount)
        json.put("timestamp", timestamp)
        json.put("isSos", isSos)
        if (gpsLat != null) json.put("gpsLat", gpsLat)
        if (gpsLon != null) json.put("gpsLon", gpsLon)
        if (fileName != null) json.put("fileName", fileName)
        if (fileSize != null) json.put("fileSize", fileSize)
        return json.toString()
    }

    companion object {
        fun fromJson(jsonStr: String): MeshPacket? {
            return try {
                val json = JSONObject(jsonStr)
                MeshPacket(
                    packetId = json.getString("packetId"),
                    type = PacketType.valueOf(json.getString("type")),
                    sourceNodeId = json.getString("sourceNodeId"),
                    sourceName = json.getString("sourceName"),
                    destinationNodeId = json.getString("destinationNodeId"),
                    encryptedPayload = json.getString("encryptedPayload"),
                    signature = json.getString("signature"),
                    ttl = json.optInt("ttl", 5),
                    hopCount = json.optInt("hopCount", 0),
                    timestamp = json.optLong("timestamp", System.currentTimeMillis()),
                    isSos = json.optBoolean("isSos", false),
                    gpsLat = if (json.has("gpsLat")) json.getDouble("gpsLat") else null,
                    gpsLon = if (json.has("gpsLon")) json.getDouble("gpsLon") else null,
                    fileName = if (json.has("fileName")) json.getString("fileName") else null,
                    fileSize = if (json.has("fileSize")) json.getLong("fileSize") else null
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}
