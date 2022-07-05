package com.orelzman.mymessages.util

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.CallLog
import android.provider.ContactsContract
import com.orelzman.mymessages.domain.model.entities.CallLogEntity
import com.orelzman.mymessages.util.extension.toDate
import java.util.*

/*Consider moving to interactors (because of context injection)*/
object CallUtils {

    fun getTodaysCallLog(context: Context): ArrayList<CallLogEntity> =
        getCallLogsByDate(
            context = context,
            startDate = Date().startOfDay,
            endDate = Date()
        )

    /**
     * Returns all the logs of calls that occurred between [startDate] and [endDate].
     * @param context is the context of the activity/fragment
     * @param startDate is the date of the first call we're looking for.
     * @param endDate is the date of the last call we're looking for.
     * @author Orel Zilberman
     */
    private fun getCallLogsByDate(
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

    fun getContactName(number: String, context: Context): String {
        val uri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(number)
        )

        val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)

        var contactName = number
        val cursor: Cursor? = context.contentResolver.query(uri, projection, null, null, null)

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                contactName = cursor.getString(0)
            }
            cursor.close()
        }
        return contactName
    }


}

enum class CallType(val value: Int, name: String) {
    MISSED(CallLog.Calls.MISSED_TYPE, "missed"),
    INCOMING(CallLog.Calls.INCOMING_TYPE, "incoming"),
    OUTGOING(CallLog.Calls.OUTGOING_TYPE, "outgoing"),
    REJECTED(CallLog.Calls.REJECTED_TYPE, "rejected"),
    BLOCK(CallLog.Calls.BLOCKED_TYPE, "block");

    companion object {
        fun fromInt(value: Int): CallType = values().first { it.value == value }
        fun fromString(value: String): CallType = values().first { it.name == value }
    }
}

val Date.startOfDay: Date
    get() {
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val day = calendar[Calendar.DATE]
        calendar[year, month, day, 0, 0] = 0
        return calendar.time
    }