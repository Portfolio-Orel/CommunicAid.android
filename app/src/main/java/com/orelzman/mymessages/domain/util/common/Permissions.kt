package com.orelzman.mymessages.domain.util.common

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.provider.Settings
import androidx.core.content.ContextCompat

/**
 * @author Orel Zilberman
 * 26/08/2022
 */

enum class RequiredPermissions(val permissionName: String, priority: PermissionPriority) {
    ReadPhoneState(
        permissionName = Manifest.permission.READ_PHONE_STATE,
        priority = PermissionPriority.Critical
    ),
    ReadCallLog(
        permissionName = Manifest.permission.READ_CALL_LOG,
        priority = PermissionPriority.High
    ),
    CallPhone(
        permissionName = Manifest.permission.CALL_PHONE,
        priority = PermissionPriority.Medium
    ),
    ReadContacts(
        permissionName = Manifest.permission.READ_CONTACTS,
        priority = PermissionPriority.Medium
    ),
    SendSMS(permissionName = Manifest.permission.SEND_SMS, priority = PermissionPriority.Low),
    DrawOverlays(
        permissionName = Manifest.permission.SYSTEM_ALERT_WINDOW,
        priority = PermissionPriority.Low
    );

    private object PermissionsUtils {
        fun canDrawOverlays(context: Context): Boolean = Settings.canDrawOverlays(context)

        fun checkPermission(context: Context, permission: RequiredPermissions): Boolean =
            ContextCompat.checkSelfPermission(
                context,
                permission.permissionName
            ) == PERMISSION_GRANTED
    }

    fun isGranted(context: Context): Boolean =
        if (permissionName == Manifest.permission.SYSTEM_ALERT_WINDOW) {
            PermissionsUtils.canDrawOverlays(context)
        } else {
            PermissionsUtils.checkPermission(context = context, this)
        }

    fun isNotGranted(context: Context) = !isGranted(context = context)

    companion object {
        fun fromString(permission: String): RequiredPermissions? {
            values().forEach {
                if (it.permissionName == permission) return it
            }
            return null
        }
    }

    enum class PermissionPriority {
        Low, // Required for some convinient settings. Example: Send sms to call in background.
        Medium, // Required for some important ux stuff. Example: Fetching caller's name.
        High, // Required for data coherent data saving. Example: Read call log to make sure all calls are saved.
        Critical; // Required for the app to function properly. Example: Reading phone's state.
    }
}
