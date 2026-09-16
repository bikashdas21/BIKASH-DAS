package com.example.gateway

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class NetworkLayer {
    LOCAL_OFFLINE_MESH,
    INTERNET_GATEWAY_CONNECTED,
    GLOBAL_INTERNET_BACKHAUL
}

data class GatewayInfo(
    val gatewayNodeId: String,
    val gatewayName: String,
    val countryCode: String,
    val uplinkType: String, // e.g. "Satellite Backhaul (Starlink)", "Fiber WAN", "Cellular 4G/5G"
    val isReachable: Boolean,
    val latencyMs: Long,
    val bandwidthMbps: Double
)

class GatewayManager {
    private val _currentLayer = MutableStateFlow(NetworkLayer.LOCAL_OFFLINE_MESH)
    val currentLayer: StateFlow<NetworkLayer> = _currentLayer.asStateFlow()

    private val _activeGateway = MutableStateFlow<GatewayInfo?>(null)
    val activeGateway: StateFlow<GatewayInfo?> = _activeGateway.asStateFlow()

    fun connectToGateway(gateway: GatewayInfo) {
        _activeGateway.value = gateway
        _currentLayer.value = NetworkLayer.INTERNET_GATEWAY_CONNECTED
    }

    fun disconnectGateway() {
        _activeGateway.value = null
        _currentLayer.value = NetworkLayer.LOCAL_OFFLINE_MESH
    }

    companion object {
        const val TRUTH_DISCLOSURE = "TECHNICAL TRUTH: Zero-Grid operates as a peer-to-peer decentralized mesh. When completely offline, packets remain within local radio hops. Real Internet access is only possible when communicating through a verified Gateway node equipped with physical backhaul."
    }
}
