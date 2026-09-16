package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.core.crypto.CryptoEngine
import com.example.transport.MeshPacket
import com.example.transport.PacketType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Zero-Grid", appName)
    }

    @Test
    fun `mesh packet serialization and deserialization roundtrip`() {
        val packet = MeshPacket(
            packetId = "TEST-PACKET-101",
            type = PacketType.TEXT_MESSAGE,
            sourceNodeId = "NODE-A",
            sourceName = "Node Alpha",
            destinationNodeId = "BROADCAST",
            encryptedPayload = "ENC_PAYLOAD_ABC123",
            signature = "SIG_XYZ789",
            ttl = 5,
            hopCount = 0,
            isSos = false,
            gpsLat = 23.8103,
            gpsLon = 90.4125
        )

        val json = packet.toJson()
        val deserialized = MeshPacket.fromJson(json)

        assertNotNull(deserialized)
        assertEquals(packet.packetId, deserialized?.packetId)
        assertEquals(packet.sourceNodeId, deserialized?.sourceNodeId)
        assertEquals(packet.destinationNodeId, deserialized?.destinationNodeId)
        assertEquals(packet.encryptedPayload, deserialized?.encryptedPayload)
        assertEquals(23.8103, deserialized?.gpsLat ?: 0.0, 0.0001)
    }

    @Test
    fun `crypto password hashing and checksum generation`() {
        val checksum1 = CryptoEngine.computeChecksum("Zero-Grid Test Data".toByteArray())
        val checksum2 = CryptoEngine.computeChecksum("Zero-Grid Test Data".toByteArray())
        assertEquals(checksum1, checksum2)
        assertTrue(checksum1.isNotEmpty())
    }
}
