package com.orels.features.customer_status.data.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.orels.features.customer_status.domain.repository.CustomerStatusRepository
import com.orels.features.customer_status.presentation.CustomerStateActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CustomerStatusService : Service() {

    @Inject
    lateinit var customerStatusRepository: CustomerStatusRepository

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        isServiceRunning = true
        startCustomerStateActivity()
    }

    private fun startCustomerStateActivity() {
        val intent = Intent(this, CustomerStateActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        isServiceRunning = false
    }

    companion object {
        var isServiceRunning = false
    }
}