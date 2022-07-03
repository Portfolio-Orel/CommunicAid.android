package com.orelzman.mymessages

import android.content.Context
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.orelzman.mymessages.data.local.LocalDatabase
import com.orelzman.mymessages.data.local.interactors.phoneCall.PhoneCallsInteractor
import com.orelzman.mymessages.data.local.interactors.phoneCall.PhoneCallsInteractorImpl
import com.orelzman.mymessages.data.local.type_converters.Converters
import com.orelzman.mymessages.domain.service.phone_call.PhoneCallManager
import com.orelzman.mymessages.domain.service.phone_call.PhoneCallManagerImpl
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
    private lateinit var phoneCallManager: PhoneCallManager
    private lateinit var phoneCallsInteractor: PhoneCallsInteractor
    private lateinit var stubDB: LocalDatabase

    private lateinit var mockContext: Context

    @OptIn(ExperimentalPermissionsApi::class)
    @Before
    fun setUp() {
        mockContext = InstrumentationRegistry.getInstrumentation().targetContext
        stubDB = Room.inMemoryDatabaseBuilder(
            mockContext,
            LocalDatabase::class.java,
        )
            .addTypeConverter(Converters())
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()

        phoneCallsInteractor =
            PhoneCallsInteractorImpl(repository = StubRepository(), database = stubDB)
        phoneCallManager = PhoneCallManagerImpl(
            phoneCallInteractor = phoneCallsInteractor,
            null
        )
    }

    @After
    fun teardown() {
        stubDB.close()
    }

    @Test
    fun endCall() {
        val phoneCalls = phoneCallsInteractor.getAll()
        assert(phoneCalls.isEmpty())
    }


}