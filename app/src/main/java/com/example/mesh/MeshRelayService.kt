package com.example.mesh

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.ZeroGridApplication

class MeshRelayService : Service() {

    companion object {
        const val CHANNEL_ID = "zerogrid_mesh_channel"
        const val NOTIF_ID = 1001

        fun startService(context: Context) {
            val intent = Intent(context, MeshRelayService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, MeshRelayService::class.java)
            context.stopService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val notification = buildForegroundNotification()
        startForeground(NOTIF_ID, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Zero-Grid Mesh Relay",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Keeps the decentralized offline mesh network active in the background"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun buildForegroundNotification(): Notification {
        val app = applicationContext as? ZeroGridApplication
        val lang = app?.preferencesManager?.language?.value ?: "en"
        val title = if (lang == "bn") "ZERO-GRID মেশ সক্রিয় 📡" else "ZERO-GRID Mesh Active 📡"
        val desc = if (lang == "bn") "অফলাইন নোড রিলে ও মেসেজ প্যাকেট রাউটিং চলছে" else "Decentralized mesh relay & peer routing operational"
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(desc)
            .setSmallIcon(android.R.drawable.stat_notify_sync)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
}
