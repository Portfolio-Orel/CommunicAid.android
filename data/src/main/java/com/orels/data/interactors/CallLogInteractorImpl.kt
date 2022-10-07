package com.orels.data.interactors

import android.content.Context
import android.provider.CallLog
import com.orels.domain.interactors.CallLogInteractor
import com.orels.domain.interactors.CallType
import com.orels.domain.model.entities.CallLogEntity
import com.orels.domain.model.entities.PhoneCall
import com.orels.domain.util.common.DateUtils
import com.orels.domain.util.extension.compareToBallPark
import com.orels.domain.util.extension.inSeconds
import com.orels.domain.util.extension.toDate
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import java.util.*
import javax.inject.Inject

class CallLogInteractorImpl @Inject constructor(@ApplicationContext private val context: Context) :
    CallLogInteractor {

    override fun getTodaysCallLog(): ArrayList<CallLogEntity> =
        getCallLogsByDate(
            startDate = DateUtils.getStartOfDay(),
            endDate = Date()
        )

    /**
     * Returns all the logs of calls that occurred between [startDate] and [endDate].
     * @param startDate is the date of the first call we're looking for.
     * @param endDate is the date of the last call we're looking for.
     * @author Orel Zilberman
     */
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

    override suspend fun getLastCallLog(delay: Long): CallLogEntity? {
        delay(delay)
        var callLog: CallLogEntity? = null
        val details = arrayOf(
            CallLog.Calls.NUMBER,
            CallLog.Calls.TYPE,
            CallLog.Calls.DURATION,
            CallLog.Calls.CACHED_NAME,
            CallLog.Calls.DATE
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
