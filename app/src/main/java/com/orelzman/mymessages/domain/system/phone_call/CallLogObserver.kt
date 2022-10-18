package com.orelzman.mymessages.domain.system.phone_call

import android.database.ContentObserver
import android.os.Handler
import com.orelzman.mymessages.domain.interactors.CallLogInteractor
import com.orelzman.mymessages.domain.interactors.PhoneCallsInteractor
import com.orelzman.mymessages.domain.util.extension.Logger
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
            val nowMillis = Date().time
            phoneCall.actualDuration = nowMillis - phoneCall.startDate.time
            Logger.v("Change in call log $phoneCall")
            phoneCallsInteractor.updateCall(phoneCall = phoneCall)
        }
    }
}