package com.orels.data.interactor

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.CallLog
import android.provider.ContactsContract
import com.orels.domain.interactors.CallDetailsInteractor
import com.orels.domain.interactors.CallType
import com.orels.domain.model.entities.CallLogEntity
import com.orels.domain.model.entities.PhoneCall
import com.orels.domain.util.common.DateUtils
import com.orels.domain.util.extension.compareToBallPark
import com.orels.domain.util.extension.epochTimeInSeconds
import com.orels.domain.util.extension.log
import com.orels.domain.util.extension.toDate
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import java.util.Date
import javax.inject.Inject

class CallDetailsInteractorImpl @Inject constructor(@ApplicationContext private val context: Context) :
    CallDetailsInteractor {

    override fun getTodaysCallLog(): ArrayList<CallLogEntity> =
        getCallLogsByDate(
            startDate = DateUtils.getStartOfDay(),
            endDate = Date()
        )

    override fun getCallLogsByDate(
        startDate: Date,
        endDate: Date
    ): ArrayList<CallLogEntity> {
        val callLogEntities = ArrayList<CallLogEntity>()
        val details = arrayOf(
            CallLog.Calls.NUMBER,
            CallLog.Calls.TYPE,
            CallLog.Calls.DURATION,
            CallLog.Calls.CACHED_NAME,
            CallLog.Calls.DATE,
            CallLog.Calls.CACHED_PHOTO_URI
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
                    val photoUri = it.getString(5) ?: ""
                    val callLogType: Int = type.toInt()
                    val callLogEntity = CallLogEntity(
                        number = number,
                        duration = duration.toLong(),
                        name = name,
                        time = date.toLong(),
                        callLogType = CallType.fromInt(callLogType),
                        photoUri = photoUri
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

    override suspend fun getLastCallLog(delay: Long): CallLogEntity? {
        delay(delay)
        var callLog: CallLogEntity? = null
        val details = arrayOf(
            CallLog.Calls.NUMBER,
            CallLog.Calls.TYPE,
            CallLog.Calls.DURATION,
            CallLog.Calls.CACHED_NAME,
            CallLog.Calls.DATE,
            CallLog.Calls.CACHED_PHOTO_URI
        )
        context.contentResolver
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
                    val photoUri = it.getString(5) ?: ""
                    val callLogType: Int = type.toInt()
                    callLog = CallLogEntity(
                        number = number,
                        duration = duration.toLong(),
                        name = name,
                        time = date.toLong(),
                        callLogType = CallType.fromInt(callLogType),
                        photoUri = photoUri
                    )
                }
            }
        return callLog
    }

    override fun getLastCallLog(): CallLogEntity? {
        var callLog: CallLogEntity? = null
        val details = arrayOf(
            CallLog.Calls.NUMBER,
            CallLog.Calls.TYPE,
            CallLog.Calls.DURATION,
            CallLog.Calls.CACHED_NAME,
            CallLog.Calls.DATE,
            CallLog.Calls.CACHED_PHOTO_URI
        )
        context.contentResolver
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
                    val photoUri = it.getString(5) ?: ""
                    val callLogType: Int = type.toInt()
                    callLog = CallLogEntity(
                        number = number,
                        duration = duration.toLong(),
                        name = name,
                        time = date.toLong(),
                        callLogType = CallType.fromInt(callLogType),
                        photoUri = photoUri
                    )
                }
            }
        return callLog
    }

    /**
     * Updates values according to the call log
     */
    override fun update(phoneCall: PhoneCall): PhoneCall? {
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
                    phoneCall.endDate =
                        (phoneCall.startDate.time.epochTimeInSeconds + duration).toDate()
                    when (type.toInt()) {
                        CallLog.Calls.MISSED_TYPE -> phoneCall.missed()
                        CallLog.Calls.REJECTED_TYPE -> phoneCall.rejected()
                    }
                    return phoneCall
                }
            }
        return null
    }

    override fun getContactImage(number: String): String {
        val uri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(number)
        )

        val projection = arrayOf(ContactsContract.PhoneLookup.PHOTO_URI)

        var photoUri = ""
        val cursor: Cursor? = context.contentResolver.query(uri, projection, null, null, null)

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                photoUri = cursor.getString(0) ?: ""
            }
            cursor.close()
        }
        return photoUri
    }

    override fun getContactName(number: String): String {
        try {
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
        } catch (e: IllegalArgumentException) {
            e.log()
            return number
        } catch (e: Exception) {
            e.log()
            return number
        }
    }
}
