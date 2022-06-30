//package com.orelzman.mymessages
//
//import android.content.Context
//import com.google.accompanist.permissions.ExperimentalPermissionsApi
//import com.orelzman.mymessages.data.local.interactors.phoneCall.PhoneCallsInteractorImpl
//import com.orelzman.mymessages.domain.service.phone_call.PhoneCallManagerImpl
//import com.orelzman.mymessages.domain.service.phone_call.CallState
//import com.orelzman.mymessages.domain.service.phone_call.PhoneCallManagerInteractorImpl
//import org.junit.Assert.*
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.mockito.Mock
//import org.mockito.Mockito.mock
//import org.mockito.junit.MockitoJUnitRunner
//
//
//@OptIn(ExperimentalPermissionsApi::class)
//@RunWith(MockitoJUnitRunner::class)
//class PhoneCallManagerTest {
//
//    private val number = "testNumber"
//    private val number2 = "testNumber2"
//    private val phoneCallManager = PhoneCallManagerImpl(phoneCallInteractor = PhoneCallsInteractorImpl(
//        repository =,
//        database =
//    ))
//
//    @Mock
//    private lateinit var mockContext: Context
//
//    @Before
//    fun setUp() {
//        mockContext = mock(Context::class.java)
//        phoneCallManager.onIdleState(context = mockContext)
//    }
//
//    private fun endCall() {
//
//        phoneCallManager.onIdleState(context = mockContext)
//        assertEquals(phoneCallManager.state.value, CallState.IDLE)
//    }
//
//    private fun `Check if backlog contains a number`(number: String) =
//        try {
//            assertTrue(phoneCallManager.callsBacklog.value.any { it.number == number })
//        } catch(excpetion: AssertionError) {
//            throw Exception("Did you comment backlog.value = emptyList() in resetValues?")
//        }
//
//    private fun testState(state: CallState) =
//        assertEquals(phoneCallManager.state.value, state)
//
//    private fun testCallOnTheLine(number: String) =
//        assertEquals(phoneCallManager.callOnTheLine.value?.number, number)
//
//    private fun testBackgroundCall(number: String) =
//        assertEquals(phoneCallManager.callInTheBackground.value?.number, number)
//
//    private fun `Check if call in backlog is outgoing`(number: String) =
//        assertFalse(phoneCallManager.callsBacklog.value.first { it.number == number }.isIncoming)
//
//    private fun `Check if call in backlog is incoming`(number: String) =
//        assertTrue(phoneCallManager.callsBacklog.value.first { it.number == number }.isIncoming)
//
//    private fun `Check if call in backlog is answered`(number: String) =
//        assertTrue(phoneCallManager.callsBacklog.value.first { it.number == number }.isAnswered)
//
//    private fun `Check if call in backlog is missed`(number: String) =
//        assertFalse(phoneCallManager.callsBacklog.value.first { it.number == number }.isAnswered)
//
//    @Test
//    fun `Test an outgoing call`() {
//        phoneCallManager.onOffHookState(number, mockContext)
//        testState(CallState.OUTGOING)
//        testCallOnTheLine(number)
//        endCall()
//        `Check if backlog contains a number`(number)
//        `Check if call in backlog is outgoing`(number)
//    }
//
//    @Test
//    fun `Test an incoming call`() {
//        phoneCallManager.onRingingState(number, mockContext)
//        testState(CallState.INCOMING)
//        testCallOnTheLine(number)
//        endCall()
//        `Check if backlog contains a number`(number)
//        `Check if call in backlog is incoming`(number)
//    }
//
//    fun `Test waiting call`() {
//        phoneCallManager.onRingingState(number, mockContext)
//
//    }
//
//}