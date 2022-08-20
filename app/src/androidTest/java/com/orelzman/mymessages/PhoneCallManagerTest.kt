package com.orelzman.mymessages

import android.content.Context
import android.telephony.TelephonyManager
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.orelzman.mymessages.data.interactors.DataSourceCallsInteractorImpl
import com.orelzman.mymessages.data.interactors.PhoneCallsInteractorImpl
import com.orelzman.mymessages.data.local.LocalDatabase
import com.orelzman.mymessages.data.local.type_converters.Converters
import com.orelzman.mymessages.domain.interactors.CallType
import com.orelzman.mymessages.domain.interactors.PhoneCallsInteractor
import com.orelzman.mymessages.domain.managers.phonecall.PhoneCallManagerImpl
import com.orelzman.mymessages.domain.model.entities.CallLogEntity
import com.orelzman.mymessages.domain.managers.phonecall.CallState
import com.orelzman.mymessages.domain.managers.phonecall.PhoneCallManager
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@Suppress("SameParameterValue")
@RunWith(AndroidJUnit4::class)
@SmallTest
class PhoneCallManagerTest {
    private lateinit var manager: PhoneCallManager
    private lateinit var interactor: PhoneCallsInteractor
    private lateinit var db: LocalDatabase

    private lateinit var mockContext: Context

    private val callLogInteractor: StubCallLogInteractor = StubCallLogInteractor()
    private var callsNotInLog: ArrayList<Pair<Numbers, CallType>> = ArrayList()

    @ExperimentalPermissionsApi
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
        this.interactor =
            PhoneCallsInteractorImpl(repository = StubRepository(), database = db)

        this.manager = PhoneCallManagerImpl(
            phoneCallInteractor = this.interactor,
            null,
            DataSourceCallsInteractorImpl(context = mockContext),
            callLogInteractor
        )
        idle(Numbers.OREL)
        db.clearAllTables()
    }

    @Test
    fun testIncomingThenWaitingAnsweredThenIncomingAnswered() {
        val count = 30
        val numberOfCallsInDbFromFunction = 3
        for (i in 0 until count) {
            incomingThenWaitingAnsweredThenIncomingAnswered()
        }
        printDBSize()
        printDB()
        testDBSize(numberOfCallsInDbFromFunction * count)
    }

    private fun incomingThenWaitingAnsweredThenIncomingAnswered() {
        incomingCallAnswered(Numbers.OREL)
        testState(CallState.OnCall)
        waitingCall(Numbers.MOM)
        testState(CallState.Waiting)
        waitingCallAnswered(Numbers.MOM)
        testState(CallState.OnCall)
        testLastNumber(Numbers.MOM)
        incomingCall(Numbers.SARA)
        testLastNumber(Numbers.SARA)
        testState(CallState.Waiting)
        waitingCallAnswered(number = Numbers.SARA)
        testState(CallState.OnCall)
        hangup()
    }

    @Test
    fun testIncomingThenWaitingAnsweredThenIncomingRejected() {
        incomingCallAnswered(Numbers.OREL)
        testState(CallState.OnCall)
        waitingCall(Numbers.MOM)
        testState(CallState.Waiting)
        printDB()
        printDBSize()
        waitingCallAnswered(Numbers.MOM, 1000)
        printDB()
        printDBSize()
        testState(CallState.OnCall)
        testLastNumber(Numbers.MOM)
        incomingCall(Numbers.SARA)
        testLastNumber(Numbers.SARA)
        testState(CallState.Waiting)
        waitingCallRejected(Numbers.MOM)
        testDBSize(3)
        hangup()
        testDBSize(3)
    }

    @Test
    fun testIncomingThenWaitingAnswered() {
        incomingCallAnswered(Numbers.OREL)
        testState(CallState.OnCall)
        waitingCall(Numbers.MOM)
        testState(CallState.Waiting)
        printDB()
        printDBSize()
        waitingCallAnswered(Numbers.MOM, 1000)
        printDB()
        printDBSize()
        testState(CallState.OnCall)
        testLastNumber(Numbers.MOM)
        hangup()
        printDB()
        printDBSize()
        testDBSize(2)
    }

    @Test
    fun testIncomingThenWaitingRejected() {
        incomingCallAnswered(Numbers.OREL)
        testState(CallState.OnCall)

        waitingCall(Numbers.DAD)
        testState(CallState.Waiting)

        waitingCallRejected(previousNumber = Numbers.OREL)
        testState(CallState.OnCall)
        testLastNumber(Numbers.DAD)

        hangup(Numbers.OREL, CallType.INCOMING)

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
            outgoingCall(duration, number)
            hangup(number, CallType.OUTGOING)
            testState(CallState.Idle)
        }
    }

    @Test
    fun testIncomingCallsAnsweredAndHangup() {
        val count = 3
        incomingCallsAnsweredAndHangup(count, Numbers.OREL, 1000)
        testDBSize(count)
    }

    private fun incomingCallsAnsweredAndHangup(count: Int, number: Numbers, duration: Long = 100) {
        for (i in 0 until count) {
            incomingCallAnswered(number, duration)
            hangup(number, CallType.INCOMING)
        }
    }

    private fun incomingCallAnswered(number: Numbers, millis: Long = 100) {
        incomingCall(number, millis)
        offhook(number, millis)
    }

    private fun incomingCall(number: Numbers, millis: Long = 100) {
        ring(number, millis)
        callsNotInLog.add(Pair(number, CallType.INCOMING))
    }

    private fun outgoingCall(millis: Long, number: Numbers) {
        offhook(number, millis)
        callsNotInLog.add(Pair(number, CallType.OUTGOING))
    }


    private fun waitingCall(number: Numbers) {
        if (getState() != CallState.OnCall) {
            throw CantStartWaitingFromIdleOrWaitingStates
        }
        ring(number)
        callsNotInLog.add(Pair(number, CallType.INCOMING))
    }

    private fun hangup(number: Numbers, type: CallType) {
        idle(number = number)
        callLogInteractor.addToCallLog(
            CallLogEntity(
                number = number.value,
                duration = 0,
                name = "",
                time = 0,
                callLogType = type
            )
        )
        callsNotInLog.remove(Pair(number, type))
        testState(CallState.Idle)
    }

    private fun hangup() {
        callLogInteractor.addToCallLog(callsNotInLog.map {
            CallLogEntity(
                number = it.first.value,
                duration = 0,
                name = "",
                time = 0,
                callLogType = it.second
            )
        })
        callsNotInLog = ArrayList()
    }

    private fun waitingCallAnswered(number: Numbers, millis: Long = 100) {
        if (getState() != CallState.Waiting) throw CantEndWaitingCallFromNotWaitingState
        offhook(number, millis)
    }

    private fun waitingCallRejected(previousNumber: Numbers) {
        if (getState() != CallState.Waiting) throw CantEndWaitingCallFromNotWaitingState
        offhook(previousNumber)
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
        manager.onStateChanged(state.value, number.value, mockContext)
    }

    private fun testDBSize(sizeExpected: Int) {
        val calls = this.interactor.getAll()
        assert(calls.size == sizeExpected)
    }

    private fun printDB() {
        println("TEST::: ${this.interactor.getAll().map { it.number }}")
    }

    private fun printDBSize() {
        println("TEST::: ${this.interactor.getAll().size}")
    }

    private fun testState(state: CallState) =
        assert(getState() == state)

    private fun testLastNumber(number: Numbers) =
        assert(this.interactor.getAll().last().number == number.value)

    private fun getState(): CallState =
        CallState.fromString(this.manager.callsData.callState ?: "") ?: throw Exception("No state")

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