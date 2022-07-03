package com.orelzman.mymessages

import android.content.Context
import android.telephony.TelephonyManager
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.orelzman.mymessages.data.local.LocalDatabase
import com.orelzman.mymessages.data.local.interactors.phoneCall.PhoneCallsInteractor
import com.orelzman.mymessages.data.local.interactors.phoneCall.PhoneCallsInteractorImpl
import com.orelzman.mymessages.data.local.type_converters.Converters
import com.orelzman.mymessages.domain.service.phone_call.CallState
import com.orelzman.mymessages.domain.service.phone_call.PhoneCallManager
import com.orelzman.mymessages.domain.service.phone_call.PhoneCallManagerImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
@SmallTest
class PhoneCallManagerTest {
    private lateinit var manager: PhoneCallManager
    private lateinit var interactor: PhoneCallsInteractor
    private lateinit var db: LocalDatabase

    private lateinit var mockContext: Context

    @OptIn(ExperimentalPermissionsApi::class)
    @Before
    fun setUp() {
        mockContext = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(
            mockContext,
            LocalDatabase::class.java,
        )
            .addTypeConverter(Converters())
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
        interactor =
            PhoneCallsInteractorImpl(repository = StubRepository(), database = db)
        manager = PhoneCallManagerImpl(
            phoneCallInteractor = interactor,
            null
        )
        idle(Numbers.OREL)
        db.clearAllTables()
    }

    @Test
    fun testIncomingThenWaitingAnswered() {

    }

    @Test
    fun testIncomingThenWaitingRejected() {
        incomingCallAnswered(1000, Numbers.OREL)
        waitingCall(Numbers.MOM)
        waitingCallRejected(previousNumber = Numbers.OREL)
        hangup(Numbers.OREL)
        testDBSize(2)
    }


    @Test
    fun testOutgoingCallsAnsweredAndHangup() {
        val count = 3
        outgoingCallsAnsweredAndHangup(count, Numbers.OREL, 1000)
        testDBSize(count)
    }

    private fun outgoingCallsAnsweredAndHangup(count: Int, number: Numbers, duration: Long) {
        for (i in 0 until count) {
            outgoingCallAnswered(duration, number)
            hangup(number)
        }
    }

    private fun outgoingCallAnswered(millis: Long, number: Numbers) {
        offhook(number, millis)
    }

    @Test
    fun testIncomingCallsAnsweredAndHangup() {
        val count = 3
        incomingCallsAnsweredAndHangup(count, Numbers.OREL, 1000)
        testDBSize(count)
    }

    private fun incomingCallsAnsweredAndHangup(count: Int, number: Numbers, duration: Long) {
        for (i in 0 until count) {
            incomingCallAnswered(duration, number)
            hangup(number)
        }
    }

    private fun incomingCallAnswered(millis: Long, number: Numbers) {
        ring(number)
        offhook(number, millis)
    }

    private fun hangup(number: Numbers) {
        idle(number = number)
    }

    private fun waitingCallAnswered(number: Numbers) {
        if (manager.state.value != CallState.WAITING) throw CantEndWaitingCallFromNotWaitingState
        offhook(number)
    }

    private fun waitingCallRejected(previousNumber: Numbers) {
        if (manager.state.value != CallState.WAITING) throw CantEndWaitingCallFromNotWaitingState
        offhook(previousNumber)
    }

    private fun waitingCall(number: Numbers) {
        if (manager.state.value != CallState.INCOMING
            && manager.state.value != CallState.OUTGOING
        ) throw CantStartWaitingFromIdleOrWaitingStates
        ring(number)
    }

    private fun idle(number: Numbers, sleepTime: Long = 100) {
        changeState(States.IDLE, number)
        Thread.sleep(sleepTime)
    }

    private fun offhook(number: Numbers, sleepTime: Long = 100) {
        changeState(States.OFFHOOK, number)
        Thread.sleep(sleepTime)
    }

    private fun ring(number: Numbers, sleepTime: Long = 100) {
        changeState(States.RINGING, number)
        Thread.sleep(sleepTime)
    }

    private fun changeState(state: States, number: Numbers) {
        CoroutineScope(Dispatchers.IO).launch {
            manager.onStateChanged(state.value, number.value)
        }
    }

    private fun testDBSize(sizeExpected: Int) {
        val calls = interactor.getAll()
        assert(calls.size == sizeExpected)
    }

    @After
    fun teardown() {
        db.close()
    }
}

enum class Numbers(val value: String) {
    OREL("0543056286"),
    SARA("0528112646"),
    MOM("0543050285"),
    DAD("0542444505"),
}

enum class States(val value: String) {
    IDLE(TelephonyManager.EXTRA_STATE_IDLE),
    RINGING(TelephonyManager.EXTRA_STATE_RINGING),
    OFFHOOK(TelephonyManager.EXTRA_STATE_OFFHOOK);
}

object CantStartWaitingFromIdleOrWaitingStates : Exception()
object CantEndWaitingCallFromNotWaitingState : Exception()