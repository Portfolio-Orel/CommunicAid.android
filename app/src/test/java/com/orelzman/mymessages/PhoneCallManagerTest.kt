package com.orelzman.mymessages

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.orelzman.mymessages.data.local.LocalDatabase
import com.orelzman.mymessages.data.local.interactors.phoneCall.PhoneCallsInteractor
import com.orelzman.mymessages.data.local.interactors.phoneCall.PhoneCallsInteractorImpl
import com.orelzman.mymessages.data.local.type_converters.Converters
import com.orelzman.mymessages.domain.service.phone_call.PhoneCallManager
import com.orelzman.mymessages.domain.service.phone_call.PhoneCallManagerImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import kotlin.coroutines.CoroutineContext

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalPermissionsApi::class)
@RunWith(MockitoJUnitRunner::class)
class PhoneCallManagerTest {

    private lateinit var phoneCallManager: PhoneCallManager
    private lateinit var phoneCallsInteractor: PhoneCallsInteractor
    private lateinit var stubDB: LocalDatabase

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private val networkContext: CoroutineContext = testDispatcher

    @Mock
    private lateinit var mockContext: Context

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        mockContext = mock(Context::class.java)
        `when`((mockContext.getString(R.string.local_db_name))).thenReturn("mymessagesdb_stub.db")
        stubDB = with(mockContext) {
            Room.databaseBuilder(
                mockContext,
                LocalDatabase::class.java,
                getString(R.string.local_db_name)
            )
                .addTypeConverter(Converters())
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build()

        }
        phoneCallsInteractor =
            PhoneCallsInteractorImpl(repository = StubRepository(), database = stubDB)
        phoneCallManager = PhoneCallManagerImpl(phoneCallInteractor = phoneCallsInteractor)
    }

    @After
    fun teardown() {
        stubDB.close()
    }

    @Test
    fun endCall() = runBlocking {
            val messages = phoneCallsInteractor.getAll()
    }
}