package com.orelzman.mymessages.domain.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.orelzman.mymessages.domain.util.extension.Logger

/**
 * @author Orel Zilberman
 * 26/08/2022
 */
enum class RequiredPermission(val permissionName: String, priority: PermissionPriority) {
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

        fun checkPermission(context: Context, permission: RequiredPermission): Boolean =
            ContextCompat.checkSelfPermission(
                context,
                permission.permissionName
            ) == PackageManager.PERMISSION_GRANTED
    }

    fun isGranted(context: Context): PermissionState {
        if (permissionName == Manifest.permission.SYSTEM_ALERT_WINDOW) {
            return if (PermissionsUtils.canDrawOverlays(context)) {
                PermissionState.Granted
            } else {
                PermissionState.DeniedOnce
            }
        }
        if (PermissionsUtils.checkPermission(context = context, this)) {
            return PermissionState.Granted
        }
        if (context as? Activity != null) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(context, permissionName)) {
                return PermissionState.DeniedPermanently
            }
        }
        return PermissionState.DeniedOnce
    }

    fun isNotGranted(context: Context) = isGranted(context = context) != PermissionState.Granted

    fun requestPermission(context: Context) {
        if (context as? Activity == null) {
            Logger.e("context is not an activity in requestPermission")
            // ToDo: Do something
            return
        }
        if (isGranted(context = context) == PermissionState.Granted) return
        if (permissionName == Manifest.permission.SYSTEM_ALERT_WINDOW) {
            context.startActivity(
                Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:${context.packageName}")
                )
            )
        } else {
            ActivityCompat.requestPermissions(
                context,
                listOf(permissionName).toTypedArray(),
                REQUEST_PERMISSION_CODE
            )
        }
    }

    companion object {
        const val REQUEST_PERMISSION_CODE = 443
    }

    enum class PermissionPriority {
        Low, // Required for some convinient settings. Example: Send sms to call in background.
        Medium, // Required for some important ux stuff. Example: Fetching caller's name.
        High, // Required for data coherent data saving. Example: Read call log to make sure all calls are saved.
        Critical; // Required for the app to function properly. Example: Reading phone's state.
    }

    enum class PermissionState {
        Granted,
        DeniedOnce,
        DeniedPermanently;
    }
}