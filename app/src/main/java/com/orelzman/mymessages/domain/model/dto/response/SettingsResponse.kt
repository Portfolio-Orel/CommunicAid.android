package com.orelzman.mymessages.domain.model.dto.response

import com.google.gson.annotations.SerializedName
import com.orelzman.mymessages.domain.model.entities.Settings
import com.orelzman.mymessages.domain.model.entities.SettingsKeys

data class SettingsResponse(
    @SerializedName("key") val key: String,
    @SerializedName("value") val value: String,
)

fun List<SettingsResponse>.toSettings(): List<Settings> {
    val array = ArrayList(this)
    val settings = ArrayList<Settings>()
    array.forEach {
        SettingsKeys.fromString(it.key)?.let { settingsKey ->
            settings.add((Settings(key = settingsKey, value = it.value)))
        }
    }
    return settings
}