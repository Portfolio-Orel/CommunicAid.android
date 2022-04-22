package com.orelzman.mymessages

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.orelzman.mymessages.domain.service.PhoneCall.CallState
import com.orelzman.mymessages.domain.service.PhoneCall.PhoneCallManagerImpl
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PhoneCallManagerTest {

    private val phoneCallManager = PhoneCallManagerImpl()
    private val context: Context =
        InstrumentationRegistry.getInstrumentation().context

    private fun endCall() {
        phoneCallManager.onIdleState(context = context)
        assertEquals(phoneCallManager.state.value, CallState.IDLE)
    }

    private fun isBacklogContains(number: String) =
        phoneCallManager.callsBacklog.value.any { it.number == number }


    @Test
    fun outgoing() {
        phoneCallManager.onOffHookState("testNumber", context)
        assertEquals(phoneCallManager.state.value, CallState.OUTGOING)
        assertEquals(phoneCallManager.callOnTheLine.value, "testNumber")
        endCall()
        assertTrue(isBacklogContains("testNumber"))
    }
}