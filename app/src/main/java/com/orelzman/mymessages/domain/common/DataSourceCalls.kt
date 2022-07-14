package com.orelzman.mymessages.domain.common

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

private val Context.callsDataStore by preferencesDataStore(
    name = "common_sp_calls"
)

data class CallPreferences(
    val callOnTheLine: String?,
    val callInTheBackground: String?,
    val callState: String?
)

interface DataSourceCalls {
    fun userPreferencesFlow(): Flow<CallPreferences>

    suspend fun updateCallOnTheLine(callOnTheLine: String?)
    suspend fun updateCallInTheBackground(callInTheBackground: String?)
    suspend fun updateState(state: String?)
}

class DataSourceCallsImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : DataSourceCalls {

    override fun userPreferencesFlow(): Flow<CallPreferences> =
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

    override suspend fun updateCallOnTheLine(callOnTheLine: String?) {
        context.callsDataStore.edit { preferences ->
            preferences[PreferencesKeys.CALL_ON_LINE] = callOnTheLine ?: ""
        }
    }

    override suspend fun updateCallInTheBackground(callInTheBackground: String?) {
        context.callsDataStore.edit { preferences ->
            preferences[PreferencesKeys.CALL_IN_BACKGROUND] = callInTheBackground ?: ""
        }
    }

    override suspend fun updateState(state: String?) {
        context.callsDataStore.edit { preferences ->
            preferences[PreferencesKeys.STATE] = state ?: ""
        }
    }


}

private object PreferencesKeys {
    val CALL_ON_LINE = stringPreferencesKey("CALL_ON_LINE")
    val CALL_IN_BACKGROUND = stringPreferencesKey("CALL_IN_BACKGROUND")
    val STATE = stringPreferencesKey("STATE")
}