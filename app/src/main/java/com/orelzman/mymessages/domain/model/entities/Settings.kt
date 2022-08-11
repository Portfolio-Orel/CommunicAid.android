package com.orelzman.mymessages.domain.model.entities

import androidx.annotation.StringRes
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.orelzman.mymessages.R
import com.orelzman.mymessages.domain.util.extension.formatDayAndHours
import com.orelzman.mymessages.domain.util.extension.log
import java.util.*
import kotlin.reflect.KClass

@Entity
data class Settings(
    @PrimaryKey val key: SettingsKey,
    val value: String = key.defaultValue
) {

    /**
     * Casts value to it's actual value type.
     * @return value of type [T] or the default value of the casting fails.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getRealValue(): T? {
        return try {
            val realValue = Gson().fromJson(value, key.valueType.java)
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
}

enum class SettingsKey(
    val keyInServer: String,
    val type: SettingsType,
    val valueType: KClass<*>,
    val defaultValue: String,
    @StringRes val title: Int? = null
) {
    CallsUpdateAt(
        keyInServer = "calls_update_at",
        type = SettingsType.Data,
        valueType = Long::class,
        defaultValue = Date().time.toString(),
        title = R.string.last_calls_update
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
        title = R.string.show_app_on_call
    ),
    IgnoredList(
        keyInServer = "ignored_list",
        type = SettingsType.PopUp,
        valueType = Array<String>::class,
        defaultValue = emptyList<String>().toString(),
        title = R.string.ignore_list
    ),
    SendSMSToBackgroundCall(
        keyInServer = "send_sms_to_background_call",
        type = SettingsType.Toggle,
        valueType = Boolean::class,
        defaultValue = false.toString(),
        title = R.string.send_sms_to_background_call
    );

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