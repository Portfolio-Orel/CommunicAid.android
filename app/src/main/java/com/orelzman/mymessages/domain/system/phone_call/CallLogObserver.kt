package com.orelzman.mymessages.domain.system.phone_call

import android.database.ContentObserver
import android.os.Handler
import com.orelzman.mymessages.domain.interactors.CallLogInteractor
import com.orelzman.mymessages.domain.interactors.PhoneCallsInteractor
import java.util.*
import javax.inject.Inject

/**
 * @author Orel Zilberman
 * 16/10/2022
 */

class CallLogObserver @Inject constructor(handler: Handler?) : ContentObserver(handler) {

    @Inject
    lateinit var callLogInteractor: CallLogInteractor

    @Inject
    lateinit var phoneCallsInteractor: PhoneCallsInteractor

    override fun deliverSelfNotifications(): Boolean {
        return true
    }

    override fun onChange(selfChange: Boolean) {
        callLogInteractor.getLastCallLog()?.phoneCall?.let { phoneCall ->
            val nowMillis = Date().time.toFloat()
            phoneCall.actualDuration = nowMillis - phoneCall.startDate.time
            phoneCallsInteractor.updateCall(phoneCall = phoneCall)
        }
    }
}