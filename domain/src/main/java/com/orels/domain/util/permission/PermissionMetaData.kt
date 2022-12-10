package com.orels.domain.util.permission

import android.content.Context
import android.content.SharedPreferences

/**
 * @author Orel Zilberman
 * 26/08/2022
 */

/**
 * This class is used to save the permission state in the shared preferences.
 * If the state is empty, it means that the user was not asked yet.
 * The rest of the permissions are self explanatory.
 */
private val Context.permissionsStateSharedPreferences: SharedPreferences
    get() = getSharedPreferences(
        "sp_permissions_state", Context.MODE_PRIVATE
    )

class PermissionMetaData {
    companion object {
        fun getPermissionState(permissionName: String, context: Context): PermissionState {
            val state = context.permissionsStateSharedPreferences.getString(permissionName, null)
            return state?.let { PermissionState.valueOf(it) } ?: PermissionState.NotAsked
        }

        fun setPermissionState(permissionName: String, state: PermissionState, context: Context) {
            context.permissionsStateSharedPreferences.edit().putString(permissionName, state.name)
                .apply()
        }
    }
}

enum class PermissionPriority {
    Low, // Required for some convenient settings. Example: Send sms to call in background.
    Medium, // Required for some important ux stuff. Example: Fetching caller's name.
    High, // Required for data coherent data saving. Example: Read call log to make sure all calls are saved.
    Critical; // Required for the app to function properly. Example: Reading phone's state.
}

enum class PermissionState {
    Granted,
    NotAsked,
    DeniedOnce,
    DeniedPermanently;
}