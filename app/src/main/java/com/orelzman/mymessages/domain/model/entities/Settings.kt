package com.orelzman.mymessages.domain.model.entities

import androidx.annotation.StringRes
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.reflect.TypeToken
import com.orelzman.mymessages.R
import java.lang.reflect.Type
import java.util.*

@Entity
data class Settings(
    @PrimaryKey val key: SettingsKey,
    val value: String = key.defaultValue
)

enum class SettingsKey(
    val keyInServer: String,
    val type: SettingsType,
    val valueType: Type,
    val defaultValue: String,
    @StringRes val title: Int? = null
) {
    CallsUpdateAt(
        "calls_update_at",
        SettingsType.Data,
        Long::class.java,
        Date().time.toString(),
        R.string.last_calls_update
    ),
    IsDataInit(
        "is_data_init",
        SettingsType.NotVisibleToUser,
        Boolean::class.java,
        false.toString()
    ),
    ShowAppOnCall(
        "show_app_on_call",
        SettingsType.Toggle,
        Boolean::class.java,
        false.toString(),
        R.string.show_app_on_call
    ),
    IgnoredList(
        "ignored_list",
        SettingsType.PopUp,
        object : TypeToken<List<String>>() {}.type,
        emptyList<String>().toString(),
        R.string.ignore_list
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