package com.example.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: String): User?

    @Query("SELECT * FROM users ORDER BY createdAt DESC LIMIT 1")
    fun getCurrentUserFlow(): Flow<User?>

    @Query("SELECT * FROM users ORDER BY createdAt DESC LIMIT 1")
    suspend fun getCurrentUser(): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("DELETE FROM users")
    suspend fun clearUsers()
}

@Dao
interface DeviceDao {
    @Query("SELECT * FROM devices ORDER BY lastSeen DESC")
    fun getAllDevices(): Flow<List<Device>>

    @Query("SELECT * FROM devices WHERE status = 'CONNECTED'")
    fun getConnectedDevices(): Flow<List<Device>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateDevice(device: Device)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevices(devices: List<Device>)

    @Query("UPDATE devices SET status = :status WHERE deviceId = :deviceId")
    suspend fun updateDeviceStatus(deviceId: String, status: String)

    @Query("DELETE FROM devices")
    suspend fun clearDevices()
}

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<Message>>

    @Query("SELECT * FROM messages WHERE isSos = 1 ORDER BY timestamp DESC")
    fun getSosMessages(): Flow<List<Message>>

    @Query("SELECT * FROM messages WHERE receiverId = :peerId OR senderId = :peerId ORDER BY timestamp ASC")
    fun getMessagesForPeer(peerId: String): Flow<List<Message>>

    @Query("SELECT * FROM messages WHERE receiverId = 'BROADCAST' ORDER BY timestamp ASC")
    fun getBroadcastMessages(): Flow<List<Message>>

    @Query("SELECT * FROM messages WHERE messageId = :messageId LIMIT 1")
    suspend fun getMessageById(messageId: String): Message?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: Message)

    @Update
    suspend fun updateMessage(message: Message)

    @Query("UPDATE messages SET status = :status WHERE messageId = :messageId")
    suspend fun updateMessageStatus(messageId: String, status: String)

    @Query("DELETE FROM messages")
    suspend fun clearMessages()
}

@Dao
interface TransferDao {
    @Query("SELECT * FROM transfers ORDER BY timestamp DESC")
    fun getAllTransfers(): Flow<List<Transfer>>

    @Query("SELECT * FROM transfers WHERE transferId = :id LIMIT 1")
    suspend fun getTransferById(id: String): Transfer?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransfer(transfer: Transfer)

    @Update
    suspend fun updateTransfer(transfer: Transfer)

    @Query("DELETE FROM transfers")
    suspend fun clearTransfers()
}

@Dao
interface NetworkNodeDao {
    @Query("SELECT * FROM network_nodes ORDER BY isCommunityNode DESC, hopCount ASC")
    fun getAllNodes(): Flow<List<NetworkNode>>

    @Query("SELECT * FROM network_nodes WHERE isCommunityNode = 1")
    fun getCommunityNodes(): Flow<List<NetworkNode>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNodes(nodes: List<NetworkNode>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNode(node: NetworkNode)

    @Query("DELETE FROM network_nodes")
    suspend fun clearNodes()
}
