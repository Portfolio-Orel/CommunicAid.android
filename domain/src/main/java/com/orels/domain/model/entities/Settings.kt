package com.orels.domain.model.entities

import android.content.Context
import androidx.annotation.StringRes
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.orels.domain.R
import com.orels.domain.util.permission.RequiredPermission
import com.orels.domain.util.extension.formatDayAndHours
import com.orels.domain.util.extension.log
import java.util.*
import kotlin.reflect.KClass

@Entity
data class Settings(
    @PrimaryKey var key: SettingsKey,
    var value: String = key.defaultValue,
    var editEnabled: Boolean? = null,
) : Uploadable() {

    fun isEnabled(): Boolean {
        return if(editEnabled != null) editEnabled!! else key.defaultEditEnabled
    }

    override fun equals(other: Any?): Boolean {
        if (other is Settings) {
            return (other.key.keyInServer == key.keyInServer)
                    && (other.value == value)
                    && (other.editEnabled == editEnabled)
        }
        return false
    }

    /**
     * Casts value to it's actual value type.
     * @return value of type [T] or the default value of the casting fails.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getRealValue(): T? {
        return try {
            val gson = GsonBuilder()
                .setLenient()
                .create()
            val json = gson.toJson(value)
            val realValue = gson.fromJson(json, key.valueType.java)
            return when (key) {
                SettingsKey.CallsUpdateAt -> Date(
                    realValue as? Long ?: Date().time
                ).formatDayAndHours() as T
                else -> realValue as? T
            }
        } catch (e: JsonSyntaxException) {
            e.log()
            key.defaultValue as? T
        }
    }

    /**
     * @return a list of all permissions that are not granted
     */
    fun getPermissionsNotGranted(context: Context): List<RequiredPermission> {
        val permissionsNotGranted = ArrayList<RequiredPermission>()
        key.requiredPermissions.forEach {
            if (it.isNotGranted(context = context)) {
                permissionsNotGranted.add(it)
            }
        }
        return permissionsNotGranted
    }

    override fun hashCode(): Int {
        var result = key.hashCode()
        result = 31 * result + value.hashCode()
        return result
    }
}

enum class SettingsKey(
    val keyInServer: String,
    val type: SettingsType,
    val valueType: KClass<*>,
    val defaultValue: String,
    val defaultEditEnabled: Boolean = true,
    val requiredPermissions: List<RequiredPermission> = emptyList(),
    @StringRes val title: Int? = null,
    val visibleToUser: Boolean = true
) {
    CallsUpdateAt(
        keyInServer = "calls_update_at",
        type = SettingsType.Data,
        valueType = Long::class,
        defaultValue = Date().time.toString(),
        title = R.string.last_calls_update,
        visibleToUser = false,
    ),
    IsDataInit(
        keyInServer = "is_data_init",
        type = SettingsType.NotVisibleToUser,
        valueType = Boolean::class,
        defaultValue = false.toString()
    ),
    ShowAppOnCall(
        keyInServer = "show_app_on_call",
        type = SettingsType.Toggle,
        valueType = Boolean::class,
        defaultValue = false.toString(),
        requiredPermissions = listOf(RequiredPermission.DrawOverlays),
        title = R.string.show_app_on_call
    ),
    IgnoredList(
        keyInServer = "ignored_list",
        type = SettingsType.PopUp,
        valueType = Array<String>::class,
        defaultValue = emptyList<String>().toString(),
        title = R.string.ignore_list
    ),
    CountRejectedAsUnhandled(
        keyInServer = "count_rejected_as_unhandled",
        type = SettingsType.Toggle,
        valueType = Boolean::class,
        defaultValue = false.toString(),
        title = R.string.count_rejected_as_missed),
    CanDeleteUnhandledCalls(
        keyInServer = "can_delete_unhandled_calls",
        type = SettingsType.Toggle,
        valueType = Boolean::class,
        defaultValue = true.toString(),
        title = R.string.can_delete_unhandled_calls
    ),
    SendSMSToBackgroundCall(
        keyInServer = "send_sms_to_background_call",
        type = SettingsType.Toggle,
        valueType = Boolean::class,
        defaultValue = false.toString(),
        requiredPermissions = listOf(RequiredPermission.SendSMS),
        title = R.string.send_sms_to_background_call
    ),
    SMSToSendToBackgroundCall(
        keyInServer = "sms_to_send_to_background_call",
        type = SettingsType.NotVisibleToUser,
        valueType = String::class,
        defaultValue = "",
    );

    fun settings(value: String = defaultValue): Settings = Settings(key = this, value = value)

    companion object {

        fun fromString(value: String): SettingsKey? =
            if (values().any { it.keyInServer == value }) {
                values().first { it.keyInServer == value }
            } else {
                null
            }
    }
}

enum class SettingsType {
    Toggle,
    Data,
    NotVisibleToUser,
    PopUp;
}