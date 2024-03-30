package com.orels.features.customer_status.data.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.view.View
import android.view.WindowManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.orels.features.R
import com.orels.features.customer_status.presentation.CustomerStateActivity

class CustomerStatusService : Service() {

    private lateinit var windowManager: WindowManager
    private var overlayView: View? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
//
//        // Initialize your ComposeView
//        val composeView = ComposeView(this).apply {
//            // Set content for your Compose view here
//            setContent {
//                OverlayUI()
//            }
//        }
//
//        // Ensure the ComposeView is laid out properly
//        val params = FrameLayout.LayoutParams(
//            FrameLayout.LayoutParams.WRAP_CONTENT,
//            FrameLayout.LayoutParams.WRAP_CONTENT
//        ).apply {
//            width = 300
//            height = 300
//        }
//        composeView.layoutParams = params
//
//        // Define the WindowManager layout params
//        val windowParams = WindowManager.LayoutParams(
//            WindowManager.LayoutParams.WRAP_CONTENT,
//            WindowManager.LayoutParams.WRAP_CONTENT,
//            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
//            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//            PixelFormat.TRANSLUCENT
//        ).apply {
//            // Position or other properties
//        }
//
//        // Add the ComposeView to the WindowManager
//        windowManager.addView(composeView, windowParams)
//        overlayView = composeView

        val notificationChannelId = "YOUR_CHANNEL_ID"
        createNotificationChannel(notificationChannelId)

        val notification = Notification.Builder(this, notificationChannelId)
            .setContentTitle("Service Running")
            .setContentText("Doing something important.")
            .setSmallIcon(R.drawable.ic_baseline_remove_circle_24) // Replace with your icon
            .build()
        startCustomerStateActivity()
    }

    private fun startCustomerStateActivity() {
        val intent = Intent(this, CustomerStateActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun createNotificationChannel(channelId: String) {
        val serviceChannel = NotificationChannel(
            channelId,
            "Foreground Service Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(serviceChannel)
    }

    override fun onDestroy() {
        super.onDestroy()
        overlayView?.let { windowManager.removeViewImmediate(it) }
        overlayView = null
    }

    @Composable
    fun OverlayUI() {
        Box(modifier = Modifier.size(300.dp)) {
            Text(text = "Hello from Overlay")
        }
    }
}