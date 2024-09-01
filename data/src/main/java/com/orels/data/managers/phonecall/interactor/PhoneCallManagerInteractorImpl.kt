package com.orels.data.managers.phonecall.interactor

import android.content.Context
import com.orels.domain.interactors.CallPreferences
import com.orels.domain.managers.phonecall.PhoneCallManager
import com.orels.domain.managers.phonecall.interactor.PhoneCallManagerInteractor
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

    override fun resetIfNoActiveCall() = phoneCallManager.resetState()

    override fun hangupCall() = phoneCallManager.hangupCall(context = context)
}