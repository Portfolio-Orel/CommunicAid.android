package com.orelzman.mymessages.domain.service

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
import com.orelzman.mymessages.data.local.interactors.phoneCall.PhoneCallStatisticsInteractor
import com.orelzman.mymessages.domain.service.PhoneCall.PhoneCallInteractor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.math.absoluteValue

@AndroidEntryPoint
@ExperimentalPermissionsApi
class CallsService : Service() {

    private var wakeLock: PowerManager.WakeLock? = null
    private lateinit var currentNotification: Notification
    private val notificationID = 1
    private val notificationChannelId = "MyMessages"
    private val scope = CoroutineScope(Job() + Dispatchers.Main)

    @Inject
    lateinit var phoneCallInteractor: PhoneCallInteractor

    @Inject
    lateinit var phoneCallStatisticsInteractor: PhoneCallStatisticsInteractor

    @Inject
    lateinit var authInteractor: AuthInteractor

    override fun onBind(p0: Intent?): IBinder? =
        null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            when (intent.action) {
                Actions.START.name -> {
                    startService()
                }
                Actions.STOP.name -> stopService()
            }
        }
        // by returning this we make sure the service is restarted if the system kills the service
        return START_STICKY
    }


    override fun onCreate() {
        super.onCreate()
        currentNotification = createNotification()
        startForeground(notificationID, currentNotification)
        uploadCalls()
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

    private fun createNotification(): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationAfter26()
        }
        val closeIntent = Intent(this, CallsService::class.java)
        closeIntent.action = Actions.STOP.toString()
        val closePendingIntent =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.getService(this, 0, closeIntent, FLAG_IMMUTABLE)
            } else {
                PendingIntent.getService(this, 0, closeIntent, 0)
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
            .addAction(
                android.R.drawable.btn_plus,
                ("עצור" as CharSequence),
                closePendingIntent
            )
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
        val callsLog = phoneCallStatisticsInteractor
            .getAll()
            .map { it.phoneCall.update(this) }
        scope.launch {
            authInteractor.user?.uid?.let {
                phoneCallStatisticsInteractor.addPhoneCalls(
                    it,
                    callsLog
                )
            }
        }
    }
}

enum class Actions {
    START,
    STOP
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
