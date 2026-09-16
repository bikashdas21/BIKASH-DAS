package com.example.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

enum class LocationShareMode {
    OFF,
    SHARE_ONCE,
    SHARE_15_MINUTES
}

data class OfflineLocation(
    val latitude: Double,
    val longitude: Double,
    val accuracyMeters: Float = 5.0f,
    val timestamp: Long = System.currentTimeMillis()
)

class OfflineLocationManager(private val context: Context) {
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager

    private val _currentLocation = MutableStateFlow<OfflineLocation?>(null)
    val currentLocation: StateFlow<OfflineLocation?> = _currentLocation.asStateFlow()

    private val _shareMode = MutableStateFlow(LocationShareMode.OFF)
    val shareMode: StateFlow<LocationShareMode> = _shareMode.asStateFlow()

    private var shareExpirationTimestamp: Long = 0L

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            _currentLocation.value = OfflineLocation(
                latitude = location.latitude,
                longitude = location.longitude,
                accuracyMeters = location.accuracy,
                timestamp = location.time
            )

            if (_shareMode.value == LocationShareMode.SHARE_ONCE) {
                stopLocationUpdates()
                _shareMode.value = LocationShareMode.OFF
            } else if (_shareMode.value == LocationShareMode.SHARE_15_MINUTES) {
                if (System.currentTimeMillis() > shareExpirationTimestamp) {
                    stopLocationUpdates()
                    _shareMode.value = LocationShareMode.OFF
                }
            }
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    @SuppressLint("MissingPermission")
    fun requestLocation(mode: LocationShareMode) {
        _shareMode.value = mode
        if (mode == LocationShareMode.OFF) {
            stopLocationUpdates()
            return
        }

        if (mode == LocationShareMode.SHARE_15_MINUTES) {
            shareExpirationTimestamp = System.currentTimeMillis() + (15 * 60 * 1000)
        }

        try {
            // Get last known location immediately from GPS or Network provider (hardware cache)
            val gpsLoc = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            val netLoc = locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            val best = gpsLoc ?: netLoc

            if (best != null) {
                _currentLocation.value = OfflineLocation(
                    latitude = best.latitude,
                    longitude = best.longitude,
                    accuracyMeters = best.accuracy,
                    timestamp = best.time
                )
                if (mode == LocationShareMode.SHARE_ONCE) {
                    return
                }
            }

            // Register passive/GPS updates without internet
            locationManager?.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000L,
                5.0f,
                locationListener
            )
        } catch (e: SecurityException) {
            // Permission denied handled gracefully
        }
    }

    fun stopLocationUpdates() {
        try {
            locationManager?.removeUpdates(locationListener)
        } catch (e: Exception) {
            // ignore
        }
        _shareMode.value = LocationShareMode.OFF
    }

    companion object {
        fun formatCoordinates(lat: Double, lon: Double): String {
            val latDir = if (lat >= 0) "N" else "S"
            val lonDir = if (lon >= 0) "E" else "W"
            return "%.4f°%s, %.4f°%s".format(Math.abs(lat), latDir, Math.abs(lon), lonDir)
        }

        fun calculateDistanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
            val r = 6371000.0 // Earth radius in meters
            val dLat = Math.toRadians(lat2 - lat1)
            val dLon = Math.toRadians(lon2 - lon1)
            val a = sin(dLat / 2) * sin(dLat / 2) +
                    cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                    sin(dLon / 2) * sin(dLon / 2)
            val c = 2 * atan2(sqrt(a), sqrt(1 - a))
            return r * c
        }
    }
}
