package com.orelzman.mymessages.data.interactors

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.orelzman.mymessages.domain.interactors.CallPreferences
import com.orelzman.mymessages.domain.interactors.DataSourceCallsInteractor
import com.orelzman.mymessages.domain.managers.phonecall.CallState
import com.orelzman.mymessages.domain.managers.phonecall.toState
import com.orelzman.mymessages.domain.model.entities.PhoneCall
import com.orelzman.mymessages.domain.model.entities.toPhoneCall
import com.orelzman.mymessages.domain.util.extension.Logger
import com.orelzman.mymessages.domain.util.extension.log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

private val Context.callsDataStore by preferencesDataStore(
    name = "sp_calls_data_store"
)

private val Context.callsSharedPrefrences: SharedPreferences
    get() = getSharedPreferences(
        "sp_calls", Context.MODE_PRIVATE
    )

class DataSourceCallsInteractorImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : DataSourceCallsInteractor {

    override fun callsPreferencesFlow(): Flow<CallPreferences> =
        context.callsDataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                val callOnTheLine = preferences[PreferencesKeys.CALL_ON_LINE]
                val callInTheBackground = preferences[PreferencesKeys.CALL_IN_BACKGROUND]
                val state = preferences[PreferencesKeys.STATE]
                CallPreferences(
                    callOnTheLine = callOnTheLine,
                    callInTheBackground = callInTheBackground,
                    callState = state
                )
            }

    override fun callStateFlow(): Flow<CallState?> = context.callsDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            CallState.fromString(preferences[PreferencesKeys.STATE])
        }

    override fun callsPrefrences(): SharedPreferences =
        context.callsSharedPrefrences

    override fun init() {
        updateCallOnTheLine(null)
        updateCallInTheBackground(null)
        updateState(null)
    }


    override fun updateCallOnTheLine(callOnTheLine: PhoneCall?) {
        updateSharedPrefrences(PreferencesKeys.CALL_ON_LINE.name, callOnTheLine?.stringify())
        CoroutineScope(SupervisorJob()).launch {
            try {
                context.callsDataStore.edit { preferences ->
                    preferences[PreferencesKeys.CALL_ON_LINE] = callOnTheLine?.stringify() ?: ""
                }
            } catch (e: Exception) {
                e.log()
            }
        }
    }

    override fun updateCallInTheBackground(callInTheBackground: PhoneCall?) {
        updateSharedPrefrences(
            PreferencesKeys.CALL_IN_BACKGROUND.name,
            callInTheBackground?.stringify() ?: ""
        )
        CoroutineScope(SupervisorJob()).launch {
            try {
                context.callsDataStore.edit { preferences ->
                    preferences[PreferencesKeys.CALL_IN_BACKGROUND] =
                        callInTheBackground?.stringify() ?: ""
                }
            } catch (e: Exception) {
                e.log()
            }
        }
    }

    override fun updateState(state: CallState?) {
        Logger.i("Updating state: $state, previous state: ${getState()}")
        if (getState() == state) return
        updateSharedPrefrences(PreferencesKeys.STATE.name, state?.value ?: "")
        CoroutineScope(SupervisorJob()).launch {
            try {
                context.callsDataStore.edit { preferences ->
                    preferences[PreferencesKeys.STATE] = state?.value ?: ""
                }
            } catch (e: Exception) {
                e.log()
            }
        }
    }

    override fun getState(): CallState? =
        (context.callsSharedPrefrences.getString(PreferencesKeys.STATE.name, null) ?: "").toState()

    override fun getCallOnTheLine(): PhoneCall? =
        context.callsSharedPrefrences.getString(PreferencesKeys.CALL_ON_LINE.name, null)
            ?.toPhoneCall()

    override fun getCallInTheBackground(): PhoneCall? =
        (context.callsSharedPrefrences.getString(
            PreferencesKeys.CALL_IN_BACKGROUND.name,
            null
        ))?.toPhoneCall()

    private fun updateSharedPrefrences(key: String, value: String?) {
        with(context.callsSharedPrefrences.edit()) {
            putString(key, value)
            apply()
        }
    }

}

private object PreferencesKeys {
    val CALL_ON_LINE = stringPreferencesKey("CALL_ON_LINE")
    val CALL_IN_BACKGROUND = stringPreferencesKey("CALL_IN_BACKGROUND")
    val STATE = stringPreferencesKey("STATE")
}