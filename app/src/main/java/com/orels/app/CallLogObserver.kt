package com.orels.app

import android.database.ContentObserver
import android.os.Handler
import com.orels.domain.interactors.CallLogInteractor
import com.orels.domain.interactors.PhoneCallsInteractor
import com.orels.domain.util.common.Logger
import java.util.*
import javax.inject.Inject

/**
 * @author Orel Zilberman
 * 16/10/2022
 */

class CallLogObserver @Inject constructor(
    private val callLogInteractor: CallLogInteractor,
    private val phoneCallsInteractor: PhoneCallsInteractor,
    handler: Handler?
) : ContentObserver(handler) {

    override fun deliverSelfNotifications(): Boolean {
        return true
    }

    override fun onChange(selfChange: Boolean) {
        callLogInteractor.getLastCallLog()?.phoneCall?.let { phoneCall ->
            phoneCall.actualEndDate = Date(Date().time - phoneCall.startDate.time)
            Logger.vNoRemoteLogging("Change in call log $phoneCall")
            phoneCallsInteractor.updateCall(phoneCall = phoneCall)
        }
    }
}