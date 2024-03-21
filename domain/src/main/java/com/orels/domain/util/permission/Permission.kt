package com.orels.domain.util.permission

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.orels.domain.util.common.Logger

/**
 * @author Orel Zilberman
 * 26/08/2022
 */
enum class RequiredPermission(val permissionName: String, priority: PermissionPriority) {
    ReadPhoneState(
        permissionName = Manifest.permission.READ_PHONE_STATE,
        priority = PermissionPriority.Critical,
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
    @Deprecated("Not allowed in play store")
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

    fun getPermissionState(context: Context): PermissionState {
        if (permissionName == Manifest.permission.SYSTEM_ALERT_WINDOW) {
            return if (PermissionsUtils.canDrawOverlays(context)) {
                PermissionState.Granted
            } else {
                PermissionState.DeniedOnce
            }
        }
        if (PermissionsUtils.checkPermission(context = context, this)) {
            PermissionMetaData.setPermissionState(permissionName, PermissionState.Granted, context)
            return PermissionState.Granted
        }
        if (context as? Activity != null) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(context, permissionName)) {
                val state = PermissionMetaData.getPermissionState(permissionName,
                    context)
                // It's possible that the permission was granted but denied from settings
                if (state == PermissionState.NotAsked || state == PermissionState.Granted) {
                    PermissionMetaData.setPermissionState(permissionName,
                        PermissionState.NotAsked,
                        context)
                    return PermissionState.NotAsked
                }
                return PermissionState.DeniedPermanently
            }
        }
        PermissionMetaData.setPermissionState(permissionName, PermissionState.DeniedOnce, context)
        return PermissionState.DeniedOnce
    }

    fun isNotGranted(context: Context) =
        getPermissionState(context = context) != PermissionState.Granted

    fun requestPermission(context: Context) {
        if (context as? Activity == null) {
            Logger.e("Context is not an activity in requestPermission")
            return
        }
        if (getPermissionState(context = context) == PermissionState.Granted) return
        if (permissionName == Manifest.permission.SYSTEM_ALERT_WINDOW) {
            context.startActivity(
                Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:${context.packageName}")
                )
            )
        } else if (getPermissionState(context = context) != PermissionState.DeniedPermanently) {
            ActivityCompat.requestPermissions(
                context,
                listOf(permissionName).toTypedArray(),
                REQUEST_PERMISSION_CODE
            )
        } else {
            context.startActivity(
                Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:${context.packageName}")
                )
            )
        }
    }


    companion object {
        const val REQUEST_PERMISSION_CODE = 443
    }
}