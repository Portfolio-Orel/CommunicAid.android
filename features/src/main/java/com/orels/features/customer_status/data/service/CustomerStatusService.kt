package com.orels.features.customer_status.data.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.view.View
import android.view.WindowManager
import com.orels.features.customer_status.domain.repostiory.CustomerStatusRepository
import com.orels.features.customer_status.presentation.CustomerStateActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CustomerStatusService : Service() {

    @Inject
    lateinit var customerStatusRepository: CustomerStatusRepository

    private lateinit var windowManager: WindowManager
    private var overlayView: View? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startCustomerStateActivity()
    }

    private fun startCustomerStateActivity() {
        val intent = Intent(this, CustomerStateActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        overlayView?.let { windowManager.removeViewImmediate(it) }
        overlayView = null
    }
}