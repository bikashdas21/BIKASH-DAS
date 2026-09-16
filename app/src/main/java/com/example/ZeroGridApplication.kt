package com.example

import android.app.Application
import com.example.audio.VoiceEngine
import com.example.core.PreferencesManager
import com.example.database.AppDatabase
import com.example.filetransfer.ChunkedFileTransferManager
import com.example.location.OfflineLocationManager
import com.example.mesh.MeshRouter
import com.example.transport.TransportManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class ZeroGridApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val database by lazy { AppDatabase.getDatabase(this) }
    val preferencesManager by lazy { PreferencesManager(this) }
    val transportManager by lazy { TransportManager(this, applicationScope) }
    val meshRouter by lazy {
        MeshRouter(database, transportManager, preferencesManager, applicationScope)
    }
    val locationManager by lazy { OfflineLocationManager(this) }
    val voiceEngine by lazy { VoiceEngine(this) }
    val fileTransferManager by lazy { ChunkedFileTransferManager(this, database.transferDao()) }
    val gatewayManager by lazy { com.example.gateway.GatewayManager() }

    override fun onCreate() {
        super.onCreate()
        // Eagerly initialize mesh router & transport
        meshRouter
    }
}
