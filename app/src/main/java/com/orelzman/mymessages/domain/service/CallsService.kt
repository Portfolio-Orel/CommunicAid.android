package com.orelzman.mymessages.domain.service

import android.annotation.SuppressLint
import android.app.*
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.provider.CallLog
import androidx.annotation.RequiresApi
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.orelzman.auth.domain.interactor.AuthInteractor
import com.orelzman.mymessages.MainActivity
import com.orelzman.mymessages.R
import com.orelzman.mymessages.data.dto.PhoneCall
import com.orelzman.mymessages.data.local.interactors.phoneCall.PhoneCallsInteractor
import com.orelzman.mymessages.domain.service.phone_call.PhoneCallManagerInteractor
import com.orelzman.mymessages.util.extension.Log
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.math.absoluteValue

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
    lateinit var phoneCallManagerInteractor: PhoneCallManagerInteractor

    @Inject
    lateinit var phoenCallsInteractor: PhoneCallsInteractor

    @Inject
    lateinit var authInteractor: AuthInteractor

    override fun onBind(p0: Intent?): IBinder? =
        null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        currentState = intent?.extras?.get(INTENT_STATE_VALUE) as ServiceState
        Log.vCustom("Service onStartCommand: $currentState")
        if (currentState == ServiceState.UPLOAD_LOGS) {
            uploadCalls()
        }
        return START_NOT_STICKY
    }


    override fun onCreate() {
        Log.vCustom("Service onCreate")
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
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PendingIntent.getActivity(this, 0, notificationIntent, FLAG_IMMUTABLE)
                } else {
                    PendingIntent.getActivity(this, 0, notificationIntent, 0)
                }
            }

        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, "MyMessages")
        } else {
            Notification.Builder(this)
                .setContentTitle("MyMessages")
        }
        return builder
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_launcher_background)
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
        CoroutineScope(Dispatchers.IO).launch {
            val callsLog = phoenCallsInteractor
                .getAll()
                .map { it.update(this@CallsService) }
            Log.vCustom("upload calls: $callsLog")
            authInteractor.getUser()?.userId?.let {
                phoenCallsInteractor.addPhoneCalls(
                    it,
                    callsLog
                )
            }
        }.invokeOnCompletion {
            Log.vCustom("About to finish service")
            stopService()
            Log.vCustom("Service stopped.")
        }
    }
}
/**
 * Updates values according to the call log
 * *** Test call in background, removed and called again to see if the backlog catches both from the calllog
 * This has to go to the service because the log is added async.
 */
private fun PhoneCall.update(context: Context): PhoneCall {
    val details = arrayOf(
        CallLog.Calls.NUMBER,
        CallLog.Calls.TYPE,
        CallLog.Calls.DURATION,
        CallLog.Calls.CACHED_NAME,
        CallLog.Calls.DATE
    )
    context.contentResolver
        .query(
            CallLog.Calls.CONTENT_URI,
            details,
            null,
            null,
            CallLog.Calls.DATE + " DESC"
        )
        ?.use {
            while (it.moveToNext()) {
                val logStartDate = it.getString(4).toLong().date
                if (
                    number != it.getString(0)
                    || logStartDate.notEquals(startDate)
                ) continue
                val type = it.getString(1)
                val duration = it.getString(2).toLong()
                name = it.getString(3) ?: ""
                startDate = logStartDate
                endDate = (startDate.time.inSeconds + duration).date
                when (type.toInt()) {
                    CallLog.Calls.MISSED_TYPE -> missed()
                    CallLog.Calls.REJECTED_TYPE -> rejected()
                }
                return@use
            }
        }
    return this
}

val Long.inSeconds: Long
    get() =
        if ("$this".length > 10) {
            this / 1000
        } else {
            this
        }

val Long.date: Date
    get() =
        if ("$this".length > 10) {
            Date(this)
        } else {
            Date(this * 1000)
        }

fun Date.notEquals(date: Date, maxDifferenceInSeconds: Long = 5): Boolean =
    (time.inSeconds - date.time.inSeconds).absoluteValue >= maxDifferenceInSeconds


enum class ServiceState(val value: Int) {
    UPLOAD_LOGS(0),
    ALIVE(1),
    NOT_ALIVE(2)
}