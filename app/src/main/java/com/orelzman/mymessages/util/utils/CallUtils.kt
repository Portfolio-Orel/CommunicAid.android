package com.orelzman.mymessages.util

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.CallLog
import android.provider.ContactsContract
import com.orelzman.mymessages.domain.model.entities.CallLogEntity
import com.orelzman.mymessages.domain.model.entities.PhoneCall
import com.orelzman.mymessages.util.extension.compareToBallPark
import com.orelzman.mymessages.util.extension.inSeconds
import com.orelzman.mymessages.util.extension.log
import com.orelzman.mymessages.util.extension.toDate
import com.orelzman.mymessages.util.utils.DateUtils
import kotlinx.coroutines.delay
import java.util.*

/*Consider moving to interactors (because of context injection)*/
object CallUtils {

    fun getTodaysCallLog(context: Context): ArrayList<CallLogEntity> =
        getCallLogsByDate(
            context = context,
            startDate = DateUtils.getStartOfDay(),
            endDate = Date()
        )

    /**
     * Returns all the logs of calls that occurred between [startDate] and [endDate].
     * @param context is the context of the activity/fragment
     * @param startDate is the date of the first call we're looking for.
     * @param endDate is the date of the last call we're looking for.
     * @author Orel Zilberman
     */
    fun getCallLogsByDate(
        context: Context,
        startDate: Date = Date(),
        endDate: Date = Date()
    ): ArrayList<CallLogEntity> {
        val callLogEntities = ArrayList<CallLogEntity>()
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
                CallLog.Calls._ID + " DESC"
            )
            ?.use {
                while (it.moveToNext()) {
                    val number = it.getString(0) ?: ""
                    val type = it.getString(1) ?: ""
                    val duration = it.getString(2) ?: ""
                    val name = it.getString(3) ?: ""
                    val date = it.getString(4) ?: ""
                    val callLogType: Int = type.toInt()
                    val callLogEntity = CallLogEntity(
                        number = number,
                        duration = duration.toLong(),
                        name = name,
                        time = date.toLong(),
                        callLogType = CallType.fromInt(callLogType)
                    )
                    if (callLogEntity.time.toDate() < startDate) {
                        break
                    } else if (callLogEntity.time.toDate() < endDate) {
                        callLogEntities.add(callLogEntity)
                    }
                }
            }
        return callLogEntities
    }

    suspend fun getLastCallLog(context: Context?, withDelay: Long = 0): CallLogEntity? {
        delay(withDelay)
        var callLog: CallLogEntity? = null
        val details = arrayOf(
            CallLog.Calls.NUMBER,
            CallLog.Calls.TYPE,
            CallLog.Calls.DURATION,
            CallLog.Calls.CACHED_NAME,
            CallLog.Calls.DATE
        )
        context?.contentResolver
            ?.query(
                CallLog.Calls.CONTENT_URI,
                details,
                null,
                null,
                CallLog.Calls._ID + " DESC"
            )
            ?.use {
                if (it.moveToNext()) {
                    val number = it.getString(0) ?: ""
                    val type = it.getString(1) ?: ""
                    val duration = it.getString(2) ?: ""
                    val name = it.getString(3) ?: ""
                    val date = it.getString(4) ?: ""
                    val callLogType: Int = type.toInt()
                    callLog = CallLogEntity(
                        number = number,
                        duration = duration.toLong(),
                        name = name,
                        time = date.toLong(),
                        callLogType = CallType.fromInt(callLogType)
                    )
                }
            }
        return callLog
    }

    fun getContactName(number: String, context: Context): String {
        val uri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(number)
        )

        val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)

        var contactName = number
        try {
            val cursor: Cursor? = context.contentResolver.query(uri, projection, null, null, null)

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    contactName = cursor.getString(0)
                }
                cursor.close()
            }
            return contactName
        } catch(exception: IllegalArgumentException) {
            // Number not found
            return number
        } catch (exception: Exception) {
            exception.log()
            return number
        }
    }

    /**
     * Updates values according to the call log
     * *** Test call in background, removed and called again to see if the backlog catches both from the calllog
     * This has to go to the service because the log is added async.
     */
    fun update(context: Context, phoneCall: PhoneCall): PhoneCall? {
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
                "${CallLog.Calls.DATE} DESC"
            )
            ?.use {
                while (it.moveToNext()) {
                    val logStartDate = it.getString(4).toLong().toDate()
                    if (
                        phoneCall.number != it.getString(0)
                        || !logStartDate.compareToBallPark(phoneCall.startDate)
                    ) continue
                    val type = it.getString(1)
                    val duration = it.getString(2).toLong()
                    phoneCall.name = it.getString(3) ?: ""
                    phoneCall.startDate = logStartDate
                    phoneCall.endDate = (phoneCall.startDate.time.inSeconds + duration).toDate()
                    when (type.toInt()) {
                        CallLog.Calls.MISSED_TYPE -> phoneCall.missed()
                        CallLog.Calls.REJECTED_TYPE -> phoneCall.rejected()
                    }
                    return phoneCall
                }
            }
        return null
    }

}

enum class CallType(val value: Int) {
    MISSED(CallLog.Calls.MISSED_TYPE),
    INCOMING(CallLog.Calls.INCOMING_TYPE),
    OUTGOING(CallLog.Calls.OUTGOING_TYPE),
    REJECTED(CallLog.Calls.REJECTED_TYPE),
    BLOCK(CallLog.Calls.BLOCKED_TYPE);

    companion object {
        fun fromInt(value: Int): CallType = values().first { it.value == value }
    }
}