package com.orelzman.mymessages.domain.service

import android.annotation.SuppressLint
import android.app.*
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.annotation.RequiresApi
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.orelzman.auth.domain.interactor.AuthInteractor
import com.orelzman.mymessages.MainActivity
import com.orelzman.mymessages.R
import com.orelzman.mymessages.domain.interactors.AnalyticsInteractor
import com.orelzman.mymessages.domain.interactors.PhoneCallsInteractor
import com.orelzman.mymessages.domain.interactors.SettingsInteractor
import com.orelzman.mymessages.domain.model.entities.*
import com.orelzman.mymessages.util.common.CallUtils
import com.orelzman.mymessages.util.common.Constants.TIME_TO_ADD_CALL_TO_CALL_LOG
import com.orelzman.mymessages.util.extension.Log
import com.orelzman.mymessages.util.extension.appendAll
import com.orelzman.mymessages.util.extension.compareToBallPark
import com.orelzman.mymessages.util.extension.log
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
@ExperimentalPermissionsApi
class CallsService : Service() {

    companion object {
        const val INTENT_STATE_VALUE = "background_service_state_value"

        var currentState: ServiceState = ServiceState.NOT_ALIVE
    }

    private var wakeLock: PowerManager.WakeLock? = null
    private lateinit var currentNotification: Notification
    private val notificationID = 1
    private val notificationChannelId = "MyMessages"

    @Inject
    lateinit var phoneCallsInteractor: PhoneCallsInteractor

    @Inject
    lateinit var authInteractor: AuthInteractor

    @Inject
    lateinit var settingsInteractor: SettingsInteractor

    @Inject
    lateinit var analyticsInteractor: AnalyticsInteractor

    override fun onBind(p0: Intent?): IBinder? =
        null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        currentState = intent?.extras?.get(INTENT_STATE_VALUE) as ServiceState
        Log.vCustom("Service onStartCommand: $currentState")
        try {
            analyticsInteractor.track("CallsService", mapOf("status" to currentState.name))
        } catch (exception: Exception) {
            exception.log(mapOf("status" to currentState.name))
        }
        startService()
        if (currentState == ServiceState.UPLOAD_LOGS) {
            uploadCalls()
        }
        return START_NOT_STICKY
    }


    override fun onCreate() {
        super.onCreate()
        currentNotification = createNotification()
        startForeground(notificationID, currentNotification)
    }

    private fun startService() {
        // we need this lock so our service gets not affected by Doze Mode
        wakeLock =
            (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyMessages::lock").apply {
                    acquire(10 * 60 * 1000L /*10 minutes*/)
                }
            }
    }

    private fun stopService() {
        try {
            wakeLock?.let {
                if (it.isHeld) {
                    it.release()
                }
            }
            stopForeground(true)
            stopSelf()
        } catch (e: Exception) {

        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    @Suppress("DEPRECATION")
    private fun createNotification(): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationAfter26()
        }
        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                notificationIntent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or FLAG_IMMUTABLE
                PendingIntent.getActivity(this, 0, notificationIntent, FLAG_IMMUTABLE)
            }

        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, "MyMessages")
        } else {
            Notification.Builder(this)
                .setContentTitle("MyMessages")
        }
        return builder
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setColor(0x7d0000)
            .setTicker("Ticker text")
            .setOnlyAlertOnce(true)
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationAfter26() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            notificationChannelId,
            "MyMessages Service notifications channel",
            NotificationManager.IMPORTANCE_HIGH
        ).let {
            it.description = "MyMessages Background Calls Service"
            it
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun uploadCalls() {
        var phoneCalls = emptyList<PhoneCall>()
        val uploadJob = CoroutineScope(Dispatchers.IO).async {
            delay(TIME_TO_ADD_CALL_TO_CALL_LOG)
            phoneCalls = phoneCallsInteractor
                .getAll()
                .appendAll(checkCallsNotRecorded())
                .distinctBy { it.startDate }
                .filter { it.uploadState == UploadState.NotUploaded }
                .mapNotNull {
                    it.uploadState = UploadState.BeingUploaded
                    phoneCallsInteractor.updateCallUploadState(
                        it,
                        UploadState.BeingUploaded
                    )
                    return@mapNotNull CallUtils.update(this@CallsService, it)
                }
            authInteractor.getUser()?.userId?.let {
                phoneCallsInteractor.createPhoneCalls(
                    it,
                    phoneCalls
                )
                phoneCalls.forEach { call ->
                    phoneCallsInteractor.updateCallUploadState(call, UploadState.Uploaded)
                    analyticsInteractor.track("Call Deleted", "call" to call.number)
                }
            }
        }
        try {
            CoroutineScope(Dispatchers.IO).launch {
                uploadJob.await()
                stopService()
            }
        } catch (e: Exception) {
            e.log(phoneCalls)
            phoneCalls.forEach {
                phoneCallsInteractor.updateCallUploadState(
                    it,
                    uploadState = UploadState.NotUploaded
                )
            }
            stopService()
        }
    }

    private suspend fun checkCallsNotRecorded(): List<PhoneCall> {
        val phoneCalls = ArrayList<PhoneCall>()
        val lastUpdateAt = settingsInteractor.getSettings(SettingsKeys.CallsUpdateAt)?.value
        val date = Date(lastUpdateAt?.toLongOrNull() ?: Date().time)
        val potentiallyMissedPhoneCalls =
            CallUtils.getCallLogsByDate(this@CallsService, startDate = date).toPhoneCalls()
        val savedPhoneCalls = phoneCallsInteractor.getAll()
        potentiallyMissedPhoneCalls.forEach { potentiallyMissedPhoneCall ->
            if (savedPhoneCalls.none {
                    it.number == potentiallyMissedPhoneCall.number
                            && it.startDate.compareToBallPark(potentiallyMissedPhoneCall.startDate)
                }) {
                phoneCalls.add(potentiallyMissedPhoneCall)
            }
        }
        phoneCallsInteractor.cachePhoneCalls(phoneCalls)
        authInteractor.getUser()?.let {
            settingsInteractor.createSettings(
                Settings(
                    key = SettingsKeys.CallsUpdateAt, value = Date().time.toString()
                ),
                userId = it.userId
            )
        }
        return phoneCalls
    }
}

enum class ServiceState(val value: Int) {
    UPLOAD_LOGS(0),
    ALIVE(1),
    NOT_ALIVE(2)
}