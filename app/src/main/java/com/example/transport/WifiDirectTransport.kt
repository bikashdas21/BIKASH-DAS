package com.example.transport

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.NetworkInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Looper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket

class WifiDirectTransport(
    private val context: Context,
    private val scope: CoroutineScope
) : TransportInterface {

    override val transportName: String = "Wi-Fi Direct"

    private val p2pManager: WifiP2pManager? =
        context.getSystemService(Context.WIFI_P2P_SERVICE) as? WifiP2pManager
    private var channel: WifiP2pManager.Channel? = null

    private val _isScanning = MutableStateFlow(false)
    override val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _discoveredPeers = MutableStateFlow<List<DiscoveredPeer>>(emptyList())
    override val discoveredPeers: StateFlow<List<DiscoveredPeer>> = _discoveredPeers.asStateFlow()

    override var isAvailable: Boolean = p2pManager != null

    private var packetReceiverCallback: ((MeshPacket) -> Unit)? = null
    private var serverSocket: ServerSocket? = null
    private var isServerRunning = false
    private var groupOwnerAddress: String? = null
    private var isGroupOwner = false

    companion object {
        const val P2P_PORT = 8988
    }

    private val p2pReceiver = object : BroadcastReceiver() {
        override fun onReceive(c: Context, intent: Intent) {
            when (intent.action) {
                WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                    val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                    isAvailable = (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED)
                }
                WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                    requestPeers()
                }
                WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                    val networkInfo = intent.getParcelableExtra<NetworkInfo>(WifiP2pManager.EXTRA_NETWORK_INFO)
                    if (networkInfo?.isConnected == true) {
                        requestConnectionInfo()
                    }
                }
            }
        }
    }

    override fun start(onPacketReceived: (MeshPacket) -> Unit) {
        packetReceiverCallback = onPacketReceived
        if (p2pManager != null) {
            try {
                channel = p2pManager.initialize(context, Looper.getMainLooper(), null)
                val filter = IntentFilter().apply {
                    addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
                    addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
                    addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
                    addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
                }
                context.registerReceiver(p2pReceiver, filter)
            } catch (t: Throwable) {
                t.printStackTrace()
                channel = null
            }
        }
        startSocketServer()
    }

    override fun stop() {
        try {
            context.unregisterReceiver(p2pReceiver)
        } catch (e: Exception) {
            // ignore
        }
        isServerRunning = false
        try {
            serverSocket?.close()
        } catch (e: Exception) {
            // ignore
        }
        _isScanning.value = false
    }

    @SuppressLint("MissingPermission")
    override fun scanPeers() {
        val mgr = p2pManager
        val ch = channel
        if (mgr == null || ch == null) {
            _isScanning.value = false
            return
        }

        _isScanning.value = true
        try {
            mgr.discoverPeers(ch, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    requestPeers()
                }

                override fun onFailure(reasonCode: Int) {
                    _isScanning.value = false
                }
            })
        } catch (t: Throwable) {
            _isScanning.value = false
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestPeers() {
        val mgr = p2pManager ?: return
        val ch = channel ?: return
        try {
            mgr.requestPeers(ch) { peers: WifiP2pDeviceList ->
                val list = peers.deviceList.map { device ->
                    DiscoveredPeer(
                        deviceId = device.deviceAddress,
                        name = if (device.deviceName.isNullOrBlank()) "Android Peer" else device.deviceName,
                        transportType = "Wi-Fi Direct",
                        signalDbm = -55,
                        distanceMeters = 8.5,
                        isConnected = (device.status == WifiP2pDevice.CONNECTED)
                    )
                }
                _discoveredPeers.value = list
                _isScanning.value = false
            }
        } catch (t: Throwable) {
            _isScanning.value = false
        }
    }

    private fun requestConnectionInfo() {
        val mgr = p2pManager ?: return
        val ch = channel ?: return
        try {
            mgr.requestConnectionInfo(ch) { info: WifiP2pInfo? ->
                if (info != null && info.groupFormed) {
                    isGroupOwner = info.isGroupOwner
                    groupOwnerAddress = info.groupOwnerAddress?.hostAddress
                }
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    @SuppressLint("MissingPermission")
    override suspend fun connect(peerId: String): Boolean = withContext(Dispatchers.IO) {
        val mgr = p2pManager ?: return@withContext false
        val ch = channel ?: return@withContext false
        val config = WifiP2pConfig().apply {
            deviceAddress = peerId
        }
        var success = false
        try {
            mgr.connect(ch, config, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    success = true
                }

                override fun onFailure(reason: Int) {
                    success = false
                }
            })
        } catch (t: Throwable) {
            success = false
        }
        success
    }

    override suspend fun disconnect(peerId: String) = withContext(Dispatchers.IO) {
        val mgr = p2pManager ?: return@withContext
        val ch = channel ?: return@withContext
        try {
            mgr.cancelConnect(ch, null)
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    private fun startSocketServer() {
        if (isServerRunning) return
        isServerRunning = true
        scope.launch(Dispatchers.IO) {
            try {
                serverSocket = ServerSocket(P2P_PORT)
                while (isServerRunning) {
                    val clientSocket = serverSocket?.accept() ?: break
                    launch(Dispatchers.IO) {
                        handleClient(clientSocket)
                    }
                }
            } catch (e: Exception) {
                // Socket closed or failed
            }
        }
    }

    private fun handleClient(socket: Socket) {
        try {
            BufferedReader(InputStreamReader(socket.getInputStream())).use { reader ->
                val jsonLine = reader.readLine()
                if (!jsonLine.isNullOrBlank()) {
                    val packet = MeshPacket.fromJson(jsonLine)
                    if (packet != null) {
                        packetReceiverCallback?.invoke(packet)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                socket.close()
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    override suspend fun sendPacket(packet: MeshPacket): Boolean = withContext(Dispatchers.IO) {
        val targetIp = groupOwnerAddress ?: "127.0.0.1"
        try {
            val socket = Socket()
            socket.connect(InetSocketAddress(targetIp, P2P_PORT), 3000)
            OutputStreamWriter(socket.getOutputStream()).use { writer ->
                writer.write(packet.toJson() + "\n")
                writer.flush()
            }
            socket.close()
            true
        } catch (e: Exception) {
            false
        }
    }
}
