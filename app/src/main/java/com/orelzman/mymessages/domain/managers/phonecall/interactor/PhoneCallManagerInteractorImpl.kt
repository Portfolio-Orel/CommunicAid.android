package com.orelzman.mymessages.domain.managers.phonecall.interactor

import android.content.Context
import com.orelzman.mymessages.domain.interactors.CallPreferences
import com.orelzman.mymessages.domain.managers.phonecall.PhoneCallManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PhoneCallManagerInteractorImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val phoneCallManager: PhoneCallManager
) : PhoneCallManagerInteractor {
    override val callsDataFlow: Flow<CallPreferences>
        get() = phoneCallManager.callsDataFlow
    override val callsData: CallPreferences
        get() = phoneCallManager.callsData

    override fun hangupCall() = phoneCallManager.hangupCall(context = context)
}