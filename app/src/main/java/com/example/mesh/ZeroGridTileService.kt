package com.example.mesh

import android.content.ComponentName
import android.content.Context
import android.graphics.drawable.Icon
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import com.example.R
import com.example.ZeroGridApplication

/**
 * Real Android Quick Settings Tile for Zero-Grid Freedom Network.
 * Controls the active decentralized mesh networking service and transports.
 */
@RequiresApi(Build.VERSION_CODES.N)
class ZeroGridTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        updateTileState()
    }

    override fun onClick() {
        super.onClick()
        val app = applicationContext as? ZeroGridApplication ?: return
        val current = app.preferencesManager.isZeroGridActive.value
        val newState = !current
        app.preferencesManager.setZeroGridActive(newState)

        if (newState) {
            MeshRelayService.startService(this)
            app.meshRouter.activate()
        } else {
            MeshRelayService.stopService(this)
            app.meshRouter.deactivate()
        }

        updateTileState()
    }

    private fun updateTileState() {
        val tile = qsTile ?: return
        val app = applicationContext as? ZeroGridApplication
        val isActive = app?.preferencesManager?.isZeroGridActive?.value ?: false

        tile.state = if (isActive) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        tile.icon = Icon.createWithResource(this, R.drawable.ic_qs_zero_grid)
        tile.label = "ZERO-GRID 📡"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            tile.subtitle = if (isActive) "Mesh Active" else "Mesh Offline"
        }
        tile.updateTile()
    }

    companion object {
        fun requestTileUpdate(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                try {
                    requestListeningState(
                        context,
                        ComponentName(context, ZeroGridTileService::class.java)
                    )
                } catch (_: Throwable) {
                    // System UI might not have initialized QS yet
                }
            }
        }
    }
}
