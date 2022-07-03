package com.orelzman.mymessages

import android.content.Context
import androidx.room.Room
import com.orelzman.mymessages.data.dto.DeletedUnhandledCalls
import com.orelzman.mymessages.data.dto.PhoneCall
import com.orelzman.mymessages.data.local.LocalDatabase
import com.orelzman.mymessages.data.local.interactors.unhandled_calls.UnhandledCallsInteractor
import com.orelzman.mymessages.data.local.interactors.unhandled_calls.UnhandledCallsInteractorImpl
import com.orelzman.mymessages.data.local.type_converters.Converters
import com.orelzman.mymessages.domain.model.CallLogEntity
import com.orelzman.mymessages.util.CallType
import com.orelzman.mymessages.util.startOfDay
import junit.framework.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import java.util.*
import kotlin.random.Random


@RunWith(MockitoJUnitRunner::class)

class DeletedUnhandledCallsTest {

    @Mock
    private lateinit var mockContext: Context

    private lateinit var unhandledCallsInteractor: UnhandledCallsInteractor
    private lateinit var db: LocalDatabase

    private var callLogs: ArrayList<CallLogEntity> = ArrayList()
    private var deletedUnhandledCalls: ArrayList<DeletedUnhandledCalls> = ArrayList()

    @Before
    fun setUp() {
        mockContext = mock(Context::class.java)
        db = Room.databaseBuilder(
            mockContext,
            LocalDatabase::class.java,
            "mymessagesdb.db"
        )
            .addTypeConverter(Converters())
            .fallbackToDestructiveMigration()
            .build()
        unhandledCallsInteractor =
            UnhandledCallsInteractorImpl(repository = null, database = db)
    }

    @Test
    fun `test incoming call unhandled`() {
        incomingCall(Numbers.OREL)
        val calls = filterCalls()
        assertTrue(calls.isEmpty())
    }

    @Test
    fun `test outgoing call unhandled`() {
        outgoingCall(Numbers.OREL)
        val calls = filterCalls()
        assertTrue(calls.isEmpty())
    }

    @Test
    fun `test a bunch of calls no removed`() {
        incomingCall(Numbers.OREL)
        missedCall(Numbers.SARA)
        missedCall(Numbers.OREL)
        incomingCall(Numbers.MOM)
        outgoingCall(Numbers.DAD)
        missedCall(Numbers.DAD)
        val calls = filterCalls().map { it.number }
        assertTrue(calls.size == 3)
        assertTrue(calls.containsAll(listOf(Numbers.OREL.value, Numbers.DAD.value, Numbers.SARA.value)))
    }

    @Test
    fun `test a bunch of calls with removed`() {
        incomingCall(Numbers.OREL)
        missedCall(Numbers.SARA)
        deleteNumber(Numbers.SARA)
        missedCall(Numbers.OREL)
        deleteNumber(Numbers.OREL)
        missedCall(Numbers.OREL)
        incomingCall(Numbers.MOM)
        outgoingCall(Numbers.DAD)
        missedCall(Numbers.DAD)
        missedCall(Numbers.DAD)
        missedCall(Numbers.DAD, date = Date().startOfDay)
        deleteNumber(Numbers.DAD)
        val calls = filterCalls().map { it.number }
        assertTrue(calls.size == 1)
        assertTrue(calls.containsAll(listOf(Numbers.OREL.value)))
    }

    @Test
    fun `missed-outgoing missed-incoming`() {
        missedCall(Numbers.OREL)
        incomingCall(Numbers.OREL)
        missedCall(Numbers.DAD)
        outgoingCall(Numbers.DAD)
        val calls = filterCalls().map { it.number }
        assertTrue(calls.isEmpty())
    }

    @Test
    fun `missed-deleted missed-outgoing missed-incoming`() {
        missedCall(Numbers.SARA)
        deleteNumber(Numbers.SARA)
        missedCall(Numbers.OREL)
        incomingCall(Numbers.OREL)
        missedCall(Numbers.DAD)
        outgoingCall(Numbers.DAD)
        val calls = filterCalls().map { it.number }
        assertTrue(calls.isEmpty())
    }

    @Test
    fun `missed-deleted missed-outgoing missed-incoming missed`() {
        missedCall(Numbers.SARA)
        deleteNumber(Numbers.SARA)
        missedCall(Numbers.OREL)
        incomingCall(Numbers.OREL)
        missedCall(Numbers.DAD)
        outgoingCall(Numbers.DAD)
        missedCall(Numbers.OREL)
        val calls = filterCalls().map { it.number }
        assertTrue(calls.size == 1)
        assertTrue(calls.contains(Numbers.OREL.value))
    }

    @Test
    fun `test calls with multiple missed and 1 remove`() {
        missedCall(Numbers.DAD)
        missedCall(Numbers.DAD)
        missedCall(Numbers.DAD)
        missedCall(Numbers.DAD)
        missedCall(Numbers.DAD)
        deleteNumber(Numbers.DAD)
        val calls = filterCalls().map { it.number }
        assertTrue(calls.isEmpty())
    }

    private fun filterCalls(): List<CallLogEntity> =
        unhandledCallsInteractor.filterUnhandledCalls(
            deletedUnhandledCalls = deletedUnhandledCalls,
            callLogs = callLogs
        )


    private fun incomingCall(number: Numbers) =
        addNumberToLog(number, CallType.INCOMING)

    private fun outgoingCall(number: Numbers) =
        addNumberToLog(number, CallType.OUTGOING)

    private fun missedCall(number: Numbers, date: Date = Date()) =
        addNumberToLog(number, CallType.MISSED, date)

    private fun rejectedCall(number: Numbers) =
        addNumberToLog(number, CallType.REJECTED)

    private fun blockedCall(number: Numbers) =
        addNumberToLog(number, CallType.BLOCK)

    private fun deleteNumber(number: Numbers) {
        deletedUnhandledCalls.add(
            DeletedUnhandledCalls(
                id = "1$number", phoneCall = PhoneCall(
                    number = number.value,
                    startDate = Date(),
                    endDate = Date(),
                    type = CallType.INCOMING.name,
                    isWaiting = false,
                    messagesSent = listOf(),
                ), deleteDate = Date()
            )
        )
        Thread.sleep(10)
    }

    private fun addNumberToLog(number: Numbers, type: CallType, date: Date = Date()) {
        callLogs.add(
            CallLogEntity(
                number = number.value,
                duration = 1,
                dateMilliseconds = date.time,
                callLogType = type
            )
        )
        Thread.sleep(10)
    }

    private fun randomizeNumber(): String {
        var number = ""
        for (i in 0..9) {
            number += Random.nextInt(0, 9)
        }
        return number
    }
}

enum class Numbers(val value: String) {
    OREL("0543056286"),
    SARA("0528112646"),
    MOM("0543050285"),
    DAD("0542444505"),
    RANDOM1("0527746342"),
    RANDOM2("0509362641")
}